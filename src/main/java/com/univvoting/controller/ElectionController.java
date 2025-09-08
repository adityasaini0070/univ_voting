package com.univvoting.controller;

import com.univvoting.repository.ElectionRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import com.univvoting.repository.CandidateRepository;
import com.univvoting.model.Candidate;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ElectionController {


    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CandidateRepository candidateRepository;

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
