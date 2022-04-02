package com.jtinder.client.telegram.botapi.handlers;

import com.jtinder.client.domen.AuthenticUser;
import com.jtinder.client.domen.Sex;
import com.jtinder.client.domen.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.ImageService;
import com.jtinder.client.telegram.service.KeyboardService;
import com.jtinder.client.telegram.service.ReplyMessagesService;
import com.jtinder.client.telegram.service.ServerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.util.HashSet;


/**
 * Формирует анкету пользователя.
 */

@Slf4j
@AllArgsConstructor
@Component
public class FillingProfileHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;
    private final ImageService imageService;

    @Override
    public BotApiMethod<?> handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_PROFILE)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_NAME);
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_PROFILE;
    }

    private BotApiMethod<?> processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        long userId = inputMsg.getChatId();
        long chatId = inputMsg.getChatId();

        User user = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;

        if (botState.equals(BotState.ASK_SEX)) {
            user.getProfile().setPassword(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askSex");
            replyToUser.setReplyMarkup(keyboardService.getInlineKeyboardSex());
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NAME);
        }

        if (botState.equals(BotState.ASK_NAME)) {
            user.getProfile().setSex(Sex.valueOf(usersAnswer));
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askName");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_DESCRIPTION);
        }

        if (botState.equals(BotState.ASK_DESCRIPTION)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askDescription");
            user.getProfile().setName(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_FIND);
        }

        if (botState.equals(BotState.ASK_FIND)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askFindSex");
            replyToUser.setReplyMarkup(keyboardService.getInlineKeyboardFindSex());
            user.getProfile().setDescription(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.PROFILE_FILLED);
        }


        userDataCache.saveUserProfileData(userId, user);

        return replyToUser;
    }

    @Override
    public PartialBotApiMethod<?> handle(CallbackQuery callbackQuery) {
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
            user.getProfile().getFindSex().add(Sex.valueOf(usersAnswer));

            profilePhoto.setReplyMarkup(keyboardService.getMainMenuKeyboard());
            serverService.registerUser(user.getProfile());
            user.setToken(serverService.loginUser(new AuthenticUser(user.getProfile().getUserId(), user.getProfile().getPassword())));
            profilePhoto.setChatId(String.valueOf(chatId));
            profilePhoto.setCaption(user.getProfile().getName());
            try {
                profilePhoto.setPhoto(new InputFile(imageService.getFile(user.getProfile())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return profilePhoto;
        }
        return replyToUser;
    }
}
