package com.univvoting.service;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.univvoting.model.TelegramLink;
import com.univvoting.model.User;
import com.univvoting.repository.TelegramLinkRepository;
import com.univvoting.repository.UserRepository;

@SuppressWarnings("deprecation")
@Service
public class TelegramBotService extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

    @Autowired
    private TelegramLinkRepository linkRepo;

    @Autowired
    private UserRepository userRepo;

     @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    
    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) return;
        Message msg = update.getMessage();
        if (!msg.hasText()) return;

        String text = msg.getText().trim();
        Long chatId = msg.getChatId();

        // user sends: /start <token>
        if (text.startsWith("/start")) {
            String[] parts = text.split(" ");
            if (parts.length < 2) {
                sendMessage(chatId, "Please provide a valid token.");
                return;
            }
            String token = parts[1];
            Optional<TelegramLink> linkOpt = linkRepo.findByToken(token);
            if (linkOpt.isEmpty()) {
                sendMessage(chatId, "Invalid or expired token.");
                return;
            }

            TelegramLink link = linkOpt.get();
            UUID userId = link.getUserId();

            User u = userRepo.findById(userId).orElse(null);
            if (u == null) {
                sendMessage(chatId, "User not found.");
                return;
            }

            u.setTelegramChatId(chatId);
            userRepo.save(u);

            // delete token after use
            linkRepo.delete(link);

            sendMessage(chatId, " Telegram successfully linked to your account!");
        }
    }

    private void sendMessage(Long chatId, String text) {
        try {
            execute(new org.telegram.telegrambots.meta.api.methods.send.SendMessage(
                    chatId.toString(), text));
        } catch (org.telegram.telegrambots.meta.exceptions.TelegramApiException e) {
            logger.error("Failed to send Telegram message to chatId: " + chatId, e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername; 
    }

    @Override
    public String getBotToken() {
        return botToken; 
    }
}
