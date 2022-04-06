package com.jtinder.client.telegram;

import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.botapi.TelegramFacade;
import com.jtinder.client.telegram.cache.UserDataCache;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

@Component
@Getter
@Setter
@RequiredArgsConstructor
public class LigaTinderBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramLongPollingBot.class);

    @Value("${telegram.bot-name}")
    private String botUsername;
    @Value("${telegram.bot-token}")
    private String botToken;

    private final TelegramFacade telegramFacade;
    private final UserDataCache userDataCache;


    @Override
    public void onUpdateReceived(Update update) {
        for (PartialBotApiMethod<?> method : telegramFacade.handleUpdate(update)) {
            addMessageToDelete(executeAnyMethod(method));
        }
    }

    private Message executeAnyMethod(PartialBotApiMethod<?> method) {
        try {
            if (method == null) return null;
            else if (method instanceof SendPhoto) return execute((SendPhoto) method);
            else if (method instanceof SendMessage) return execute((SendMessage) method);
            else if (method instanceof DeleteMessage) execute((DeleteMessage) method);
            else if (method instanceof AnswerCallbackQuery) execute((AnswerCallbackQuery) method);
        } catch (TelegramApiException e) {
            log.info(e.getMessage());
        }
        return null;
    }

    private void addMessageToDelete(Message message) {
        if (message != null) {
            BotState usersCurrentBotState = userDataCache.getUsersCurrentBotState(message.getChatId());
            switch (usersCurrentBotState) {
                case PROFILE:
                case SEARCH:
                case LOVERS:
                case MAIN_MENU:
                    userDataCache.setMessagesToDelete(message.getChatId(), Set.of(message.getMessageId()));
                    break;
                default:
                    userDataCache.addMessage(message.getChatId(), message.getMessageId());
            }
        }
    }
}
