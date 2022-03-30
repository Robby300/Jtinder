package com.jtinder.client.telegram.botapi.handlers;

import com.jtinder.client.domen.Sex;
import com.jtinder.client.domen.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.KeyboardService;
import com.jtinder.client.telegram.service.ReplyMessagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashSet;
import java.util.Set;


/**
 * Формирует анкету пользователя.
 */

@Slf4j
@Component
public class FillingProfileHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final KeyboardService keyboardService;

    public FillingProfileHandler(UserDataCache userDataCache,
                                 ReplyMessagesService messagesService, KeyboardService keyboardService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.keyboardService = keyboardService;
    }

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
    public BotApiMethod<?> handle(CallbackQuery callbackQuery) {
        String usersAnswer = callbackQuery.getData();
        long userId = callbackQuery.getMessage().getChatId();
        long chatId = callbackQuery.getMessage().getChatId();

        User user = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;

        if (botState.equals(BotState.ASK_NAME)) {
            user.getProfile().setSex(Sex.valueOf(usersAnswer));
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askName");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_DESCRIPTION);
        }

        if (botState.equals(BotState.PROFILE_FILLED)) {
            user.getProfile().setFindSex(new HashSet<>());
            user.getProfile().getFindSex().add(Sex.valueOf(usersAnswer));
            userDataCache.setUsersCurrentBotState(userId, BotState.MAIN_MENU);
            replyToUser = new SendMessage(String.valueOf(chatId), String.format("%s %s", "Данные по вашей анкете", user));
            replyToUser.enableMarkdown(true);
            replyToUser.setReplyMarkup(keyboardService.getMainMenuKeyboard());

        }

        return replyToUser;
    }
}
