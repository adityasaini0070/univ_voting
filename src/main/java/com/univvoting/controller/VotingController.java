package com.univvoting.controller;

import com.univvoting.model.User;
import com.univvoting.repository.UserRepository;
import com.univvoting.service.OtpService;
import com.univvoting.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/vote")
public class VotingController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private VoteService voteService;

    @PostMapping("/request-otp")
    public String requestOtp(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam UUID electionId,
                             Model model) {
        Optional<User> maybe = userRepository.findByUniversityId(userDetails.getUsername());
        if (maybe.isEmpty()) {
            model.addAttribute("error","User not found");
            return "elections";
        }
        User u = maybe.get();
        try {
            otpService.generateAndSendOtp(u.getId());
            model.addAttribute("message","OTP sent to your Telegram (if linked)");
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        model.addAttribute("electionId", electionId);
        return "enter-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam UUID electionId,
                            @RequestParam UUID candidateId,
                            @RequestParam String otp,
                            Model model) {
        Optional<User> maybe = userRepository.findByUniversityId(userDetails.getUsername());
        if (maybe.isEmpty()) {
            model.addAttribute("error","User not found");
            return "elections";
        }
        User u = maybe.get();
        boolean ok = otpService.verifyOtp(u.getId(), otp);
        if (!ok) {
            model.addAttribute("error","Invalid or expired OTP");
            return "enter-otp";
        }

        try {
            voteService.castVote(electionId, u.getId(), candidateId);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "elections";
        }

        model.addAttribute("message","Vote cast successfully");
        return "elections";
    }
}
