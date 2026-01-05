package com.internshipfinder.zirah.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.internshipfinder.zirah.model.Internship;
import com.internshipfinder.zirah.model.ApplicationStatus;
import com.internshipfinder.zirah.repository.InternshipRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Comparator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class InternshipService {

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private StudentService studentService;

    private final String RESUME_UPLOAD_DIR = "uploads/resumes/";

    public Internship saveInternship(Internship internship) {
        return internshipRepository.save(internship);
    }

    public List<Internship> getAllInternships() {
        return internshipRepository.findAll();
    }

    public List<Internship> getInternshipsByRecruiter(Long recruiterId) {
        return internshipRepository.findByRecruiterId(recruiterId);
    }

    public List<Internship> getInternshipsByStudent(Long studentId) {
        return internshipRepository.findByAppliedStudentIdsContaining(studentId);
    }

    public Internship getInternshipById(Long id) {
        return internshipRepository.findById(id).orElse(null);
    }

    public void applyForInternship(Long internshipId, Long studentId, MultipartFile resume) {
        Internship internship = getInternshipById(internshipId);
        if (internship != null) {
            // Ensure appliedStudentIds is initialized
            if (internship.getAppliedStudentIds() == null) {
                internship.setAppliedStudentIds(new java.util.ArrayList<>());
            }
            if (!internship.getAppliedStudentIds().contains(studentId)) {
                // Save resume file
                String resumePath = saveResumeFile(resume, studentId, internshipId);

                // Update student's resume path
                var student = studentService.findById(studentId);
                if (student != null) {
                    student.setResumeFilePath(resumePath);
                    studentService.saveStudent(student);
                }

                // Add student to applicants
                internship.getAppliedStudentIds().add(studentId);

                // Add application status
                ApplicationStatus appStatus = new ApplicationStatus(studentId);
                internship.getApplicationStatuses().add(appStatus);

                internshipRepository.save(internship);
            }
        }
    }

    private String saveResumeFile(MultipartFile file, Long studentId, Long internshipId) {
        try {
            Path uploadPath = Paths.get(RESUME_UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = studentId + "_" + internshipId + "_" + System.currentTimeMillis() + "_"
                    + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save resume file", e);
        }
    }

    public void updateApplicationStatus(Long internshipId, Long studentId, String status, String rejectionReason) {
        Internship internship = getInternshipById(internshipId);
        if (internship != null && internship.getApplicationStatuses() != null) {
            for (ApplicationStatus appStatus : internship.getApplicationStatuses()) {
                if (appStatus.getStudentId().equals(studentId)) {
                    appStatus.setStatus(status);
                    appStatus.setRejectionReason(rejectionReason);
                    appStatus.setDecisionDate(LocalDateTime.now());
                    break;
                }
            }
            internshipRepository.save(internship);
        }
    }

    public ApplicationStatus getApplicationStatus(Long internshipId, Long studentId) {
        Internship internship = getInternshipById(internshipId);
        if (internship != null && internship.getApplicationStatuses() != null) {
            return internship.getApplicationStatuses().stream()
                    .filter(status -> status.getStudentId().equals(studentId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public List<Long> getApplicantsForInternship(Long internshipId) {
        Internship internship = getInternshipById(internshipId);
        return (internship != null && internship.getAppliedStudentIds() != null)
                ? internship.getAppliedStudentIds()
                : java.util.List.of();
    }

    // Add these methods to calculate statistics for the dashboard
    public long getTotalApplicationsForRecruiter(Long recruiterId) {
        List<Internship> internships = getInternshipsByRecruiter(recruiterId);
        return internships.stream()
                .mapToLong(internship -> {
                    if (internship.getAppliedStudentIds() != null) {
                        return internship.getAppliedStudentIds().size();
                    }
                    return 0L;
                })
                .sum();
    }

    public long getActiveInternshipsCount(Long recruiterId) {
        List<Internship> internships = getInternshipsByRecruiter(recruiterId);
        return internships.stream()
                .filter(internship -> internship.getDeadline() != null
                        && internship.getDeadline().isAfter(LocalDateTime.now()))
                .count();
    }

    public List<Internship> getRecentInternships(Long recruiterId) {
        List<Internship> internships = getInternshipsByRecruiter(recruiterId);

        // Fix: Use Comparator with proper null handling
        return internships.stream()
                .sorted(Comparator.comparing(
                        Internship::getPostedDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();
    }
}