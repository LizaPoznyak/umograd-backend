package com.umograd.analytic.service.impl;

import com.umograd.analytic.dto.TaskAnalyticsResponse;
import com.umograd.analytic.mapper.AnalyticsMapper;
import com.umograd.analytic.repository.task.TaskRepository;
import com.umograd.analytic.service.AnalyticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultAnalyticService implements AnalyticService {

    private final TaskRepository taskRepository;

    private final AnalyticsMapper analyticsMapper;

    @Override
    public List<TaskAnalyticsResponse> getTaskStats() {
        return taskRepository.findAll().stream()
                .map(analyticsMapper::toResponse)
                .collect(Collectors.toList());
    }
}
