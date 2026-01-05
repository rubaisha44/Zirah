package com.internshipfinder.zirah.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Internship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String requirements;
    private String location;
    private String duration;
    private String stipend;

    private LocalDateTime postedDate;
    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "recruiter_id")
    private Recruiter recruiter;

    @ElementCollection
    private List<Long> appliedStudentIds = new ArrayList<>();

    // Add this field for application statuses
    @ElementCollection
    private List<ApplicationStatus> applicationStatuses = new ArrayList<>();

    // Add constructor to initialize dates
    public Internship() {
        this.postedDate = LocalDateTime.now();
    }

    // Add this getter method if Lombok @Data is not working properly
    public List<Long> getAppliedStudentIds() {
        if (this.appliedStudentIds == null) {
            this.appliedStudentIds = new ArrayList<>();
        }
        return this.appliedStudentIds;
    }

    // Add getter for application statuses
    public List<ApplicationStatus> getApplicationStatuses() {
        if (this.applicationStatuses == null) {
            this.applicationStatuses = new ArrayList<>();
        }
        return this.applicationStatuses;
    }
}