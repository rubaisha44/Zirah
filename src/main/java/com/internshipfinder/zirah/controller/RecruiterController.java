package com.internshipfinder.zirah.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.internshipfinder.zirah.model.Recruiter;
import com.internshipfinder.zirah.service.RecruiterService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/recruiters")
public class RecruiterController {

    @Autowired
    private RecruiterService recruiterService;

    @PostMapping("/save")
    public String saveRecruiter(@ModelAttribute Recruiter recruiter, Model model) {
        try {
            recruiterService.saveRecruiter(recruiter);
            model.addAttribute("success", "Recruiter registered successfully!");
            return "signin";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Email already exists! Please use a different email.");
            model.addAttribute("student", new Object());
            model.addAttribute("recruiter", new Recruiter());
            return "signup";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            model.addAttribute("student", new Object());
            model.addAttribute("recruiter", new Recruiter());
            return "signup";
        }
    }

    @GetMapping("/profile")
    public String showProfileForm(Model model, HttpSession session) {
        Recruiter recruiter = (Recruiter) session.getAttribute("user");
        if (recruiter == null) {
            return "redirect:/signin";
        }
        
        // Get fresh data from database
        Recruiter freshRecruiter = recruiterService.findByEmail(recruiter.getEmail());
        model.addAttribute("recruiter", freshRecruiter);
        return "recruiter-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Recruiter updatedRecruiter, 
                               HttpSession session, 
                               Model model) {
        Recruiter currentRecruiter = (Recruiter) session.getAttribute("user");
        if (currentRecruiter == null) {
            return "redirect:/signin";
        }

        try {
            // Get existing recruiter
            Recruiter existingRecruiter = recruiterService.findByEmail(currentRecruiter.getEmail());
            
            // Update fields
            existingRecruiter.setName(updatedRecruiter.getName());
            existingRecruiter.setCompanyName(updatedRecruiter.getCompanyName());
            existingRecruiter.setPosition(updatedRecruiter.getPosition());
            
            // Save updated recruiter
            Recruiter savedRecruiter = recruiterService.saveRecruiter(existingRecruiter);
            
            // Update session
            session.setAttribute("user", savedRecruiter);
            model.addAttribute("recruiter", savedRecruiter);
            model.addAttribute("success", "Profile updated successfully!");
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update profile: " + e.getMessage());
            Recruiter current = (Recruiter) session.getAttribute("user");
            model.addAttribute("recruiter", current);
        }
        
        return "recruiter-profile";
    }
}