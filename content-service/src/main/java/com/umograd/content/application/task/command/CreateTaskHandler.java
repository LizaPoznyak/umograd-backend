package com.umograd.content.application.task.command;

import com.umograd.content.application.dto.TaskContentDto;
import com.umograd.content.application.dto.TaskDto;
import com.umograd.content.domain.task.*;
import com.umograd.content.domain.task.TaskRepository;

import java.time.LocalDateTime;

public class CreateTaskHandler {
    private final TaskRepository repository;

    public CreateTaskHandler(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskDto handle(CreateTaskCommand cmd) {
        var now = LocalDateTime.now();

        var task = Task.createNew(
                null, // внутренний ID назначит БД
                null, // sourceId отсутствует при ручном создании
                new TaskTitle(cmd.title()),
                new TaskDescription(cmd.description()),
                cmd.createdBy(),
                now,
                new AgeRange(cmd.minAge(), cmd.maxAge()),
                Difficulty.valueOf(cmd.difficulty()),
                new TaskContent(
                        cmd.content().type(),
                        cmd.content().question(),
                        cmd.content().options(),
                        cmd.content().answer()
                )
        );

        var saved = repository.save(task);

        return new TaskDto(
                saved.id() != null ? saved.id().value() : null, // внутренний ID
                saved.sourceId(),                               // внешний ID (null для ручных задач)
                saved.title().value(),
                saved.description().value(),
                saved.ageRange().min(),
                saved.ageRange().max(),
                saved.difficulty().name(),
                saved.createdBy(),
                saved.createdAt(),
                saved.updatedAt(),
                new TaskContentDto(
                        saved.content().type(),
                        saved.content().question(),
                        saved.content().options(),
                        saved.content().answer()
                )
        );
    }
}
