package com.umograd.content.domain.result;

import com.umograd.content.domain.task.TaskId;

import java.time.LocalDateTime;
import java.util.Objects;

public class TaskResult {
    private final TaskResultId id;
    private final TaskId taskId;
    private final Long childId;
    private TaskResultStatus status;
    private Integer score;
    private LocalDateTime finishedAt;

    // приватный конструктор
    private TaskResult(TaskResultId id,
                       TaskId taskId,
                       Long childId,
                       TaskResultStatus status,
                       Integer score,
                       LocalDateTime finishedAt) {
        this.id = id;
        this.taskId = Objects.requireNonNull(taskId);
        this.childId = Objects.requireNonNull(childId);
        this.status = Objects.requireNonNull(status);
        this.score = score;
        this.finishedAt = finishedAt;
    }

    // --- Фабрики ---

    /** Создание нового результата (ребёнок начал задание) */
    public static TaskResult createNew(TaskId taskId, Long childId) {
        return new TaskResult(null, taskId, childId, TaskResultStatus.IN_PROGRESS, null, null);
    }

    /** Восстановление из БД (для маппера) */
    public static TaskResult restore(TaskResultId id,
                                     TaskId taskId,
                                     Long childId,
                                     TaskResultStatus status,
                                     Integer score,
                                     LocalDateTime finishedAt) {
        return new TaskResult(id, taskId, childId, status, score, finishedAt);
    }

    // --- Методы поведения ---

    /** Завершить задание */
    public void markFinished(int score, LocalDateTime finishedAt) {
        if (this.status == TaskResultStatus.DONE) {
            throw new IllegalStateException("Задание уже завершено");
        }
        this.status = TaskResultStatus.DONE;
        this.score = score;
        this.finishedAt = finishedAt;
    }

    /** Обновить оценку (например, если пересчитали результат) */
    public void updateScore(int newScore) {
        if (this.status != TaskResultStatus.DONE) {
            throw new IllegalStateException("Нельзя обновить оценку для незавершённого задания");
        }
        this.score = newScore;
    }

    // --- Геттеры ---
    public TaskResultId id() { return id; }
    public TaskId taskId() { return taskId; }
    public Long childId() { return childId; }
    public TaskResultStatus status() { return status; }
    public Integer score() { return score; }
    public LocalDateTime finishedAt() { return finishedAt; }
}
