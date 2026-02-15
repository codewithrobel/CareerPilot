package com.in.controller;

import com.in.model.Interview;
import com.in.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    // ADMIN interview create karega
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Interview createInterview(@RequestBody Interview interview) {
        return interviewService.createInterview(interview);
    }

    // Pagination + Search + Sorting
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    public Page<Interview> getAllInterviews(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return interviewService.getAllInterviews(
                keyword,
                page,
                size,
                sortBy,
                direction
        );
    }

}