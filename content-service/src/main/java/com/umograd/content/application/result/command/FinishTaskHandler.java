package com.umograd.content.application.result.command;

import com.umograd.content.application.dto.TaskResultDto;
import com.umograd.content.domain.result.TaskResultId;
import com.umograd.content.domain.result.TaskResultRepository;

import java.time.LocalDateTime;

public class FinishTaskHandler {
    private final TaskResultRepository repository;

    public FinishTaskHandler(TaskResultRepository repository) {
        this.repository = repository;
    }

    public TaskResultDto handle(FinishTaskCommand cmd) {
        var result = repository.findById(new TaskResultId(cmd.taskResultId()))
                .orElseThrow(() -> new IllegalArgumentException("TaskResult not found"));

        if (!result.childId().equals(cmd.childId())) {
            throw new SecurityException("Child cannot finish someone else's task");
        }

        result.markFinished(cmd.score(), LocalDateTime.now());
        var saved = repository.save(result);

        return new TaskResultDto(
                saved.id() != null ? saved.id().value() : null,
                saved.taskId().value(),
                saved.childId(),
                saved.status().name(),
                saved.score(),
                saved.finishedAt() != null ? saved.finishedAt().toString() : null
        );
    }
}
