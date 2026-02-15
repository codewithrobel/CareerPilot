package com.in.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardResponse {

    private long totalUsers;
    private long totalStudents;
    private long totalAdmins;

    private long totalInterviews;

    private long totalApplications;
    private long approvedApplications;
    private long rejectedApplications;
    private long pendingApplications;
}