package com.internshipfinder.zirah.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.internshipfinder.zirah.model.Internship;
import com.internshipfinder.zirah.model.Recruiter;
import com.internshipfinder.zirah.service.InternshipService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/internships")
public class InternshipController {

    @Autowired
    private InternshipService internshipService;

    @GetMapping("/post")
    public String showPostInternshipForm(Model model, HttpSession session) {
        Recruiter recruiter = (Recruiter) session.getAttribute("user");
        if (recruiter == null) {
            return "redirect:/signin";
        }
        model.addAttribute("internship", new Internship());
        return "post-internship";
    }

    @PostMapping("/post")
    public String postInternship(@ModelAttribute Internship internship, HttpSession session) {
        Recruiter recruiter = (Recruiter) session.getAttribute("user");
        if (recruiter == null) {
            return "redirect:/signin";
        }
        internship.setRecruiter(recruiter);
        internship.setPostedDate(LocalDateTime.now());
        internshipService.saveInternship(internship);
        return "redirect:/recruiter/dashboard";
    }
}