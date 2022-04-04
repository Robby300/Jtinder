package com.jtinder.client.telegram.handlers;

import com.jtinder.client.domain.AuthenticUser;
import com.jtinder.client.domain.Sex;
import com.jtinder.client.domain.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


/**
 * Формирует анкету пользователя.
 */

@AllArgsConstructor
@Component
public class FillingProfileHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final TextMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;
    private final ImageService imageService;
    private final BotMethodService botMethodService;

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_PROFILE)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_NAME);
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_PROFILE;
    }

    private List<PartialBotApiMethod<?>> processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        long chatId = inputMsg.getChatId();
        DeleteMessage deleteMessage = botMethodService.getDeleteMessage(chatId, inputMsg.getMessageId());

        User user = userDataCache.getUserProfileData(chatId);
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);

        if (botState.equals(BotState.ASK_SEX)) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_NAME);
            user.getProfile().setPassword(usersAnswer);
            return List.of(deleteMessage, botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askSex"),
                    keyboardService.getInlineKeyboardSex()));
        }

        if (botState.equals(BotState.ASK_NAME)) {
            user.getProfile().setSex(Sex.valueOf(usersAnswer));
            userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_DESCRIPTION);
            return List.of(deleteMessage, botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askName")));
        }

        if (botState.equals(BotState.ASK_DESCRIPTION)) {
            user.getProfile().setName(usersAnswer);
            userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_FIND);
            return List.of(deleteMessage, botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askDescription")));
        }

        if (botState.equals(BotState.ASK_FIND)) {
            user.getProfile().setDescription(usersAnswer);
            userDataCache.setUsersCurrentBotState(chatId, BotState.PROFILE_FILLED);
            return List.of(deleteMessage, botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askFindSex"),
                    keyboardService.getInlineKeyboardFindSex()));
        }

        return Collections.singletonList(deleteMessage);
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        List<PartialBotApiMethod<?>> answerList = new ArrayList<>();
        String usersAnswer = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();

        User user = userDataCache.getUserProfileData(chatId);
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);

        SendMessage replyToUser = null;
        SendPhoto profilePhoto = new SendPhoto();

        if (botState.equals(BotState.ASK_NAME)) {
            user.getProfile().setSex(Sex.valueOf(usersAnswer));
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askName");
            userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_DESCRIPTION);
        }

        if (botState.equals(BotState.PROFILE_FILLED)) {
            user.getProfile().setFindSex(new HashSet<>());
            if (usersAnswer.equals("ВСЕХ")) {
                user.getProfile().getFindSex().add(Sex.MALE);
                user.getProfile().getFindSex().add(Sex.FEMALE);
            } else {
                user.getProfile().getFindSex().add(Sex.valueOf(usersAnswer));
            }

            profilePhoto.setReplyMarkup(keyboardService.getInlineMainMenu());
            serverService.registerUser(user.getProfile());
            user.setToken(serverService.loginUser(new AuthenticUser(user.getProfile().getUserId(), user.getProfile().getPassword())));
            profilePhoto.setChatId(String.valueOf(chatId));
            profilePhoto.setCaption(user.getProfile().getName());

            profilePhoto.setPhoto(new InputFile(imageService.getFile(user.getProfile())));

            answerList.add(profilePhoto);
            answerList.add(new DeleteMessage(String.valueOf(chatId), callbackQuery.getMessage().getMessageId()));
            userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
            return answerList;
        }

        answerList.add(replyToUser);
        answerList.add(new DeleteMessage(String.valueOf(chatId), callbackQuery.getMessage().getMessageId()));
        return answerList;
    }
}
