package com.umograd.content.application.task.query;

import com.umograd.content.application.dto.TaskDto;
import com.umograd.content.domain.task.Difficulty;
import com.umograd.content.domain.task.TaskRepository;

import java.util.List;

public class ListTasksForChildHandler {
    private final TaskRepository repository;

    public ListTasksForChildHandler(TaskRepository repository) {
        this.repository = repository;
    }

    public List<TaskDto> handle(ListTasksForChildQuery q) {
        return repository.findAll().stream()
                .filter(task -> task.ageRange().includes(q.age()))
                .filter(task -> q.difficulty() == null
                        || task.difficulty() == Difficulty.valueOf(q.difficulty().toUpperCase()))
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
