package com.jtinder.client.telegram.handlers;

import com.jtinder.client.domain.Profile;
import com.jtinder.client.domain.ScrollableListWrapper;
import com.jtinder.client.domain.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class LoversHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final TextMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;
    private final ImageService imageService;
    private final BotMethodService botMethodService;

    @Override
    public BotState getHandlerName() {
        return BotState.LOVERS;
    }

    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        return Collections.emptyList();
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        long chatId = message.getChatId();
        User user = userDataCache.getUserProfileData(chatId);

        if (message.getText().equals(messagesService.getText("button.lovers"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.LOVERS);
            List<Profile> users = serverService.getLowersProfilesToUser(user);
            log.info("Пришел список подходящих анкет с размером {}", users.size());
            log.info("Список: {}", users);

            if (users.size() == 0) {
                userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
                return Collections.singletonList(botMethodService.getSendMessage(chatId,
                        messagesService.getText("reply.noProfile"),
                        keyboardService.getMainMenu()));
            }

            user.setScrollableListWrapper(new ScrollableListWrapper(users));

            return Collections.singletonList(botMethodService.getSendPhoto(
                    chatId,
                    imageService.getFile(user.getScrollableListWrapper().getCurrentProfile()),
                    keyboardService.getKeyboardLowers(),
                    serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)));
        }

        if (message.getText().equals(messagesService.getText("button.next"))) {
            if (user.getScrollableListWrapper().isLast()) {
                user.getScrollableListWrapper().resetCurrentIndex();

                return Collections.singletonList(botMethodService.getSendPhoto(
                        chatId,
                        imageService.getFile(user.getScrollableListWrapper().getCurrentProfile()),
                        keyboardService.getKeyboardLowers(),
                        serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)));
            }

            return Collections.singletonList(botMethodService.getSendPhoto(
                    chatId,
                    imageService.getFile(user.getScrollableListWrapper().getNextProfile()),
                    keyboardService.getKeyboardLowers(),
                    serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)));
        }

        if (message.getText().equals(messagesService.getText("button.prev"))) {
            if (user.getScrollableListWrapper().isFirst()) {
                user.getScrollableListWrapper().resetCurrentIndexFromLast();

                return Collections.singletonList(botMethodService.getSendPhoto(chatId,
                        imageService.getFile(user.getScrollableListWrapper().getCurrentProfile()),
                        keyboardService.getKeyboardLowers(),
                        serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)));
            }

            return Collections.singletonList(botMethodService.getSendPhoto(chatId,
                    imageService.getFile(user.getScrollableListWrapper().getPreviousProfile()),
                    keyboardService.getKeyboardLowers(),
                    serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)));
        }

        if (message.getText().equals(messagesService.getText("button.menu"))) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
            return Collections.singletonList(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.menu"),
                    keyboardService.getMainMenu()));
        }
        return Collections.emptyList();
    }


}
