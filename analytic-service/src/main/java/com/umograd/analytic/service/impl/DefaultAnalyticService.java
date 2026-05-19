package com.umograd.analytic.service.impl;

import com.umograd.analytic.dto.DifficultyRecommendation;
import com.umograd.analytic.dto.ParentAgeLimitResponse;
import com.umograd.analytic.dto.TaskAnalyticsResponse;
import com.umograd.analytic.entity.ParentAgeLimitEntity;
import com.umograd.analytic.entity.ParentRecommendationEntity;
import com.umograd.analytic.entity.task.TaskJpaEntity;
import com.umograd.analytic.entity.task.TaskResultEntity;
import com.umograd.analytic.mapper.AnalyticsMapper;
import com.umograd.analytic.mapper.LimitMapper;
import com.umograd.analytic.repository.analytic.ParentAgeLimitRepository;
import com.umograd.analytic.repository.analytic.ParentRecommendationRepository;
import com.umograd.analytic.repository.task.TaskRepository;
import com.umograd.analytic.repository.task.TaskResultRepository;
import com.umograd.analytic.security.AuthenticationHolder;
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

    private final ParentRecommendationRepository parentRecommendationRepository;

    private final ParentAgeLimitRepository limitRepository;

    private final AnalyticsMapper analyticsMapper;

    private final LimitMapper limitMapper;

    @Override
    public List<TaskAnalyticsResponse> getTaskStats() {
        return taskRepository.findAll().stream()
                .map(analyticsMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DifficultyRecommendation getRecommendation(Long childId) {
        List<Long> parentAssignedTaskIds = parentRecommendationRepository.findActiveTaskIdsByChildId(childId);

        String parentText = null;
        if (!parentAssignedTaskIds.isEmpty()) {
            List<TaskJpaEntity> assignedTasks = taskRepository.findAllById(parentAssignedTaskIds);
            String taskTitles = assignedTasks.stream()
                    .map(TaskJpaEntity::getTitle)
                    .collect(Collectors.joining("\", \""));
            parentText = "Родитель зафиксировал для вас приоритетные задания: \"" + taskTitles + "\".";
        }

        List<TaskResultEntity> lastResults = taskResultRepository.findLastFinishedResults(childId);
        if (lastResults.isEmpty()) {
            return new DifficultyRecommendation("EASY", parentText != null ? parentText : "Начните с простого уровня заданий.", parentAssignedTaskIds);
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

        String calculatedDiff = currentDiff;
        String aiText = "Хороший темп! Продолжайте заниматься в том же режиме.";

        if (avgTime < 30 && avgScore >= 100) {
            calculatedDiff = currentDiff.equals("EASY") ? "MEDIUM" : "HARD";
            aiText = "Отличный результат! Скорость мышления на высоте. Уровень сложности повышен.";
        } else if (avgScore < 50) {
            calculatedDiff = currentDiff.equals("HARD") ? "MEDIUM" : "EASY";
            aiText = "Задания вызывают трудности. Рекомендуем повторить материал или снизить уровень сложности.";
        }

        return new DifficultyRecommendation(
                calculatedDiff,
                parentText != null ? parentText : aiText,
                parentAssignedTaskIds
        );
    }


    @Override
    @Transactional
    public void recommendMultiple(Long childId, List<Long> taskIds) {
        Long parentId = AuthenticationHolder.getUserId();

        List<ParentRecommendationEntity> activeRecs = parentRecommendationRepository.findAllByChildIdAndIsCompleted(childId, false);
        parentRecommendationRepository.deleteAll(activeRecs);

        if (taskIds != null && !taskIds.isEmpty()) {
            List<ParentRecommendationEntity> newRecs = taskIds.stream().map(taskId -> {
                ParentRecommendationEntity rec = new ParentRecommendationEntity();
                rec.setParentId(parentId);
                rec.setChildId(childId);
                rec.setTaskId(taskId);
                rec.setCompleted(false);
                return rec;
            }).toList();
            parentRecommendationRepository.saveAll(newRecs);
        }
    }

    @Override
    public void deleteRecommendation(Long childId) {
        parentRecommendationRepository.findActiveTaskIdsByChildId(childId).stream()
                .findFirst()
                .flatMap(taskId -> parentRecommendationRepository
                        .findByChildIdAndTaskIdAndIsCompleted(childId, taskId, false)).ifPresent(rec -> {
                            rec.setCompleted(true);
                            parentRecommendationRepository.save(rec);
                        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getActiveTasksId(Long childId) {
        return parentRecommendationRepository.findActiveTaskIdsByChildId(childId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParentAgeLimitResponse> getLimits() {
        Long parentId = AuthenticationHolder.getUserId();
        return limitMapper.toListDto(limitRepository.findAllByParentId(parentId));
    }

    @Override
    @Transactional
    public void saveLimit(int age, int maxMinutes) {
        Long parentId = AuthenticationHolder.getUserId();
        ParentAgeLimitEntity limit = limitRepository.findByParentIdAndAge(parentId, age)
                .orElse(new ParentAgeLimitEntity(null, parentId, age, maxMinutes));
        limit.setMaxMinutes(maxMinutes);
        limitRepository.save(limit);
    }
}
