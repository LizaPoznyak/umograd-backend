package com.umograd.content.application.task.command;

import com.umograd.content.application.dto.TaskDto;
import com.umograd.content.domain.task.*;
import java.time.LocalDateTime;

public class CreateTaskHandler {
    private final TaskRepository repository;

    public CreateTaskHandler(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskDto handle(CreateTaskCommand cmd) {
        var task = Task.createNew(
                null,
                new TaskTitle(cmd.title()),
                new TaskDescription(cmd.description()),
                cmd.createdBy(),
                LocalDateTime.now(),
                new AgeRange(cmd.minAge(), cmd.maxAge()),
                Difficulty.valueOf(cmd.difficulty().toUpperCase())
        );

        var saved = repository.save(task);

        return new TaskDto(
                saved.id() != null ? saved.id().value() : null,
                saved.title().value(),
                saved.description().value(),
                saved.createdBy(),
                saved.createdAt().toString(),
                saved.updatedAt() != null ? saved.updatedAt().toString() : null,
                saved.ageRange().min(),
                saved.ageRange().max(),
                saved.difficulty().name()
        );
    }
}
