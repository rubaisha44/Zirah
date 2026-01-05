package com.internshipfinder.zirah.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.internshipfinder.zirah.model.Student;
import com.internshipfinder.zirah.service.StudentService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileDownloadController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/download/resume/{studentId}")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long studentId) {
        Student student = studentService.findById(studentId);
        if (student == null || student.getResumeFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = Paths.get(student.getResumeFilePath());
            Resource resource = new InputStreamResource(Files.newInputStream(filePath));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}