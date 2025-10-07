package com.umograd.content.application.task.query;

import com.umograd.content.application.dto.TaskContentDto;
import com.umograd.content.application.dto.TaskDto;
import com.umograd.content.domain.task.Task;
import com.umograd.content.domain.task.TaskRepository;

import java.util.List;

public class ListTasksHandler {
    private final TaskRepository repository;

    public ListTasksHandler(TaskRepository repository) {
        this.repository = repository;
    }

    public List<TaskDto> handle(ListTasksQuery query) {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    private TaskDto toDto(Task task) {
        return new TaskDto(
                task.id() != null ? task.id().value() : null, // внутренний ID
                task.sourceId(),                              // внешний ID
                task.title().value(),
                task.description().value(),
                task.ageRange().min(),
                task.ageRange().max(),
                task.difficulty().name(),
                task.createdBy(),
                task.createdAt(),
                task.updatedAt(),
                new TaskContentDto(
                        task.content().type(),
                        task.content().question(),
                        task.content().options(),
                        task.content().answer()
                )
        );
    }
}
