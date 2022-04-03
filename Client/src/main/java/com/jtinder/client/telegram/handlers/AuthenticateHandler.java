package com.jtinder.client.telegram.handlers;

import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.KeyboardService;
import com.jtinder.client.telegram.service.ReplyMessagesService;
import com.jtinder.client.telegram.service.ServerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class AuthenticateHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;


    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        List<PartialBotApiMethod<?>> answerList = new ArrayList<>();
        String usersAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();

        SendMessage replyToUser = new SendMessage();
        if (usersAnswer.equals("/start")) {
            replyToUser.setChatId(String.valueOf(chatId));
            replyToUser.setText("Зарегистрируйтесь или войдите");
            replyToUser.enableMarkdown(true);
            replyToUser.setReplyMarkup(keyboardService.getAuthenticateKeyboard());
        }

        if (message.getText().equals("Регистрація")) {
            boolean isRegister = serverService.isRegistered(userId);
            if (isRegister) {
                replyToUser.setChatId(String.valueOf(chatId));
                replyToUser.setText("Вы уже зарегистрированы, войдите с паролем");
                replyToUser.enableMarkdown(true);
                replyToUser.setReplyMarkup(keyboardService.getAuthenticateKeyboard());
            }

            userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_SEX);
            System.out.println(userDataCache.getUsersCurrentBotState(chatId));
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askPassword");
        }

        if (message.getText().equals("Внити")) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.LOGIN);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askPassword");
        }
        answerList.add(new DeleteMessage(String.valueOf(chatId), message.getMessageId()));
        answerList.add(replyToUser);
        return answerList;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.AUTHENTICATE;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        return null;
    }
}
