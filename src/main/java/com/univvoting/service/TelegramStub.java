package com.univvoting.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TelegramStub {
    private static final Logger log = LoggerFactory.getLogger(TelegramStub.class);

    public void sendOtp(Long chatId, String otp) {
        // Stub: logs OTP. Replace with real Telegram Bot API calls in production.
        log.info("[TELEGRAM-STUB] Sending OTP {} to chat {}", otp, chatId);
    }
}
