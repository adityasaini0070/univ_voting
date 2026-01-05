package com.univvoting.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.univvoting.model.Candidate;
import com.univvoting.repository.CandidateRepository;
import com.univvoting.repository.ElectionRepository;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ElectionController {

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @GetMapping("/elections/vote")
    public String votePage(@RequestParam @NonNull UUID electionId, Model model) {
        var election = electionRepository.findById(electionId).orElse(null);
        if (election == null) {
            return "redirect:/elections";
        }

        var candidates = candidateRepository.findByElectionId(electionId);
        model.addAttribute("election", election);
        model.addAttribute("candidates", candidates);
        return "vote-page";
    }

    @GetMapping("/elections")
    public String listElections(Model model) {
        var elections = electionRepository.findAll();
        var candidatesMap = new HashMap<java.util.UUID, java.util.List<Candidate>>();
        var electionViewList = new java.util.ArrayList<java.util.Map<String, Object>>();
        for (var election : elections) {
            candidatesMap.put(election.getId(), candidateRepository.findByElectionId(election.getId()));
            var electionView = new java.util.HashMap<String, Object>();
            electionView.put("id", election.getId());
            electionView.put("title", election.getTitle());
            electionView.put("startTime", LocalDateTime.ofInstant(election.getStartTime(), ZoneId.systemDefault()));
            electionView.put("endTime", LocalDateTime.ofInstant(election.getEndTime(), ZoneId.systemDefault()));
            electionViewList.add(electionView);
        }
        model.addAttribute("elections", electionViewList);
        model.addAttribute("candidatesMap", candidatesMap);
        return "elections";
    }
}
