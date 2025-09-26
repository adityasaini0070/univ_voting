package com.univvoting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to forward all non-API requests to the React frontend
 */
@Controller
public class FrontendController {

    /**
     * Forward all routes to index.html except for API calls
     * This allows React Router to handle client-side routing
     */
    @GetMapping(value = {
        "/", 
        "/home", 
        "/index", 
        // Removing "/login" and "/register" as they are handled by AuthController
        "/elections", 
        "/elections/**", 
        "/profile", 
        "/vote", 
        "/vote/**",
        "/admin",
        "/admin/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}