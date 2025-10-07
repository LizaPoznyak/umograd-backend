package com.umograd.content.application.task.command;

import com.umograd.content.application.dto.TaskContentDto;
import com.umograd.content.application.dto.TaskDto;
import com.umograd.content.domain.external.ContentProvider;
import com.umograd.content.domain.external.ExternalTaskDto;
import com.umograd.content.domain.task.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ImportTasksHandler {

    private final TaskRepository taskRepository;
    private final Map<String, ContentProvider> providers;

    public ImportTasksHandler(TaskRepository taskRepository, Map<String, ContentProvider> providers) {
        this.taskRepository = Objects.requireNonNull(taskRepository);
        this.providers = Objects.requireNonNull(providers);
    }

    public List<TaskDto> handle(ImportTasksCommand cmd) {
        ContentProvider provider = providers.get(cmd.providerName());
        if (provider == null) {
            throw new IllegalArgumentException("Unknown provider: " + cmd.providerName());
        }

        List<ExternalTaskDto> externalTasks = provider.fetchTasks(cmd.topic(), cmd.limit());
        LocalDateTime now = LocalDateTime.now();

        return externalTasks.stream()
                .map(et -> toDomainTask(et, cmd.createdBy(), now))
                .map(taskRepository::save)
                .map(this::toDto)
                .toList();
    }

    private Task toDomainTask(ExternalTaskDto et, String createdBy, LocalDateTime now) {
        return Task.createNew(
                null,
                et.sourceId(), // 👈 сохраняем внешний идентификатор
                new TaskTitle(et.title()),
                new TaskDescription(et.description()),
                createdBy,
                now,
                new AgeRange(et.minAge(), et.maxAge()),
                Difficulty.valueOf(et.difficulty().toUpperCase()),
                new TaskContent(
                        et.content().type(),
                        et.content().question(),
                        et.content().options(),
                        et.content().answer()
                )
        );
    }

    private TaskDto toDto(Task task) {
        return new TaskDto(
                task.id() != null ? task.id().value() : null,
                task.sourceId(), // 👈 возвращаем внешний ID
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

    public List<ExternalTaskDto> preview(String providerName, String topic, int limit) {
        ContentProvider provider = providers.get(providerName);
        System.out.println("Available providers: " + providers.keySet());
        if (provider == null) {
            throw new IllegalArgumentException("Unknown provider: " + providerName);
        }
        return provider.fetchTasks(topic, limit);
    }

    public TaskDto saveSingle(TaskDto dto, String createdBy) {
        LocalDateTime now = LocalDateTime.now();
        Task task = Task.createNew(
                null,
                dto.sourceId(), // 👈 сохраняем внешний ID, если он пришёл
                new TaskTitle(dto.title()),
                new TaskDescription(dto.description()),
                createdBy,
                now,
                new AgeRange(dto.minAge(), dto.maxAge()),
                Difficulty.valueOf(dto.difficulty().toUpperCase()),
                new TaskContent(
                        dto.content().type(),
                        dto.content().question(),
                        dto.content().options(),
                        dto.content().answer()
                )
        );
        Task saved = taskRepository.save(task);
        return toDto(saved);
    }
}
