package com.internshipfinder.zirah.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.internshipfinder.zirah.model.Student;
import com.internshipfinder.zirah.service.StudentService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/save")
    public String saveStudent(@ModelAttribute Student student, Model model) {
        try {
            studentService.saveStudent(student);
            model.addAttribute("success", "Student registered successfully!");
            return "signin";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Email already exists! Please use a different email.");
            model.addAttribute("student", new Student());
            model.addAttribute("recruiter", new Object());
            return "signup";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            model.addAttribute("student", new Student());
            model.addAttribute("recruiter", new Object());
            return "signup";
        }
    }

    @GetMapping("/profile")
    public String showProfileForm(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/signin";
        }
        
        // Get fresh data from database
        Student freshStudent = studentService.findByEmail(student.getEmail());
        model.addAttribute("student", freshStudent);
        return "student-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Student updatedStudent, 
                               HttpSession session, 
                               Model model) {
        Student currentStudent = (Student) session.getAttribute("user");
        if (currentStudent == null) {
            return "redirect:/signin";
        }

        try {
            // Get existing student
            Student existingStudent = studentService.findByEmail(currentStudent.getEmail());
            
            // Update fields
            existingStudent.setName(updatedStudent.getName());
            existingStudent.setDegree(updatedStudent.getDegree());
            existingStudent.setUniversity(updatedStudent.getUniversity());
            existingStudent.setSkills(updatedStudent.getSkills());
            
            // Save updated student
            Student savedStudent = studentService.saveStudent(existingStudent);
            
            // Update session
            session.setAttribute("user", savedStudent);
            model.addAttribute("student", savedStudent);
            model.addAttribute("success", "Profile updated successfully!");
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update profile: " + e.getMessage());
            Student current = (Student) session.getAttribute("user");
            model.addAttribute("student", current);
        }
        
        return "student-profile";
    }
}