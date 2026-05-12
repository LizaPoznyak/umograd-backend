package com.umograd.analytic.controller;

import com.umograd.analytic.dto.TaskAnalyticsResponse;
import com.umograd.analytic.service.AnalyticService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticController {

    private final AnalyticService analyticsService;

    @GetMapping("/tasks")
    public List<TaskAnalyticsResponse> getTaskStats() {
        return analyticsService.getTaskStats();
    }
}
