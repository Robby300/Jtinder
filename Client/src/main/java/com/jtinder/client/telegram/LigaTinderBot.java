package com.jtinder.client.telegram;

import com.jtinder.client.telegram.botapi.TelegramFacade;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

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


    @Override
    public void onUpdateReceived(Update update) {
        PartialBotApiMethod<?> botApiMethod = telegramFacade.handleUpdate(update);
        if(botApiMethod instanceof BotApiMethod<?>) {
            try {
                execute((BotApiMethod<?>) botApiMethod);
            }catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if (botApiMethod instanceof SendPhoto) {
            try {
                execute((SendPhoto) botApiMethod);
            }catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }else try {
            execute((EditMessageMedia) botApiMethod);
        }catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
