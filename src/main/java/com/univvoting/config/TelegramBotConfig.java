package com.univvoting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.univvoting.service.TelegramBotService;

@Configuration
public class TelegramBotConfig {

    @Autowired
    private TelegramBotService telegramBotService;

    @Bean
    public Boolean registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBotService);
            System.out.println("Telegram bot registered successfully!");
            return true;
        } catch (TelegramApiException e) {
            System.err.println("Failed to register Telegram bot: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}