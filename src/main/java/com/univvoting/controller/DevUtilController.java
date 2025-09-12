package com.univvoting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.univvoting.service.UserService;

@RestController
public class DevUtilController {

    // Endpoint to delete a user by universityId: /delete-user?universityId=2310991418
    @GetMapping("/delete-user")
    public String deleteUser(@RequestParam String universityId) {
        boolean ok = userService.deleteUserByUniversityId(universityId);
        return ok ? "User deleted successfully" : "User not found";
    }
    @Autowired
    private UserService userService;

    // Example: /set-role?universityId=2310991418&role=VOTER
    @GetMapping("/set-role")
    public String setRole(@RequestParam String universityId, @RequestParam String role) {
        boolean ok = userService.setRoleByUniversityId(universityId, role);
        return ok ? "Role updated successfully" : "User not found";
    }
}