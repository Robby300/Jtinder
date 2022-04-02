package com.jtinder.client.telegram.botapi.handlers;

import com.jtinder.client.domen.Profile;
import com.jtinder.client.domen.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.ImageService;
import com.jtinder.client.telegram.service.KeyboardService;
import com.jtinder.client.telegram.service.ReplyMessagesService;
import com.jtinder.client.telegram.service.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class SearchHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;
    private final ImageService imageService;

    public SearchHandler(UserDataCache userDataCache,
                         ReplyMessagesService messagesService, KeyboardService keyboardService, ServerService serverService, ImageService imageService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.keyboardService = keyboardService;
        this.serverService = serverService;
        this.imageService = imageService;
    }

    @Override
    public BotApiMethod<?> handle(Message message) {
        return processUsersInput(message);
    }

    private BotApiMethod<?> processUsersInput(Message message) {
        long userId = message.getChatId();
        long chatId = message.getChatId();
        User user = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        SendMessage replyToUser = new SendMessage();
        SendPhoto sendPhoto = new SendPhoto();

        if (botState.equals(BotState.SEARCH)) {
            List<Profile> users = serverService.getValidProfilesToUser(user);
            log.info("Пришел список подходящих анкет с размером {}", users.size());
            log.info("Список: {}", users);
            user.setProfileList(users);
            user.setPage(0);
            replyToUser.setChatId(String.valueOf(chatId));
            try {
                sendPhoto.setPhoto(new InputFile(imageService.getFile(user.getProfileList().get(user.getPage()))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            replyToUser.setReplyMarkup(keyboardService.getInlineKeyboardSearch());
        }
        return replyToUser;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SEARCH;
    }

    @Override
    public BotApiMethod<?> handle(CallbackQuery callbackQuery) {
        long userId = callbackQuery.getMessage().getChatId();
        long chatId = callbackQuery.getMessage().getChatId();

        User user = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        EditMessageText editMessageText = new EditMessageText();

        if (botState.equals(BotState.SEARCH)) {
            if (callbackQuery.getData().equals("Лайк")) {
                int page = user.getPage();
                user.setPage(++page);
                editMessageText = keyboardService.getEditMessageText(chatId, callbackQuery, user.getProfileList().get(user.getPage()).toString());
                editMessageText.setReplyMarkup(keyboardService.getInlineKeyboardSearch());
            }
            if (callbackQuery.getData().equals("Следующий")) {
                int page = user.getPage();
                user.setPage(++page);
                editMessageText = keyboardService.getEditMessageText(chatId, callbackQuery, user.getProfileList().get(user.getPage()).toString());
                editMessageText.setReplyMarkup(keyboardService.getInlineKeyboardSearch());
            }
        }
        return editMessageText;
    }

}
