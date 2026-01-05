package com.internshipfinder.zirah.controller;

import com.internshipfinder.zirah.service.StudentService;
import com.internshipfinder.zirah.service.RecruiterService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private RecruiterService recruiterService;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        // Check if user is admin
        Object userType = session.getAttribute("userType");
        System.out.println("=== ADMIN DASHBOARD - UserType: " + userType + " ===");
        
        if (!"admin".equals(userType)) {
            System.out.println("=== REDIRECTING TO HOME - Not admin ===");
            return "redirect:/";
        }

        // Get counts
        long studentCount = studentService.getStudentCount();
        long recruiterCount = recruiterService.getRecruiterCount();

        System.out.println("=== LOADING ADMIN DASHBOARD - Students: " + studentCount + ", Recruiters: " + recruiterCount + " ===");

        model.addAttribute("studentCount", studentCount);
        model.addAttribute("recruiterCount", recruiterCount);

        return "admin-dashboard";
    }
}