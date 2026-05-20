package com.revtalent.recruitment_service.controller;

import com.revtalent.recruitment_service.service.CandidateDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
public class CandidateDashboardController {

    private final CandidateDashboardService dashboardService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        return ResponseEntity.ok(dashboardService.getProfile(auth.getName()));
    }

    @GetMapping("/applications")
    public ResponseEntity<?> getApplications(Authentication auth) {
        return ResponseEntity.ok(dashboardService.getApplications(auth.getName()));
    }

    @GetMapping("/interviews/upcoming")
    public ResponseEntity<?> getUpcomingInterviews(Authentication auth) {
        return ResponseEntity.ok(dashboardService.getUpcomingInterviews(auth.getName()));
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<?>> getNotifications() {
        return ResponseEntity.ok(List.of());
    }
}