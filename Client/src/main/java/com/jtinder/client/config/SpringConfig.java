package com.jtinder.client.config;

import com.jtinder.client.telegram.LigaTinderBot;
import com.jtinder.client.telegram.handlers.CallbackQueryHandler;
import com.jtinder.client.telegram.handlers.MessageHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
@AllArgsConstructor
public class SpringConfig {
    private final TelegramConfig telegramConfig;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(telegramConfig.getWebhookPath()).build();
    }

    @Bean
    public LigaTinderBot springWebhookBot(SetWebhook setWebhook,
                                          MessageHandler messageHandler,
                                          CallbackQueryHandler callbackQueryHandler) {
        LigaTinderBot bot = new LigaTinderBot(setWebhook, messageHandler, callbackQueryHandler);

        bot.setBotPath(telegramConfig.getWebhookPath());
        bot.setBotUsername(telegramConfig.getBotName());
        bot.setBotToken(telegramConfig.getBotToken());

        return bot;
    }
}