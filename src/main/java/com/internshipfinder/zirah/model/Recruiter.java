package com.internshipfinder.zirah.model;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Recruiter extends User {
    private String companyName;
    private String position;
    
    @OneToMany(mappedBy = "recruiter")
    private List<Internship> internships = new ArrayList<>();
}