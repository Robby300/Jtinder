package com.jtinder.client.telegram.handlers;

import com.jtinder.client.domain.Sex;
import com.jtinder.client.domain.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
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

        if (message.getText().equals(messagesService.getText("button.editSex"))) {
            DeleteMessage deleteMessage = botMethodService.getDeleteMessage(chatId, message.getMessageId());
            return List.of(deleteMessage, botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askSex"),
                    keyboardService.getInlineKeyboardSex()));
        }

        return Collections.singletonList(botMethodService.getDeleteMessage(chatId, message.getMessageId()));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.EDIT;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        String usersAnswer = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        DeleteMessage deleteMessage = botMethodService.getDeleteMessage(chatId, callbackQuery.getMessage().getMessageId());

        User user = userDataCache.getUserProfileData(chatId);
        user.getProfile().setSex(Sex.valueOf(usersAnswer));
        serverService.changeSex(user.getProfile().getSex(), user);
        userDataCache.setUsersCurrentBotState(chatId, BotState.PROFILE);
        return List.of(deleteMessage, (botMethodService.getSendPhoto(chatId,
                imageService.getFile(user.getProfile()),
                keyboardService.getProfileMenu(),user.getProfile().getSex().getName() + ", " +
                        user.getProfile().getName())));
    }

}

