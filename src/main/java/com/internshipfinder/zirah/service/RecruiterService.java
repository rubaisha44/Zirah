package com.internshipfinder.zirah.service;

import com.internshipfinder.zirah.model.Recruiter;
import com.internshipfinder.zirah.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RecruiterService {
    
    @Autowired
    private RecruiterRepository repo;

    public Recruiter saveRecruiter(Recruiter r) {
        return repo.save(r);
    }

    public List<Recruiter> getAll() {
        return repo.findAll();
    }

    public Recruiter findByEmail(String email) {
        List<Recruiter> recruiters = repo.findByEmail(email);
        return (recruiters != null && !recruiters.isEmpty()) ? recruiters.get(0) : null;
    }

    public Recruiter findById(Long id) {
        Optional<Recruiter> recruiter = repo.findById(id);
        return recruiter.orElse(null);
    }
    public long getRecruiterCount() {
    return repo.count();
}
}