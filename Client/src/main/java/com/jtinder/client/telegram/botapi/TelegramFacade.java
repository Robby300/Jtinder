package com.jtinder.client.telegram.botapi;

import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.BotMethodService;
import com.jtinder.client.telegram.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TelegramFacade {
    private static final Logger log = LoggerFactory.getLogger(TelegramFacade.class);
    private final BotStateContext botStateContext;
    private final UserDataCache userDataCache;
    private final BotMethodService botMethodService;


    public List<PartialBotApiMethod<?>> handleUpdate(Update update) {
        List<PartialBotApiMethod<?>> replyMessage = new ArrayList<>();

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());

            replyMessage = handleInputCallBackQuery(callbackQuery);
        }

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private List<PartialBotApiMethod<?>> handleInputMessage(Message message) {
        String inputMsg = message.getText();
        long chatId = message.getChatId();
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);

        if (inputMsg.equals("/start")) {
            botState = BotState.AUTHENTICATE;
            userDataCache.setUsersCurrentBotState(chatId, botState);
            List<PartialBotApiMethod<?>> method = userDataCache.getMessagesToDelete(chatId).stream()
                    .map(integer -> botMethodService.getDeleteMessage(chatId, integer))
                    .collect(Collectors.toList());
            method.add(botMethodService.getDeleteMessage(chatId, message.getMessageId()));
            method.addAll(botStateContext.processInputMessage(botState, message));
            return method;
        }

        List<PartialBotApiMethod<?>> answerList = botStateContext.processInputMessage(botState, message);
        if(answerList.isEmpty()) {
            return Collections.singletonList(botMethodService.getDeleteMessage(chatId, message.getMessageId()));
        }
        return answerList;
    }

    private List<PartialBotApiMethod<?>> handleInputCallBackQuery(CallbackQuery callbackQuery) {
        return botStateContext.processInputCallBack(userDataCache.getUsersCurrentBotState(callbackQuery.getFrom().getId()), callbackQuery);
    }


}
