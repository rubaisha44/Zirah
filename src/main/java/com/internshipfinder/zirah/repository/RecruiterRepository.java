package com.internshipfinder.zirah.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.internshipfinder.zirah.model.Recruiter;
import java.util.List;

public interface RecruiterRepository extends JpaRepository<Recruiter, Long> {
    List<Recruiter> findByEmail(String email);
}