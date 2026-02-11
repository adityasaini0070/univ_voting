package com.univvoting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class TelegramServiceReal {
    private final TelegramBotService bot;

    public void sendOtp(Long chatId, String otp) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId.toString());
        sm.setText("Your verification code: " + otp + "\nExpires in 2 minutes.");
        try {
            bot.execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
