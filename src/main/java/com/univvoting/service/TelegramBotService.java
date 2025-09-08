package com.univvoting.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.univvoting.model.TelegramLink;
import com.univvoting.model.User;
import com.univvoting.repository.UserRepository;

@Component
public class TelegramBotService extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramBotService.class);

    private final TelegramLinkService linkService;
    private final UserRepository userRepository;

    private final String botToken;
    private final String botUsername;

    public TelegramBotService(
            TelegramLinkService linkService,
            UserRepository userRepository,
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username:}") String botUsername) {
        super(botToken);
        this.linkService = linkService;
        this.userRepository = userRepository;
        this.botToken = botToken;
        this.botUsername = botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) return;
        Message msg = update.getMessage();
        if (msg.hasText()) {
            String text = msg.getText().trim();
            Long chatId = msg.getChatId();
            log.info("Received telegram message from {}: {}", chatId, text);
            if (text.startsWith("/start")) {
                String[] parts = text.split("\s+");
                if (parts.length >= 2) {
                    String token = parts[1].trim();
                    Optional<TelegramLink> maybe = linkService.findByToken(token);
                    if (maybe.isPresent()) {
                        TelegramLink tl = maybe.get();
                        // link chat id to user
                        Optional<User> uopt = userRepository.findById(tl.getUserId());
                        if (uopt.isPresent()) {
                            User u = uopt.get();
                            u.setTelegramChatId(chatId);
                            userRepository.save(u);
                            linkService.delete(tl);
                            sendMsg(chatId, "Your Telegram has been linked to your university account.");
                            return;
                        }
                    }
                    sendMsg(chatId, "Invalid or expired link token. Please request a new link from the web app.");
                } else {
                    sendMsg(chatId, "Please provide the token: /start <token>");
                }
            }
        }
    }

    private void sendMsg(Long chatId, String text) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId.toString());
        sm.setText(text);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error("Failed to send telegram message", e);
        }
    }

    @Override
    public String getBotUsername() { return botUsername == null ? "UnivVotingBot" : botUsername; }

    @Override
    public String getBotToken() { return botToken; }
}
