package com.jtinder.client.telegram.botapi.handlers;

import com.jtinder.client.telegram.botapi.BotState;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Обработчик сообщений
 */
public interface InputMessageHandler {
    BotApiMethod<?> handle(Message message);

    BotState getHandlerName();

    BotApiMethod<?> handle(CallbackQuery callbackQuery);
}

