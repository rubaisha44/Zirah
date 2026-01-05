package com.internshipfinder.zirah.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.internshipfinder.zirah.service.StudentService;
import com.internshipfinder.zirah.service.RecruiterService;
import com.internshipfinder.zirah.model.Student;
import com.internshipfinder.zirah.model.Recruiter;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private RecruiterService recruiterService;

    @PostMapping("/login")
    public String login(@RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session) {
// First try to find student
    Student student = studentService.findByEmail(email);
    if (student != null && student.getPassword().equals(password)) {
        session.setAttribute("user", student);
        session.setAttribute("userType", "student");
        return "redirect:/student/dashboard";
    }

    // If not student, try recruiter
    Recruiter recruiter = recruiterService.findByEmail(email);
    if (recruiter != null && recruiter.getPassword().equals(password)) {
        session.setAttribute("user", recruiter);
        session.setAttribute("userType", "recruiter");
        return "redirect:/recruiter/dashboard";
    }

    // Check for admin (simple hardcoded admin for now)
    if ("admin@zirah.com".equals(email) && "admin123".equals(password)) {
        session.setAttribute("user", "admin");
        session.setAttribute("userType", "admin");
        return "redirect:/admin/dashboard";
    }

    // If neither found or password incorrect
    model.addAttribute("error", "Invalid email or password!");
    return "signin";
}
@GetMapping("/admin-login")
public String adminLogin() {
    return "admin-login";
}
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}