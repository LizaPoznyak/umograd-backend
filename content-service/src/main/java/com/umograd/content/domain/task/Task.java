package com.umograd.content.domain.task;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private final TaskId id;          // внутренний ID (из БД)
    private final String sourceId;    // внешний ID (например, "opentdb-19-12345")

    private TaskTitle title;
    private TaskDescription description;
    private final String createdBy;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private AgeRange ageRange;
    private Difficulty difficulty;
    private TaskContent content;

    private Task(TaskId id,
                 String sourceId,
                 TaskTitle title,
                 TaskDescription description,
                 String createdBy,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt,
                 AgeRange ageRange,
                 Difficulty difficulty,
                 TaskContent content) {
        this.id = id;
        this.sourceId = sourceId;
        this.title = Objects.requireNonNull(title);
        this.description = Objects.requireNonNull(description);
        this.createdBy = Objects.requireNonNull(createdBy);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = updatedAt;
        this.ageRange = Objects.requireNonNull(ageRange);
        this.difficulty = Objects.requireNonNull(difficulty);
        this.content = Objects.requireNonNull(content);
    }

    public static Task createNew(TaskId id,
                                 String sourceId,
                                 TaskTitle title,
                                 TaskDescription description,
                                 String createdBy,
                                 LocalDateTime now,
                                 AgeRange ageRange,
                                 Difficulty difficulty,
                                 TaskContent content) {
        return new Task(id, sourceId, title, description, createdBy, now, null, ageRange, difficulty, content);
    }

    // --- Методы поведения ---
    public void rename(TaskTitle newTitle, TaskDescription newDescription, LocalDateTime now) {
        this.title = Objects.requireNonNull(newTitle);
        this.description = Objects.requireNonNull(newDescription);
        this.updatedAt = now;
    }

    public void changeDifficulty(Difficulty newDifficulty, LocalDateTime now) {
        this.difficulty = Objects.requireNonNull(newDifficulty);
        this.updatedAt = now;
    }

    public void updateAgeRange(AgeRange newRange, LocalDateTime now) {
        this.ageRange = Objects.requireNonNull(newRange);
        this.updatedAt = now;
    }

    public void updateContent(TaskContent newContent, LocalDateTime now) {
        this.content = Objects.requireNonNull(newContent);
        this.updatedAt = now;
    }

    // --- Геттеры ---
    public TaskId id() { return id; }
    public String sourceId() { return sourceId; }
    public TaskTitle title() { return title; }
    public TaskDescription description() { return description; }
    public String createdBy() { return createdBy; }
    public LocalDateTime createdAt() { return createdAt; }
    public LocalDateTime updatedAt() { return updatedAt; }
    public AgeRange ageRange() { return ageRange; }
    public Difficulty difficulty() { return difficulty; }
    public TaskContent content() { return content; }
}
