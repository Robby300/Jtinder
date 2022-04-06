package com.jtinder.client.telegram.handlers;

import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.BotMethodService;
import com.jtinder.client.telegram.service.KeyboardService;
import com.jtinder.client.telegram.service.ServerService;
import com.jtinder.client.telegram.service.TextMessagesService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Component
public class AuthenticateHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final TextMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;
    private final BotMethodService botMethodService;

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        String usersAnswer = message.getText();
        long chatId = message.getChatId();

        if (usersAnswer.equals("/start")) {
            return Collections.singletonList(botMethodService.getSendMessage(chatId,
                    messagesService.getText("reply.welcome"),
                    keyboardService.getAuthenticateKeyboard()));
        }

        if (message.getText().equals(messagesService.getText("button.registration"))) {
            boolean isRegister = serverService.isRegistered(chatId);
            if (isRegister) {
                return Collections.singletonList(botMethodService.getSendMessage(chatId,
                        messagesService.getText("reply.registeredYet"),
                        keyboardService.getAuthenticateKeyboard()));
            }
            userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_SEX);
            return Collections.singletonList(botMethodService.getSendMessage(chatId,
                    messagesService.getText("reply.askPassword")));
        }

        if (message.getText().equals(messagesService.getText("button.login"))) {
            boolean isRegister = serverService.isRegistered(chatId);
            if (!isRegister) {
                return Collections.singletonList(botMethodService.getSendMessage(chatId,
                        messagesService.getText("reply.notRegistered"),
                        keyboardService.getAuthenticateKeyboard()));
            } else {
                userDataCache.setUsersCurrentBotState(chatId, BotState.LOGIN);
                return Collections.singletonList(botMethodService.getSendMessage(chatId,
                        messagesService.getText("reply.askPassword")));
            }
        }
        return Collections.emptyList();
    }

    @Override
    public BotState getHandlerName() {
        return BotState.AUTHENTICATE;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        return Collections.emptyList();
    }
}
