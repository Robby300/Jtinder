package com.jtinder.client.config;

import com.jtinder.client.telegram.LigaTinderBot;
import com.jtinder.client.telegram.botapi.TelegramFacade;
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
                                          TelegramFacade telegramFacade) {
        LigaTinderBot bot = new LigaTinderBot(setWebhook, telegramFacade);

        bot.setBotPath(telegramConfig.getWebhookPath());
        bot.setBotUsername(telegramConfig.getBotName());
        bot.setBotToken(telegramConfig.getBotToken());

        return bot;
    }
}