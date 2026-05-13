package com.umograd.analytic.service.impl;

import com.umograd.analytic.dto.DifficultyRecommendation;
import com.umograd.analytic.dto.TaskAnalyticsResponse;
import com.umograd.analytic.entity.task.TaskJpaEntity;
import com.umograd.analytic.entity.task.TaskResultEntity;
import com.umograd.analytic.mapper.AnalyticsMapper;
import com.umograd.analytic.repository.task.TaskRepository;
import com.umograd.analytic.repository.task.TaskResultRepository;
import com.umograd.analytic.service.AnalyticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultAnalyticService implements AnalyticService {

    private final TaskRepository taskRepository;

    private final TaskResultRepository taskResultRepository;

    private final AnalyticsMapper analyticsMapper;

    @Override
    public List<TaskAnalyticsResponse> getTaskStats() {
        return taskRepository.findAll().stream()
                .map(analyticsMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DifficultyRecommendation getRecommendation(Long childId) {
        List<TaskResultEntity> lastResults = taskResultRepository.findLastFinishedResults(childId);

        if (lastResults.isEmpty()) {
            return new DifficultyRecommendation("EASY", "Начните с простого уровня заданий.");
        }

        double totalSeconds = 0;
        double totalScore = 0;

        for (TaskResultEntity res : lastResults) {
            long seconds = Duration.between(res.getStartedAt(), res.getFinishedAt()).toSeconds();
            totalSeconds += seconds;
            totalScore += (res.getScore() != null ? res.getScore() : 0);
        }

        double avgTime = totalSeconds / lastResults.size();
        double avgScore = totalScore / lastResults.size();

        String currentDiff = "EASY";
        Optional<TaskJpaEntity> lastTask = taskRepository.findById(lastResults.get(0).getTaskId());
        if (lastTask.isPresent()) {
            currentDiff = lastTask.get().getDifficulty().toString();
        }

        if (avgTime < 30 && avgScore >= 100) {
            String nextDiff = currentDiff.equals("EASY") ? "MEDIUM" : "HARD";
            return new DifficultyRecommendation(nextDiff, "Отличный результат! Скорость мышления на высоте. Уровень сложности повышен.");
        } else if (avgScore < 50) {
            String prevDiff = currentDiff.equals("HARD") ? "MEDIUM" : "EASY";
            return new DifficultyRecommendation(prevDiff, "Задания вызывают трудности. Рекомендуем повторить материал или снизить уровень сложности.");
        }

        return new DifficultyRecommendation(currentDiff, "Хороший темп! Продолжайте заниматься в том же режиме.");
    }
}
