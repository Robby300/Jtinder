package com.jtinder.client.telegram.botapi.handlers;

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

@Component
@Slf4j
public class MainMenuHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final KeyboardService keyboardService;

    public MainMenuHandler(UserDataCache userDataCache,
                                 ReplyMessagesService messagesService, KeyboardService keyboardService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.keyboardService = keyboardService;
    }

    @Override
    public BotApiMethod<?> handle(Message message) {
        return processUsersInput(message);
    }

    private BotApiMethod<?> processUsersInput(Message message) {
        String usersAnswer = message.getText();
        long userId = message.getChatId();
        String chatId = message.getChatId().toString();

        UserProfileData profileData = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = new SendMessage();
        replyToUser.setChatId(chatId);
        replyToUser.setText("Заебал");
        return replyToUser;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.MAIN_MENU;
    }

    @Override
    public BotApiMethod<?> handle(CallbackQuery callbackQuery) {
        return null;
    }
}
