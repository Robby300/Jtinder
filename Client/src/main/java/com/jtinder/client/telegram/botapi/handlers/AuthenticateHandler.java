package com.jtinder.client.telegram.botapi.handlers;

import com.jtinder.client.domen.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.KeyboardService;
import com.jtinder.client.telegram.service.ReplyMessagesService;
import com.jtinder.client.telegram.service.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Component
public class AuthenticateHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;

    public AuthenticateHandler(UserDataCache userDataCache,
                               ReplyMessagesService messagesService, KeyboardService keyboardService, ServerService serverService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.keyboardService = keyboardService;
        this.serverService = serverService;
    }

    @Override
    public BotApiMethod<?> handle(Message message) {
        String usersAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();

        User user = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = new SendMessage();
        if (usersAnswer.equals("/start")) {
            replyToUser.setChatId(String.valueOf(chatId));
            replyToUser.setText("Зарегистрируйтесь или войдите");
            replyToUser.enableMarkdown(true);
            replyToUser.setReplyMarkup(keyboardService.getAuthenticateKeyboard());
        }

        if (message.getText().equals("Регистрація")) {
            boolean isRegister = serverService.isRegistered(userId);
            if (isRegister) {
                replyToUser.setChatId(String.valueOf(chatId));
                replyToUser.setText("Вы уже зарегистрированы, войдите с паролем");
                replyToUser.enableMarkdown(true);
                replyToUser.setReplyMarkup(keyboardService.getAuthenticateKeyboard());
            }

            userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_SEX);
            System.out.println(userDataCache.getUsersCurrentBotState(chatId));
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askPassword");
        }

        if (message.getText().equals("Внити")) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.LOGIN);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askPassword");
        }


        return replyToUser;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.AUTHENTICATE;
    }

    @Override
    public BotApiMethod<?> handle(CallbackQuery callbackQuery) {
        return null;
    }
}
