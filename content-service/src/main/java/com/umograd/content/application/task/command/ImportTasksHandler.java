package com.umograd.content.application.task.command;

import com.umograd.content.application.dto.TaskDto;
import com.umograd.content.domain.external.ContentProvider;
import com.umograd.content.domain.task.*;

import java.time.LocalDateTime;
import java.util.List;

public class ImportTasksHandler {
    private final TaskRepository taskRepository;
    private final ContentProvider contentProvider;

    public ImportTasksHandler(TaskRepository taskRepository, ContentProvider contentProvider) {
        this.taskRepository = taskRepository;
        this.contentProvider = contentProvider;
    }

    public List<TaskDto> handle(ImportTasksCommand cmd) {
        var external = contentProvider.fetchTasks(cmd.topic(), cmd.limit());
        var now = LocalDateTime.now();

        return external.stream()
                .map(et -> Task.createNew(
                        null,
                        new TaskTitle(et.title()),
                        new TaskDescription(et.description()),
                        cmd.createdBy(),
                        now,
                        new AgeRange(et.minAge(), et.maxAge()),
                        Difficulty.valueOf(et.difficulty().toUpperCase())
                ))
                .map(taskRepository::save)
                .map(saved -> new TaskDto(
                        saved.id() != null ? saved.id().value() : null,
                        saved.title().value(),
                        saved.description().value(),
                        saved.createdBy(),
                        saved.createdAt().toString(),
                        saved.updatedAt() != null ? saved.updatedAt().toString() : null,
                        saved.ageRange().min(),
                        saved.ageRange().max(),
                        saved.difficulty().name()
                ))
                .toList();
    }
}
