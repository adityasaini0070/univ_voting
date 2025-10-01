package com.univvoting.controller;

import com.univvoting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerForm() { return "register"; }

    @PostMapping("/register")
    public String register(@RequestParam String universityId,
                           @RequestParam String fullName,
                           @RequestParam String password,
                           @RequestParam(required = false) String phoneNumber,
                           Model model) {
        try {
            userService.register(universityId, fullName, "VOTER", password, phoneNumber);
            model.addAttribute("message","Registration successful. Please login.");
            return "login";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String login() { return "login"; }
}
