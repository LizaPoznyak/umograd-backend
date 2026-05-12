package com.umograd.analytic.service;

import com.umograd.analytic.dto.TaskAnalyticsResponse;

import java.util.List;

public interface AnalyticService {

    List<TaskAnalyticsResponse> getTaskStats();
}
