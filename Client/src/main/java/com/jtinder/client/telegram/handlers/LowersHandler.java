package com.jtinder.client.telegram.handlers;

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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class LowersHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;
    private final ImageService imageService;

    public LowersHandler(UserDataCache userDataCache,
                         ReplyMessagesService messagesService, KeyboardService keyboardService, ServerService serverService, ImageService imageService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.keyboardService = keyboardService;
        this.serverService = serverService;
        this.imageService = imageService;
    }


    @Override
    public BotState getHandlerName() {
        return BotState.LOWERS;
    }

    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        List<PartialBotApiMethod<?>> answerList = new ArrayList<>();

        long chatId = callbackQuery.getMessage().getChatId();
        User user = userDataCache.getUserProfileData(chatId);
        answerList.add(new DeleteMessage(String.valueOf(chatId), callbackQuery.getMessage().getMessageId()));

        SendMessage replyToUser;
        SendPhoto profilePhoto = new SendPhoto();

        if (callbackQuery.getData().equals("ЛЮБИМЦЫ")) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.LOWERS);
            List<Profile> users = serverService.getLowersProfilesToUser(user);
            log.info("Пришел список подходящих анкет с размером {}", users.size());
            log.info("Список: {}", users);

            if (users.size() == 0) {
                replyToUser = messagesService.getReplyMessage(chatId, "reply.noProfile");
                answerList.add(replyToUser);
                replyToUser.setReplyMarkup(keyboardService.getInlineMainMenu());
                userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
                return answerList;
            }

            user.setScrollableListWrapper(new ScrollableListWrapper(users));
            profilePhoto.setChatId(String.valueOf(chatId));
            try {
                profilePhoto.setPhoto(new InputFile(imageService.getFile(user.getScrollableListWrapper().getCurrentProfile())));
                profilePhoto.setCaption(serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user));
            } catch (IOException e) {
                e.printStackTrace();
            }
            profilePhoto.setReplyMarkup(keyboardService.getInlineKeyboardLowers());
            answerList.add(profilePhoto);
            answerList.add(new DeleteMessage(String.valueOf(chatId), callbackQuery.getMessage().getMessageId()));
        }

        if (callbackQuery.getData().equals("Следующий")) {
            profilePhoto.setChatId(String.valueOf(chatId));
            if (user.getScrollableListWrapper().isLast()) {
                user.getScrollableListWrapper().resetCurrentIndex();
                try {
                    profilePhoto.setPhoto(new InputFile(imageService.getFile(user.getScrollableListWrapper().getCurrentProfile())));
                    profilePhoto.setReplyMarkup(keyboardService.getInlineKeyboardLowers());
                    profilePhoto.setCaption(serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                answerList.add(profilePhoto);
                return answerList;
            }

            try {
                profilePhoto.setPhoto(new InputFile(imageService.getFile(user.getScrollableListWrapper().getNextProfile())));
                profilePhoto.setCaption(serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user));
                profilePhoto.setReplyMarkup(keyboardService.getInlineKeyboardLowers());
            } catch (IOException e) {
                e.printStackTrace();
            }

            answerList.add(profilePhoto);
        }

        if (callbackQuery.getData().equals("Предыдущий")) {
            profilePhoto.setChatId(String.valueOf(chatId));
            if (user.getScrollableListWrapper().isFirst()) {
                user.getScrollableListWrapper().resetCurrentIndexFromLast();
                try {
                    profilePhoto.setPhoto(new InputFile(imageService.getFile(user.getScrollableListWrapper().getCurrentProfile())));
                    profilePhoto.setChatId(String.valueOf(chatId));
                    profilePhoto.setReplyMarkup(keyboardService.getInlineKeyboardLowers());
                    profilePhoto.setCaption(serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                answerList.add(profilePhoto);
                return answerList;
            }

            try {
                profilePhoto.setPhoto(new InputFile(imageService.getFile(user.getScrollableListWrapper().getPreviousProfile())));
                profilePhoto.setCaption(serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user));
                profilePhoto.setReplyMarkup(keyboardService.getInlineKeyboardLowers());
            } catch (IOException e) {
                e.printStackTrace();
            }

            answerList.add(profilePhoto);
        }

        if (callbackQuery.getData().equals("MENUL")) {
            replyToUser = new SendMessage();
            replyToUser.setReplyMarkup(keyboardService.getInlineMainMenu());
            replyToUser.setChatId(String.valueOf(chatId));
            replyToUser.setText("МЕНЮ");
            answerList.add(replyToUser);
            userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
        }
        return answerList;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        return null;
    }


}
