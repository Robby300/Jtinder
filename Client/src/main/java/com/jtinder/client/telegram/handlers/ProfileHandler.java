package com.jtinder.client.telegram.handlers;

import com.jtinder.client.domain.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.BotMethodService;
import com.jtinder.client.telegram.service.ImageService;
import com.jtinder.client.telegram.service.KeyboardService;
import com.jtinder.client.telegram.service.TextMessagesService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class ProfileHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final TextMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ImageService imageService;
    private final BotMethodService botMethodService;

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        long chatId = message.getChatId();
        User user = userDataCache.getUserProfileData(chatId);
        userDataCache.setUsersCurrentBotState(chatId, BotState.PROFILE);

        if (message.getText().equals(messagesService.getText("button.profile"))) {
            return getProfile(message, chatId, user);
        }

        if (message.getText().equals(messagesService.getText("button.edit"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.EDIT);
            return Collections.singletonList(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.edit"),
                    keyboardService.getProfileEditMenu()));
        }

        if (message.getText().equals(messagesService.getText("button.menu"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
            return Collections.singletonList(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.menu"),
                    keyboardService.getMainKeyboard()));
        }
        return Collections.emptyList();
    }

    @Override
    public BotState getHandlerName() {
        return BotState.PROFILE;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<?>> getProfile(Message message, Long chatId, User user) {

        return Collections.singletonList(botMethodService.getSendPhoto(
                chatId,
                imageService.getFile(user.getProfile()),
                keyboardService.getProfileMenu(), user.getProfile().getSex().getName() + ", " +
                        user.getProfile().getName()));
    }
}
