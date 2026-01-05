package com.internshipfinder.zirah.service;

import com.internshipfinder.zirah.model.Student;
import com.internshipfinder.zirah.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repo; // This is named 'repo'

    public Student saveStudent(Student s) {
        return repo.save(s);
    }

    public List<Student> getAll() {
        return repo.findAll();
    }

    public Student findByEmail(String email) {
        List<Student> students = repo.findByEmail(email);
        return students.isEmpty() ? null : students.get(0);
    }

    public Student findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<Student> getStudentsByIds(List<Long> ids) {
        return repo.findAllById(ids);
    }

    public void updateResumeFilePath(Long studentId, String resumeFilePath) {
        Student student = findById(studentId);
        if (student != null) {
            student.setResumeFilePath(resumeFilePath);
            repo.save(student);
        }
    }

    // FIXED: Use 'repo' instead of 'studentRepository'
    public long getStudentCount() {
        return repo.count();
    }
}