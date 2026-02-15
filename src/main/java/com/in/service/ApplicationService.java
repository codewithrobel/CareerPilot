package com.in.service;

import com.in.model.*;
import com.in.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;

    // Student apply karega
    public Application applyForInterview(String email, Long interviewId) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        // 🔴 DUPLICATE CHECK
        if (applicationRepository.existsByUserAndInterview(user, interview)) {
            throw new RuntimeException("You have already applied for this interview");
        }

        Application application = new Application();
        application.setUser(user);
        application.setInterview(interview);
        application.setStatus("APPLIED");

        return applicationRepository.save(application);
    }

    // Student apne applications dekhe
    public List<Application> getMyApplications(String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return applicationRepository.findByUser(user);
    }

    // Admin kisi interview ke applications dekhe
    public List<Application> getApplicationsByInterview(Long interviewId) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        return applicationRepository.findByInterview(interview);
    }
    // ADMIN application ka status update karega
    public Application updateApplicationStatus(Long applicationId, String status) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Only APPROVED or REJECTED allowed
        if (!status.equals("APPROVED") && !status.equals("REJECTED")) {
            throw new RuntimeException("Invalid status value");
        }

        application.setStatus(status);

        return applicationRepository.save(application);
    }
}