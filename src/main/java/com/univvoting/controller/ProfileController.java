package com.univvoting.controller;

import com.univvoting.model.TelegramLink;
import com.univvoting.model.User;
import com.univvoting.repository.UserRepository;
import com.univvoting.service.TelegramLinkService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final TelegramLinkService telegramLinkService;

    // This method shows the user their profile page.
    @GetMapping
    public String userProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> maybeUser = userRepository.findByUniversityId(userDetails.getUsername());
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            model.addAttribute("telegramLinked", user.getTelegramChatId() != null);
        }
        return "profile";
    }

    // This is the new method that generates the token when a button is clicked.
    @PostMapping("/generate-link")
    public String generateTelegramLink(@AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        // Find the currently logged-in user
        Optional<User> maybeUser = userRepository.findByUniversityId(userDetails.getUsername());

        if (maybeUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Could not find your user account.");
            return "redirect:/profile";
        }

        // Call the service to create the token
        TelegramLink telegramLink = telegramLinkService.createTokenForUser(maybeUser.get().getId());

        // Send the token back to the page so the user can see it
        String fullCommand = "/start " + telegramLink.getToken();
        redirectAttributes.addFlashAttribute("telegramCommand", fullCommand);
        redirectAttributes.addFlashAttribute("message", "Token generated successfully! Send the command to your bot.");

        return "redirect:/profile";
    }
}