package com.univvoting.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import com.univvoting.model.Candidate;
import com.univvoting.model.Election;
import com.univvoting.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.univvoting.repository.ElectionRepository;
import com.univvoting.service.AdminService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @GetMapping("/edit-election")
    public String editElectionForm(@RequestParam UUID id, Model model) {
        var election = electionRepository.findById(id).orElse(null);
        if (election == null) {
            model.addAttribute("error", "Election not found");
            return "redirect:/admin/elections";
        }
        model.addAttribute("election", election);
        return "admin/edit-election";
    }

    @PostMapping("/edit-election")
    public String editElection(@RequestParam UUID id,
                              @RequestParam String name,
                              @RequestParam("startTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startTime,
                              @RequestParam("endTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endTime,
                              Model model) {
        try {
            var startInstant = startTime.atZone(ZoneId.systemDefault()).toInstant();
            var endInstant = endTime.atZone(ZoneId.systemDefault()).toInstant();
            adminService.updateElection(id, name, startInstant, endInstant);
            model.addAttribute("message", "Election updated successfully");
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/elections";
    }

    @PostMapping("/delete-election")
    public String deleteElection(@RequestParam UUID id, Model model) {
        try {
            adminService.deleteElection(id);
            model.addAttribute("message", "Election deleted successfully");
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/elections";
    }

    @Autowired
    private AdminService adminService;
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @GetMapping
    public String adminHome() {
        return "admin/dashboard";
    }

    // In AdminController.java
    @GetMapping("/elections")
    public String elections(Model model) {
        // Get the original list of Election objects
        List<Election> elections = adminService.getAllElections();

        // Prepare a list of maps for Thymeleaf compatibility
        List<Map<String, Object>> electionViews = new ArrayList<>();
        for (Election election : elections) {
            Map<String, Object> view = new HashMap<>();
            view.put("id", election.getId());
            view.put("title", election.getTitle());
            view.put("startTime", election.getStartTime());
            view.put("endTime", election.getEndTime());
            view.put("startTimeLocal", election.getStartTime() != null ? java.time.LocalDateTime.ofInstant(election.getStartTime(), java.time.ZoneId.systemDefault()) : null);
            view.put("endTimeLocal", election.getEndTime() != null ? java.time.LocalDateTime.ofInstant(election.getEndTime(), java.time.ZoneId.systemDefault()) : null);
            electionViews.add(view);
        }

        // Create the map for candidates, always non-null
        var candidatesMap = new HashMap<UUID, List<Candidate>>();
        for (var election : elections) {
            List<Candidate> candidates = candidateRepository.findByElectionId(election.getId());
            if (candidates == null) candidates = new ArrayList<>();
            candidatesMap.put(election.getId(), candidates);
        }

        // Add the new electionViews list and the map to the model
        model.addAttribute("elections", electionViews);
        model.addAttribute("candidatesMap", candidatesMap);

        return "admin/elections";
    }

    @GetMapping("/create-election")
    public String createElectionForm() {
        return "admin/create-election";
    }

    @PostMapping("/create-election")
    public String createElection(@RequestParam String name,
                                 @RequestParam("startTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startTime,
                                 @RequestParam("endTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endTime,
                                 Model model) {
        try {
            // Convert LocalDateTime to Instant using system default zone
            var startInstant = startTime.atZone(ZoneId.systemDefault()).toInstant();
            var endInstant = endTime.atZone(ZoneId.systemDefault()).toInstant();
            adminService.createElection(name, startInstant, endInstant);
            model.addAttribute("message", "Election created successfully");
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/elections";
    }

    @GetMapping("/create-candidate")
    public String createCandidateForm(Model model) {
        model.addAttribute("elections", electionRepository.findAll());
        return "admin/create-candidate";
    }

    @PostMapping("/create-candidate")
    public String createCandidate(@RequestParam String name,
                                  @RequestParam String description,
                                  @RequestParam UUID electionId,
                                  Model model) {
        try {
            adminService.createCandidate(name, description, electionId);
            model.addAttribute("message", "Candidate created successfully");
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/elections";
    }
}
