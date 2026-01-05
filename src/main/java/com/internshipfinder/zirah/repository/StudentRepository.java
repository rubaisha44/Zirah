package com.internshipfinder.zirah.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.internshipfinder.zirah.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByEmail(String email);
}