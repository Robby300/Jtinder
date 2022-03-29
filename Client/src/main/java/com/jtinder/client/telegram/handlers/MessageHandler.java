package com.jtinder.client.telegram.handlers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;


@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageHandler {
    public BotApiMethod<?> answerMessage(Message message) {
        String chatId = message.getChatId().toString();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if(!Objects.equals(message.getText(), "/start")) {
            sendMessage.setText("Привет, Роберт спит, он устал.");
        }
        else {
            sendMessage.setText("Я стартанул!");
        }
        return sendMessage;
    }
}
