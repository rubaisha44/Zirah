package com.internshipfinder.zirah.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Embeddable
public class ApplicationStatus {
    private Long studentId;
    private String status; // PENDING, ACCEPTED, REJECTED
    private String rejectionReason;
    private LocalDateTime appliedDate;
    private LocalDateTime decisionDate;

    public ApplicationStatus() {
        this.appliedDate = LocalDateTime.now();
        this.status = "PENDING";
    }

    public ApplicationStatus(Long studentId) {
        this();
        this.studentId = studentId;
    }
}