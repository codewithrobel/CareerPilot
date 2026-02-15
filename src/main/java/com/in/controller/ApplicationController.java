package com.in.controller;

import com.in.model.Application;
import com.in.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.in.dto.UpdateApplicationStatusRequest;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // STUDENT apply karega
    @PostMapping("/{interviewId}")
    @PreAuthorize("hasRole('STUDENT')")
    public Application applyForInterview(@PathVariable Long interviewId,
                                         Authentication authentication) {

        String email = authentication.getName();

        return applicationService.applyForInterview(email, interviewId);
    }

    // STUDENT apne applications dekhe
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public List<Application> getMyApplications(Authentication authentication) {

        String email = authentication.getName();

        return applicationService.getMyApplications(email);
    }

    // ADMIN kisi interview ke applications dekhe
    @GetMapping("/interview/{interviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Application> getApplicationsByInterview(@PathVariable Long interviewId) {

        return applicationService.getApplicationsByInterview(interviewId);
    }

    // ADMIN approve/reject application
    @PutMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Application> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {

        Application updated = applicationService
                .updateApplicationStatus(applicationId, request.getStatus());

        return ResponseEntity.ok(updated);
    }
}