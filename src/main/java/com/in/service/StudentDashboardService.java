package com.in.service;

import com.in.model.User;
import com.in.repository.ApplicationRepository;
import com.in.repository.InterviewRepository;
import com.in.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentDashboardService {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;

    public Map<String, Object> getStudentDashboard(String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        long totalApplications = applicationRepository.countByUser(user);
        long approved = applicationRepository.countByUserAndStatus(user, "APPROVED");
        long rejected = applicationRepository.countByUserAndStatus(user, "REJECTED");
        long pending = applicationRepository.countByUserAndStatus(user, "APPLIED");

        long upcomingInterviews =
                interviewRepository.countByInterviewDateAfter(LocalDate.now());

        Map<String, Object> response = new HashMap<>();
        response.put("myApplications", totalApplications);
        response.put("approved", approved);
        response.put("rejected", rejected);
        response.put("pending", pending);
        response.put("upcomingInterviews", upcomingInterviews);

        return response;
    }
}