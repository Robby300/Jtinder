package com.jtinder.client.telegram.handlers;

import com.jtinder.client.domain.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class ProfileHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final KeyboardService keyboardService;
    private final ImageService imageService;
    private final BotMethodService botMethodService;


    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        long chatId = message.getChatId();
        return Collections.singletonList(botMethodService.getDeleteMessage(chatId, message.getMessageId()));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.PROFILE;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        List<PartialBotApiMethod<?>> answerList = new ArrayList<>();

        long chatId = callbackQuery.getMessage().getChatId();
        answerList.add(new DeleteMessage(String.valueOf(chatId), callbackQuery.getMessage().getMessageId()));

        User user = userDataCache.getUserProfileData(chatId);
        SendPhoto profilePhoto = new SendPhoto();
        profilePhoto.setChatId(String.valueOf(chatId));
        profilePhoto.setReplyMarkup(keyboardService.getInlineMainMenu());
        profilePhoto.setCaption(user.getProfile().getName());

        profilePhoto.setPhoto(new InputFile(imageService.getFile(user.getProfile())));

        answerList.add(profilePhoto);
        return answerList;
    }
}
