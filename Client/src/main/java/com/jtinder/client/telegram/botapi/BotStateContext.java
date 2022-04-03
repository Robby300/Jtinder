package com.jtinder.client.telegram.botapi;

import com.jtinder.client.telegram.handlers.InputMessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines message handlers for each state.
 */
@Component
public class BotStateContext {
    private final Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public List<PartialBotApiMethod<?>> processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    public List<PartialBotApiMethod<?>> processInputCallBack(BotState currentState, CallbackQuery callbackQuery) {
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


    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isFillingProfileState(currentState)) {
            return messageHandlers.get(BotState.FILLING_PROFILE);
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
                currentState = BotState.LOWERS;
                break;
        }

        if (isFillingProfileState(currentState)) {
            return messageHandlers.get(BotState.FILLING_PROFILE);
        }
        return messageHandlers.get(currentState);
    }
}
