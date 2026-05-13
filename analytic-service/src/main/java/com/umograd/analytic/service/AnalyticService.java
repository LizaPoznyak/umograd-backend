package com.umograd.analytic.service;

import com.umograd.analytic.dto.DifficultyRecommendation;
import com.umograd.analytic.dto.TaskAnalyticsResponse;

import java.util.List;

public interface AnalyticService {

    List<TaskAnalyticsResponse> getTaskStats();

    DifficultyRecommendation getRecommendation(Long childId);
}
