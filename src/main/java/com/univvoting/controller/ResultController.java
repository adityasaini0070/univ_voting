package com.univvoting.controller;

import com.univvoting.service.ElectionResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/results")
public class ResultController {

    @Autowired
    private ElectionResultService electionResultService;

    @GetMapping
    public String allResults(Model model) {
        var allResults = electionResultService.getAllElectionResults();
        model.addAttribute("electionResults", allResults);
        return "results";
    }

    @GetMapping("/{electionId}")
    public String electionResult(@PathVariable UUID electionId, Model model) {
        try {
            var result = electionResultService.getElectionResults(electionId);
            model.addAttribute("electionResult", result);
            return "election-result";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "redirect:/results";
        }
    }
}