package com.jtinder.client.telegram.handlers;

import com.jtinder.client.domen.Profile;
import com.jtinder.client.domen.ScrollableListWrapper;
import com.jtinder.client.domen.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.ImageService;
import com.jtinder.client.telegram.service.KeyboardService;
import com.jtinder.client.telegram.service.TextMessagesService;
import com.jtinder.client.telegram.service.ServerService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Slf4j
public class SearchHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final TextMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;
    private final ImageService imageService;

    @Override
    public BotState getHandlerName() {
        return BotState.SEARCH;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        List<PartialBotApiMethod<?>> answerList = new ArrayList<>();

        long userId = callbackQuery.getMessage().getFrom().getId();
        long chatId = callbackQuery.getMessage().getChatId();
        User user = userDataCache.getUserProfileData(chatId);
        answerList.add(new DeleteMessage(String.valueOf(chatId), callbackQuery.getMessage().getMessageId()));

        SendMessage replyToUser;
        SendPhoto profilePhoto = new SendPhoto();

        if (callbackQuery.getData().equals("ПОИСК")) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.SEARCH);
            List<Profile> users = serverService.getValidProfilesToUser(user);
            log.info("Пришел список подходящих анкет с размером {}", users.size());
            log.info("Список: {}", users);

            if (users.size() == 0) {
                replyToUser = messagesService.getReplyMessage(chatId, "reply.noProfile");
                replyToUser.setReplyMarkup(keyboardService.getInlineMainMenu());
                answerList.add(replyToUser);
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
            profilePhoto.setReplyMarkup(keyboardService.getInlineKeyboardSearch());
            answerList.add(profilePhoto);
            answerList.add(new DeleteMessage(String.valueOf(chatId), callbackQuery.getMessage().getMessageId()));
        }

        if (callbackQuery.getData().equals("Лайк") || callbackQuery.getData().equals("Следующий")) {
            if (callbackQuery.getData().equals("Лайк")) {
                serverService.likeProfile(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user);
            }
            if (user.getScrollableListWrapper().isLast()) {
                user.setScrollableListWrapper(new ScrollableListWrapper(serverService.getValidProfilesToUser(user)));
                if (user.getScrollableListWrapper().isEmpty()) {
                    replyToUser = messagesService.getReplyMessage(chatId, "reply.noProfile");
                    replyToUser.setReplyMarkup(keyboardService.getInlineMainMenu());
                    answerList.add(replyToUser);
                    return answerList;
                }
                try {
                    profilePhoto.setPhoto(new InputFile(imageService.getFile(user.getScrollableListWrapper().getCurrentProfile())));
                    profilePhoto.setChatId(String.valueOf(chatId));
                    profilePhoto.setReplyMarkup(keyboardService.getInlineKeyboardSearch());
                    profilePhoto.setCaption(serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                answerList.add(profilePhoto);
                return answerList;
            }



            try {
                profilePhoto.setPhoto(new InputFile(imageService.getFile(user.getScrollableListWrapper().getNextProfile())));
                profilePhoto.setChatId(String.valueOf(chatId));
                profilePhoto.setReplyMarkup(keyboardService.getInlineKeyboardSearch());
                profilePhoto.setCaption(serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user));
            } catch (IOException e) {
                e.printStackTrace();
            }

            answerList.add(profilePhoto);
        }

        if (callbackQuery.getData().equals("MENU")) {
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