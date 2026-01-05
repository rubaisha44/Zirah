package com.internshipfinder.zirah.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.internshipfinder.zirah.model.Internship;
import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long> {
    List<Internship> findByRecruiterId(Long recruiterId);
    List<Internship> findByTitleContainingIgnoreCase(String title);
    List<Internship> findByAppliedStudentIdsContaining(Long studentId);
}