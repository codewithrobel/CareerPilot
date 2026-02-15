package com.in.service;

import com.in.repository.ApplicationRepository;
import com.in.repository.InterviewRepository;
import com.in.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;

    public DashboardService(UserRepository userRepository,
                            InterviewRepository interviewRepository,
                            ApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
        this.applicationRepository = applicationRepository;
    }

    public Map<String, Object> getAdminStats() {

        Map<String, Object> stats = new HashMap<>();

        // Users
        stats.put("totalUsers", userRepository.count());
        stats.put("totalStudents", userRepository.countByRole("STUDENT"));
        stats.put("totalAdmins", userRepository.countByRole("ADMIN"));

        // Interviews
        stats.put("totalInterviews", interviewRepository.count());

        // Applications
        stats.put("totalApplications", applicationRepository.count());
        stats.put("approvedApplications", applicationRepository.countByStatus("APPROVED"));
        stats.put("rejectedApplications", applicationRepository.countByStatus("REJECTED"));

        return stats;
    }
}