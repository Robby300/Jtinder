package com.jtinder.client.telegram.handlers;

import com.jtinder.client.domain.AuthenticUser;
import com.jtinder.client.domain.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.BotMethodService;
import com.jtinder.client.telegram.service.KeyboardService;
import com.jtinder.client.telegram.service.ServerService;
import com.jtinder.client.telegram.service.TextMessagesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class LoginHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final TextMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;
    private final BotMethodService botMethodService;


    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        long chatId = message.getChatId();
        DeleteMessage deleteMessage = botMethodService.getDeleteMessage(chatId, message.getMessageId());

        User user = userDataCache.getUserProfileData(chatId);
        user.setToken(serverService.loginUser(new AuthenticUser(chatId, message.getText())));

        if(user.getToken().isEmpty()) {
            return List.of(deleteMessage, botMethodService.getSendMessage(chatId,
                    messagesService.getText("reply.wrongPassword"),
                    keyboardService.getAuthenticateKeyboard()));
        }

        user.setProfile(serverService.getLoginUserProfile(user));
        userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
        return List.of(botMethodService.getDeleteMessage(chatId, message.getMessageId()),
                botMethodService.getSendMessage(
                        chatId,
                        messagesService.getText("reply.menu"),
                        keyboardService.getInlineMainMenu()));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.LOGIN;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        return Collections.singletonList(botMethodService.getDeleteMessage(chatId, callbackQuery.getMessage().getMessageId()));
    }
}
