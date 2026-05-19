package com.umograd.analytic.controller;

import com.umograd.analytic.dto.ChildProgressPoint;
import com.umograd.analytic.dto.DifficultyRecommendation;
import com.umograd.analytic.dto.ParentAgeLimitResponse;
import com.umograd.analytic.dto.TaskAnalyticsResponse;
import com.umograd.analytic.repository.analytic.ParentAgeLimitRepository;
import com.umograd.analytic.service.AnalyticService;
import com.umograd.analytic.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticController {

    private final AnalyticService analyticsService;

    private final ReportService reportService;

    private final ParentAgeLimitRepository limitRepository;

    @GetMapping("/tasks")
    public List<TaskAnalyticsResponse> getTaskStats() {
        return analyticsService.getTaskStats();
    }

    @GetMapping("/report/{childId}")
    public List<ChildProgressPoint> getReport(@PathVariable Long childId,
                                              @RequestParam(defaultValue = "month") String period) {
        return reportService.getChildReport(childId, period);
    }

    @GetMapping("/recommendation/{childId}")
    public DifficultyRecommendation getRecommendation(@PathVariable Long childId) {
        return analyticsService.getRecommendation(childId);
    }

    @PostMapping("/recommend-multiple")
    public void recommendTask(@RequestParam Long childId, @RequestBody List<Long> taskIds) {
        analyticsService.recommendMultiple(childId, taskIds);
    }

    @DeleteMapping("/recommend")
    public void removeRecommendation(@RequestParam Long childId) {
        analyticsService.deleteRecommendation(childId);
    }

    @GetMapping("/active-recs/{childId}")
    public List<Long> getActiveRecommendations(@PathVariable Long childId) {
        return analyticsService.getActiveTasksId(childId);
    }

    @GetMapping
    public List<ParentAgeLimitResponse> getLimits() {
        return analyticsService.getLimits();
    }

    @PostMapping
    public void saveLimit(@RequestParam int age, @RequestParam int maxMinutes) {
        analyticsService.saveLimit(age, maxMinutes);
    }

    @PostMapping("/report/aggregate")
    public Map<Long, List<ChildProgressPoint>> getAggregateReport(@RequestBody List<Long> childIds) {
        return childIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> reportService.getChildReport(id, "month")
                ));
    }
}
