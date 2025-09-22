package com.umograd.content.application.task.query;

import com.umograd.content.application.dto.TaskDto;
import com.umograd.content.domain.task.TaskRepository;

import java.util.List;

public class ListTasksHandler {
    private final TaskRepository repository;

    public ListTasksHandler(TaskRepository repository) {
        this.repository = repository;
    }

    public List<TaskDto> handle(ListTasksQuery q) {
        return repository.findAll().stream()
                .map(t -> new TaskDto(
                        t.id() != null ? t.id().value() : null,
                        t.title().value(),
                        t.description().value(),
                        t.createdBy(),
                        t.createdAt().toString(),
                        t.updatedAt() != null ? t.updatedAt().toString() : null,
                        t.ageRange().min(),
                        t.ageRange().max(),
                        t.difficulty().name()
                ))
                .toList();
    }
}
