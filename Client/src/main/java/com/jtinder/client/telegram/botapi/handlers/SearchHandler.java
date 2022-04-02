package com.jtinder.client.telegram.botapi.handlers;

import com.jtinder.client.domen.Profile;
import com.jtinder.client.domen.ScrollableListWrapper;
import com.jtinder.client.domen.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.ImageService;
import com.jtinder.client.telegram.service.KeyboardService;
import com.jtinder.client.telegram.service.ReplyMessagesService;
import com.jtinder.client.telegram.service.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
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
    public PartialBotApiMethod<?> handle(Message message) {
        return processUsersInput(message);
    }

    private PartialBotApiMethod<?> processUsersInput(Message message) {
        long userId = message.getChatId();
        long chatId = message.getChatId();
        User user = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        SendMessage replyToUser;
        SendPhoto sendPhoto = new SendPhoto();
        if (botState.equals(BotState.SEARCH)) {
            List<Profile> users = serverService.getValidProfilesToUser(user);
            log.info("Пришел список подходящих анкет с размером {}", users.size());
            log.info("Список: {}", users);
            if (users.size() == 0) {
                replyToUser = messagesService.getReplyMessage(chatId, "reply.noProfile");
                return replyToUser;
            }
            user.setScrollableListWrapper(new ScrollableListWrapper(users));
            sendPhoto.setChatId(String.valueOf(chatId));
            try {
                sendPhoto.setPhoto(new InputFile(imageService.getFile(user.getScrollableListWrapper().getCurrentProfile())));
                sendPhoto.setCaption(user.getScrollableListWrapper().getCurrentProfile().getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendPhoto.setReplyMarkup(keyboardService.getInlineKeyboardSearch());
        }
        return sendPhoto;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SEARCH;
    }

    @Override
    public PartialBotApiMethod<?> handle(CallbackQuery callbackQuery) {
        long userId = callbackQuery.getMessage().getChatId();
        long chatId = callbackQuery.getMessage().getChatId();
        SendMessage replyToUser;

        User user = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        EditMessageMedia editMessageMedia = new EditMessageMedia();

        if (botState.equals(BotState.SEARCH)) {

            if (callbackQuery.getData().equals("Лайк")) {
                serverService.likeProfile(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user);
                validateListIsEnd(user);
                if (user.getScrollableListWrapper().isEmpty()) {
                    replyToUser = messagesService.getReplyMessage(chatId, "reply.noProfiles");
                    return replyToUser;
                }
                try {
                    editMessageMedia = keyboardService.getEditMessageImage(chatId, callbackQuery, imageService.getFile(user.getScrollableListWrapper().getNextProfile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                editMessageMedia.setReplyMarkup(keyboardService.getInlineKeyboardSearch());
            }
            if (callbackQuery.getData().equals("Следующий")) {
                validateListIsEnd(user);
                try {
                    editMessageMedia = keyboardService.getEditMessageImage(chatId, callbackQuery, imageService.getFile(user.getScrollableListWrapper().getNextProfile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                editMessageMedia.setReplyMarkup(keyboardService.getInlineKeyboardSearch());
            }
        }
        return editMessageMedia;
    }

    private void validateListIsEnd(User user) { //переименовать
        if (user.getScrollableListWrapper().isLast()) {
            user.setScrollableListWrapper(new ScrollableListWrapper(serverService.getValidProfilesToUser(user)));
        }
    }

}
