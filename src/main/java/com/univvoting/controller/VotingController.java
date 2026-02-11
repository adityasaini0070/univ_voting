package com.univvoting.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.univvoting.model.User;
import com.univvoting.repository.UserRepository;
import com.univvoting.service.OtpService;
import com.univvoting.service.VoteService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/vote")
@RequiredArgsConstructor
public class VotingController {
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final VoteService voteService;

    // Step 1: Candidate selection
    @PostMapping("/select-candidate")
    public String selectCandidate(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam UUID electionId,
            @RequestParam UUID candidateId,
            Model model) {
        Optional<User> maybe = userRepository.findByUniversityId(userDetails.getUsername());
        if (maybe.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "elections";
        }

        User u = maybe.get();
        if (!"VOTER".equalsIgnoreCase(u.getRole())) {
            model.addAttribute("error", "You need to be a VOTER to vote");
            return "elections";
        }

        // Store selection in model
        model.addAttribute("electionId", electionId);
        model.addAttribute("candidateId", candidateId);
        if (voteService.hasUserVoted(electionId, u.getId())) {
            model.addAttribute("error", "You have already voted in this election.");
            return "elections";
        }
        // Request OTP
        try {
            otpService.generateAndSendOtp(u.getId());
            model.addAttribute("message", "OTP sent to your Telegram");
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "elections";
        }

        return "enter-otp";
    }

    // Step 2: OTP Verification and Voting
    @PostMapping("/verify-otp")
    public String verifyOtp(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam UUID electionId,
            @RequestParam UUID candidateId,
            @RequestParam String otp,
            Model model) {
        Optional<User> maybe = userRepository.findByUniversityId(userDetails.getUsername());
        if (maybe.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "elections";
        }

        User u = maybe.get();
        if (!"VOTER".equalsIgnoreCase(u.getRole())) {
            model.addAttribute("error", "You need to be a VOTER to vote");
            return "elections";
        }

        // Verify OTP
        boolean ok = otpService.verifyOtp(u.getId(), otp);
        if (!ok) {
            model.addAttribute("error", "Invalid or expired OTP");
            model.addAttribute("electionId", electionId);
            model.addAttribute("candidateId", candidateId);
            return "enter-otp";
        }

        // Cast vote
        try {
            voteService.castVote(electionId, u.getId(), candidateId);
            model.addAttribute("message", "Vote cast successfully!");
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "elections";
        }

        return "redirect:/elections";
    }

}