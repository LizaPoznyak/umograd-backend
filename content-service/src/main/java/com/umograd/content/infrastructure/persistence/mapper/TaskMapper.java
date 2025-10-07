package com.umograd.content.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umograd.content.domain.task.*;
import com.umograd.content.infrastructure.persistence.jpa.TaskJpaEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class TaskMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static TaskJpaEntity toJpa(Task task) {
        var e = new TaskJpaEntity();
        if (task.id() != null) {
            e.setId(task.id().value());
        }
        e.setSourceId(task.sourceId()); // 👈 сохраняем внешний идентификатор
        e.setTitle(task.title().value());
        e.setDescription(task.description().value());
        e.setCreatedBy(task.createdBy());
        e.setCreatedAt(task.createdAt());
        e.setUpdatedAt(task.updatedAt());
        e.setMinAge(task.ageRange().min());
        e.setMaxAge(task.ageRange().max());
        e.setDifficulty(task.difficulty());
        if (task.content() != null) {
            e.setContentType(task.content().type());
            e.setQuestion(task.content().question());
            try {
                e.setOptions(objectMapper.writeValueAsString(task.content().options())); // List -> JSON
            } catch (JsonProcessingException ex) {
                throw new RuntimeException("Failed to serialize options", ex);
            }
            e.setAnswer(task.content().answer());
        }
        return e;
    }

    public static Task toDomain(TaskJpaEntity e) {
        List<String> options = Collections.emptyList();
        if (e.getOptions() != null) {
            try {
                options = objectMapper.readValue(
                        e.getOptions(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
                );
            } catch (IOException ex) {
                throw new RuntimeException("Failed to deserialize options", ex);
            }
        }

        return Task.createNew(
                e.getId() != null ? new TaskId(e.getId()) : null,
                e.getSourceId(), // 👈 восстанавливаем внешний идентификатор
                new TaskTitle(e.getTitle()),
                new TaskDescription(e.getDescription()),
                e.getCreatedBy(),
                e.getCreatedAt(),
                new AgeRange(e.getMinAge(), e.getMaxAge()),
                e.getDifficulty(),
                new TaskContent(
                        e.getContentType(),
                        e.getQuestion(),
                        options, // JSON -> List<String>
                        e.getAnswer()
                )
        );
    }
}
