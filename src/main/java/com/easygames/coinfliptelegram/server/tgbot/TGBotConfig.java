package com.easygames.coinfliptelegram.server.tgbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class TGBotConfig {

    @Value("${BOT.TOKEN}")
    private String botToken;

    @Bean
    public TelegramClient telegramClient() {
        return new OkHttpTelegramClient(botToken);
    }

    @Bean
    public String botToken() {
        return botToken;
    }
}
