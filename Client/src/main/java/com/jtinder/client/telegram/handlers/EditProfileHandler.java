package com.jtinder.client.telegram.handlers;

import com.jtinder.client.domain.Sex;
import com.jtinder.client.domain.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Component
public class EditProfileHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final TextMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;
    private final ImageService imageService;
    private final BotMethodService botMethodService;

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        long chatId = message.getChatId();
        User user = userDataCache.getUserProfileData(chatId);
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);

        if (botState.equals(BotState.EDIT_NAME)) {
            return getEditName(chatId, user, message.getText());
        }

        if (botState.equals(BotState.EDIT_DESCRIPTION)) {
            return getEditDescription(chatId, user, message.getText());
        }

        if (message.getText().equals(messagesService.getText("button.editSex"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.EDIT_SEX);
            return List.of(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askSex"),
                    keyboardService.getInlineKeyboardSex()));
        }

        if (message.getText().equals(messagesService.getText("button.editName"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.EDIT_NAME);
            return List.of(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askName")));
        }

        if (message.getText().equals(messagesService.getText("button.editDescription"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.EDIT_DESCRIPTION);
            return List.of(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askDescription")));
        }

        if (message.getText().equals(messagesService.getText("button.editFindSex"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.EDIT_FIND);
            return List.of(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askFindSex"),
                    keyboardService.getInlineKeyboardFindSex()));
        }
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<?>> getEditDescription(long chatId, User user, String text) {
        user.getProfile().setDescription(text);
        serverService.updateCurrentUser(user.getProfile(), user);
        userDataCache.setUsersCurrentBotState(chatId, BotState.PROFILE);
        return List.of(botMethodService.getSendPhoto(chatId,
                imageService.getFile(user.getProfile()),
                keyboardService.getProfileMenu(), user.getProfile().getSex().getName() + ", " +
                        user.getProfile().getName()), botMethodService.getSendMessage(
                chatId,
                messagesService.getText("reply.editDescription")));
    }


    @Override
    public BotState getHandlerName() {
        return BotState.EDIT;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        String usersAnswer = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        User user = userDataCache.getUserProfileData(chatId);

        if (usersAnswer.equals(Sex.MALE.toString()) || usersAnswer.equals(Sex.FEMALE.toString())) {
            return this.getEditSex(chatId, user, usersAnswer);
        }

        if (callbackQuery.getData().equals(messagesService.getText("button.maleedit")) ||
                callbackQuery.getData().equals(messagesService.getText("button.femaleedit")) ||
                callbackQuery.getData().equals(messagesService.getText("button.alledit"))) {
            return getEditFindSex(chatId, user, usersAnswer);
        }
        return Collections.emptyList();
    }




    private List<PartialBotApiMethod<?>> getEditSex(long chatId, User user, String usersAnswer) {
        user.getProfile().setSex(Sex.valueOf(usersAnswer));
        serverService.updateCurrentUser(user.getProfile(), user);
        userDataCache.setUsersCurrentBotState(chatId, BotState.PROFILE);
        return List.of(botMethodService.getSendPhoto(chatId,
                imageService.getFile(user.getProfile()),
                keyboardService.getProfileMenu(), user.getProfile().getSex().getName() + ", " +
                        user.getProfile().getName()), botMethodService.getSendMessage(
                chatId,
                messagesService.getText("reply.editSex")));
    }

    private List<PartialBotApiMethod<?>> getEditName(long chatId, User user, String usersAnswer) {
        user.getProfile().setName(usersAnswer);
        serverService.updateCurrentUser(user.getProfile(), user);
        userDataCache.setUsersCurrentBotState(chatId, BotState.PROFILE);
        return List.of(botMethodService.getSendPhoto(chatId,
                imageService.getFile(user.getProfile()),
                keyboardService.getProfileMenu(), user.getProfile().getSex().getName() + ", " +
                        user.getProfile().getName()), botMethodService.getSendMessage(
                chatId,
                messagesService.getText("reply.editName")));
    }



    private List<PartialBotApiMethod<?>> getEditFindSex(Long chatId, User user, String usersAnswer) {
        switch (usersAnswer) {
            case "MALE_EDIT":
                user.getProfile().getFindSex().add(Sex.MALE);
                break;
            case "FEMALE_EDIT":
                user.getProfile().getFindSex().add(Sex.FEMALE);
                break;
            case "ALL_EDIT":
                user.getProfile().getFindSex().add(Sex.MALE);
                user.getProfile().getFindSex().add(Sex.FEMALE);
                break;
        }
        serverService.updateCurrentUser(user.getProfile(), user);
        return List.of(botMethodService.getSendPhoto(chatId,
                imageService.getFile(user.getProfile()),
                keyboardService.getProfileMenu(), user.getProfile().getSex().getName() + ", " +
                        user.getProfile().getName()), botMethodService.getSendMessage(
                chatId,
                messagesService.getText("reply.editFindSex")));
    }

}

