package com.jtinder.client.telegram.botapi;

import com.jtinder.client.telegram.handlers.InputMessageHandler;
import com.jtinder.client.telegram.service.TextMessagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines message handlers for each state.
 */
@Slf4j
@Component
public class BotStateContext {
    private final Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();
    private final TextMessagesService messagesService;

    public BotStateContext(List<InputMessageHandler> messageHandlers, TextMessagesService messagesService) {
        this.messagesService = messagesService;
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public List<PartialBotApiMethod<?>> processInputMessage(BotState currentState, Message message) {
        if(currentState == null) {
            return Collections.singletonList(new DeleteMessage(message.getChatId().toString(), message.getMessageId()));
        }
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState, message);
        if(currentMessageHandler == null) {
            return Collections.singletonList(new DeleteMessage(message.getChatId().toString(), message.getMessageId()));
        }
        log.info("User state {}, handler {}", currentState, currentMessageHandler);
        return currentMessageHandler.handle(message);
    }

    public List<PartialBotApiMethod<?>> processInputCallBack(BotState currentState, CallbackQuery callbackQuery) {
        if(currentState == null) {
            return Collections.singletonList(new DeleteMessage(callbackQuery.getMessage().getChatId().toString(), callbackQuery.getMessage().getMessageId()));
        }
        InputMessageHandler currentMessageHandler = findMessageHandlerForCallBack(currentState, callbackQuery);
        return currentMessageHandler.handle(callbackQuery);
    }

    private boolean isFillingProfileState(BotState currentState) {
        switch (currentState) {
            case ASK_NAME:
            case ASK_SEX:
            case ASK_DESCRIPTION:
            case ASK_FIND:
            case FILLING_PROFILE:
            case PROFILE_FILLED:
                return true;
            default:
                return false;
        }
    }


    private InputMessageHandler findMessageHandler(BotState currentState, Message message) {
        if (isFillingProfileState(currentState)) {
            return messageHandlers.get(BotState.FILLING_PROFILE);
        }
        if(currentState.equals(BotState.MAIN_MENU)) {
            String text = message.getText();
            if (messagesService.getText("button.search").equals(text)) {
                currentState = BotState.SEARCH;
            } else if (messagesService.getText("button.profile").equals(text)) {
                currentState = BotState.PROFILE;
            } else if (messagesService.getText("button.lovers").equals(text)) {
                currentState = BotState.LOVERS;
            }
        }
        return messageHandlers.get(currentState);
    }

    private InputMessageHandler findMessageHandlerForCallBack(BotState currentState, CallbackQuery callbackQuery) {
        switch (callbackQuery.getData()) {
            case "ПОИСК":
                currentState = BotState.SEARCH;
                break;
            case "АНКЕТА":
                currentState = BotState.PROFILE;
                break;
            case "ЛЮБИМЦЫ":
                currentState = BotState.LOVERS;
                break;
        }

        if (isFillingProfileState(currentState)) {
            return messageHandlers.get(BotState.FILLING_PROFILE);
        }
        return messageHandlers.get(currentState);
    }
}
