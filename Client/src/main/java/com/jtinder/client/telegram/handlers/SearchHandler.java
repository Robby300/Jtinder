package com.jtinder.client.telegram.handlers;

import com.jtinder.client.domain.Profile;
import com.jtinder.client.domain.ScrollableListWrapper;
import com.jtinder.client.domain.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.Collections;
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
    private final BotMethodService botMethodService;

    @Override
    public BotState getHandlerName() {
        return BotState.SEARCH;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        return Collections.emptyList();
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        List<PartialBotApiMethod<?>> answerList = new ArrayList<>();

        long chatId = message.getChatId();
        User user = userDataCache.getUserProfileData(chatId);

        SendMessage replyToUser;
        SendPhoto profilePhoto = new SendPhoto();

        if (message.getText().equals(messagesService.getText("button.search"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.SEARCH);
            List<Profile> users = serverService.getValidProfilesToUser(user);
            log.info("Пришел список подходящих анкет с размером {}", users.size());
            log.info("Список: {}", users);

            if (users.size() == 0) {
                replyToUser = messagesService.getReplyMessage(chatId, "reply.noProfile");
                replyToUser.setReplyMarkup(keyboardService.getMainMenu());
                answerList.add(replyToUser);
                return answerList;
            }

            user.setScrollableListWrapper(new ScrollableListWrapper(users));
            profilePhoto.setChatId(String.valueOf(chatId));

            profilePhoto.setPhoto(new InputFile(imageService.getFile(user.getScrollableListWrapper().getCurrentProfile())));
            profilePhoto.setCaption(serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user));

            profilePhoto.setReplyMarkup(keyboardService.getKeyboardSearch());
            answerList.add(profilePhoto);
        }

        if (message.getText().equals(messagesService.getText("button.like")) || message.getText().equals(messagesService.getText("button.next"))) {
            if (message.getText().equals(messagesService.getText("button.like"))) {
                serverService.likeProfile(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user);

                if(serverService.weLove(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)) {
                    answerList.add(botMethodService.getSendMessage(chatId, messagesService.getText("button.reciprocity")));
                }
            }
            if (user.getScrollableListWrapper().isLast()) {
                user.setScrollableListWrapper(new ScrollableListWrapper(serverService.getValidProfilesToUser(user)));
                if (user.getScrollableListWrapper().isEmpty()) {
                    replyToUser = messagesService.getReplyMessage(chatId, "reply.noProfile");
                    replyToUser.setReplyMarkup(keyboardService.getMainMenu());
                    userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
                    answerList.add(replyToUser);
                    return answerList;
                }
                profilePhoto.setPhoto(new InputFile(imageService.getFile(user.getScrollableListWrapper().getCurrentProfile())));
                profilePhoto.setChatId(String.valueOf(chatId));
                profilePhoto.setReplyMarkup(keyboardService.getKeyboardSearch());
                profilePhoto.setCaption(serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user));

                answerList.add(profilePhoto);
                return answerList;
            }

            profilePhoto.setPhoto(new InputFile(imageService.getFile(user.getScrollableListWrapper().getNextProfile())));
            profilePhoto.setChatId(String.valueOf(chatId));
            profilePhoto.setReplyMarkup(keyboardService.getKeyboardSearch());
            profilePhoto.setCaption(serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user));


            answerList.add(profilePhoto);
        }

        if (message.getText().equals(messagesService.getText("button.menu"))) {
            replyToUser = new SendMessage();
            replyToUser.setReplyMarkup(keyboardService.getMainMenu());
            replyToUser.setChatId(String.valueOf(chatId));
            replyToUser.setText(messagesService.getText("button.menu"));
            answerList.add(replyToUser);
            userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
        }
        return answerList;
    }


}
