package com.umograd.content.infrastructure.persistence.mapper;

import com.umograd.content.domain.task.*;
import com.umograd.content.infrastructure.persistence.jpa.TaskJpaEntity;

public class TaskMapper {
    public static TaskJpaEntity toJpa(Task task) {
        var e = new TaskJpaEntity();
        if (task.id() != null) e.setId(task.id().value());
        e.setTitle(task.title().value());
        e.setDescription(task.description().value());
        e.setCreatedBy(task.createdBy());
        e.setCreatedAt(task.createdAt());
        e.setUpdatedAt(task.updatedAt());
        e.setMinAge(task.ageRange().min());
        e.setMaxAge(task.ageRange().max());
        e.setDifficulty(task.difficulty());
        return e;
    }

    public static Task toDomain(TaskJpaEntity e) {
        return new Task(
                e.getId() != null ? new TaskId(e.getId()) : null,
                new TaskTitle(e.getTitle()),
                new TaskDescription(e.getDescription()),
                e.getCreatedBy(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                new AgeRange(e.getMinAge(), e.getMaxAge()),
                e.getDifficulty()
        );
    }
}
