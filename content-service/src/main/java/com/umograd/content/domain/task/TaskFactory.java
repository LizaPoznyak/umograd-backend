package com.umograd.content.domain.task;

import java.time.LocalDateTime;
import java.util.Objects;

public final class TaskFactory {

    public Task create(TaskTitle title,
                       TaskDescription description,
                       String createdBy,
                       LocalDateTime now,
                       AgeRange ageRange,
                       Difficulty difficulty) {

        Objects.requireNonNull(title);
        Objects.requireNonNull(description);
        Objects.requireNonNull(createdBy);
        Objects.requireNonNull(ageRange);
        Objects.requireNonNull(difficulty);

        // ID = null, потому что его назначит инфраструктура (БД)
        return Task.createNew(null, title, description, createdBy, now, ageRange, difficulty);
    }
}
