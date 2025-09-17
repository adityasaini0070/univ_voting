package com.univvoting.service;

import com.univvoting.model.TelegramLink;
import com.univvoting.model.User;
import com.univvoting.repository.TelegramLinkRepository;
import com.univvoting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;
import java.util.UUID;

@Service
public class TelegramBotService extends TelegramLongPollingBot {

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "univvote_bot"; 
    }

    @Override
    public String getBotToken() {
        return "8080890392:AAE-SKLrxks2TTrpo7vDYFQulYjweASPeX4"; 
    }
}
