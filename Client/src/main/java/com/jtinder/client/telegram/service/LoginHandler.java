package com.jtinder.client.telegram.service;

import com.jtinder.client.domen.AuthenticUser;
import com.jtinder.client.domen.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.botapi.handlers.InputMessageHandler;
import com.jtinder.client.telegram.cache.UserDataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@Slf4j
public class LoginHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;

    public LoginHandler(UserDataCache userDataCache,
                        ReplyMessagesService messagesService, KeyboardService keyboardService, ServerService serverService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.keyboardService = keyboardService;
        this.serverService = serverService;
    }

    @Override
    public BotApiMethod<?> handle(Message message) {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();

        User user = userDataCache.getUserProfileData(userId);

        SendMessage replyToUser = new SendMessage();
        replyToUser.enableMarkdown(true);
        replyToUser.setReplyMarkup(keyboardService.getMainMenuKeyboard());
        replyToUser.setChatId(String.valueOf(chatId));
        user.setToken(serverService.loginUser(new AuthenticUser(userId, message.getText())));
        user.setProfile(serverService.getLoginUserProfile(user));
        replyToUser.setText("Добро пожаловать, уебок " + user.getProfile().getName());
        return replyToUser;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.LOGIN;
    }

    @Override
    public BotApiMethod<?> handle(CallbackQuery callbackQuery) {
        return null;
    }
}
