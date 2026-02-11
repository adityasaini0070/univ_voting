package com.univvoting.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import com.univvoting.model.Candidate;
import com.univvoting.model.Election;
import com.univvoting.repository.CandidateRepository;
import com.univvoting.repository.VoteLedgerRepository;
import com.univvoting.repository.ElectionRepository;
import com.univvoting.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    @GetMapping("/edit-election")
    public String editElectionForm(@RequestParam @NonNull UUID id, Model model) {
        var election = electionRepository.findById(id).orElse(null);
        if (election == null) {
            model.addAttribute("error", "Election not found");
            return "redirect:/admin/elections";
        }
        model.addAttribute("election", election);
        return "admin/edit-election";
    }

    @PostMapping("/edit-election")
    public String editElection(@RequestParam @NonNull UUID id,
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
    public String deleteElection(@RequestParam @NonNull UUID id, Model model) {
        try {
            adminService.deleteElection(id);
            model.addAttribute("message", "Election deleted successfully");
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/elections";
    }

    private final AdminService adminService;
    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;
    private final VoteLedgerRepository voteLedgerRepository;

    @GetMapping
    public String adminHome(Model model) {
        // Get statistics for dashboard
        long totalElections = electionRepository.count();
        long totalCandidates = candidateRepository.count();
        long totalUsers = adminService.getAllUsers().size();
        long totalVotes = voteLedgerRepository.count();

        // Add to model
        model.addAttribute("totalElections", totalElections);
        model.addAttribute("totalCandidates", totalCandidates);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalVotes", totalVotes);

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
            view.put("startTimeLocal",
                    election.getStartTime() != null
                            ? java.time.LocalDateTime.ofInstant(election.getStartTime(),
                                    java.time.ZoneId.systemDefault())
                            : null);
            view.put("endTimeLocal",
                    election.getEndTime() != null
                            ? java.time.LocalDateTime.ofInstant(election.getEndTime(), java.time.ZoneId.systemDefault())
                            : null);
            electionViews.add(view);
        }

        // Create the map for candidates, always non-null
        var candidatesMap = new HashMap<UUID, List<Candidate>>();
        for (var election : elections) {
            List<Candidate> candidates = candidateRepository.findByElectionId(election.getId());
            if (candidates == null)
                candidates = new ArrayList<>();
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
            @RequestParam @NonNull UUID electionId,
            Model model) {
        try {
            adminService.createCandidate(name, description, electionId);
            model.addAttribute("message", "Candidate created successfully");
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/elections";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        var users = adminService.getAllUsers();
        model.addAttribute("users", users);

        // Count users by role
        long adminCount = users.stream().filter(user -> "ADMIN".equals(user.getRole())).count();
        long voterCount = users.stream().filter(user -> "VOTER".equals(user.getRole())).count();

        model.addAttribute("adminCount", adminCount);
        model.addAttribute("voterCount", voterCount);

        return "admin/users";
    }

    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam @NonNull UUID userId, Model model) {
        try {
            adminService.deleteUser(userId);
            model.addAttribute("message", "User deleted successfully");
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/users";
    }
}
