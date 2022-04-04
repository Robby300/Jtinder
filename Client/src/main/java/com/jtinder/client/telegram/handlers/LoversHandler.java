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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
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
        return BotState.LOWERS;
    }

    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        DeleteMessage deleteMessage = botMethodService.getDeleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        User user = userDataCache.getUserProfileData(chatId);

        if (callbackQuery.getData().equals("ЛЮБИМЦЫ")) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.LOWERS);
            List<Profile> users = serverService.getLowersProfilesToUser(user);
            log.info("Пришел список подходящих анкет с размером {}", users.size());
            log.info("Список: {}", users);

            if (users.size() == 0) {
                userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
                return List.of(deleteMessage, botMethodService.getSendMessage(chatId,
                        messagesService.getText("reply.noProfile"),
                        keyboardService.getInlineMainMenu()));
            }

            user.setScrollableListWrapper(new ScrollableListWrapper(users));

            return List.of(deleteMessage, botMethodService.getSendPhoto(chatId,
                    imageService.getFile(user.getScrollableListWrapper().getCurrentProfile()),
                    keyboardService.getInlineKeyboardLowers(),
                    serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)));


        }

        if (callbackQuery.getData().equals("Следующий")) {
            if (user.getScrollableListWrapper().isLast()) {
                user.getScrollableListWrapper().resetCurrentIndex();

                return List.of(deleteMessage, botMethodService.getSendPhoto(chatId,
                        imageService.getFile(user.getScrollableListWrapper().getCurrentProfile()),
                        keyboardService.getInlineKeyboardLowers(),
                        serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)));

            }

            return List.of(deleteMessage, botMethodService.getSendPhoto(chatId,
                    imageService.getFile(user.getScrollableListWrapper().getNextProfile()),
                    keyboardService.getInlineKeyboardLowers(),
                    serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)));

        }

        if (callbackQuery.getData().equals("Предыдущий")) {
            if (user.getScrollableListWrapper().isFirst()) {
                user.getScrollableListWrapper().resetCurrentIndexFromLast();

                return List.of(deleteMessage, botMethodService.getSendPhoto(chatId,
                        imageService.getFile(user.getScrollableListWrapper().getCurrentProfile()),
                        keyboardService.getInlineKeyboardLowers(),
                        serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)));

            }

            return List.of(deleteMessage, botMethodService.getSendPhoto(chatId,
                    imageService.getFile(user.getScrollableListWrapper().getPreviousProfile()),
                    keyboardService.getInlineKeyboardLowers(),
                    serverService.getCaption(user.getScrollableListWrapper().getCurrentProfile().getUserId(), user)));
        }

        if (callbackQuery.getData().equals("MENUL")) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
            return List.of(deleteMessage, botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.menu"),
                    keyboardService.getInlineMainMenu()));
        }
        return Collections.singletonList(deleteMessage);
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        long chatId = message.getChatId();
        return Collections.singletonList(botMethodService.getDeleteMessage(chatId, message.getMessageId()));
    }


}
