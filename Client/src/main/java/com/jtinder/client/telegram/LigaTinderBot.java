package com.jtinder.client.telegram;

import com.jtinder.client.telegram.botapi.TelegramFacade;
import com.jtinder.client.telegram.cache.UserDataCache;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.util.List;

@Component
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class LigaTinderBot extends TelegramLongPollingBot {
    @Value("${telegram.bot-name}")
    String botUsername;
    @Value("${telegram.bot-token}")
    String botToken;

    final TelegramFacade telegramFacade;
    private final UserDataCache userDataCache;


    @Override
    public void onUpdateReceived(Update update) {

        List<PartialBotApiMethod<?>> answerList = telegramFacade.handleUpdate(update);
        for (PartialBotApiMethod<?> answer : answerList) {
            if (answer instanceof BotApiMethod<?>) {
                BotApiMethod<Message> message = (BotApiMethod<Message>) answer;
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (answer instanceof SendPhoto) {
                try {
                    execute((SendPhoto) answer);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else try {
                execute((EditMessageMedia) answer);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
