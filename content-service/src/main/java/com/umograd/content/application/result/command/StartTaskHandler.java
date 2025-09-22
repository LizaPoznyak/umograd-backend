package com.umograd.content.application.result.command;

import com.umograd.content.application.dto.TaskResultDto;
import com.umograd.content.domain.result.TaskResult;
import com.umograd.content.domain.result.TaskResultRepository;
import com.umograd.content.domain.task.TaskId;

public class StartTaskHandler {
    private final TaskResultRepository repository;

    public StartTaskHandler(TaskResultRepository repository) {
        this.repository = repository;
    }

    public TaskResultDto handle(StartTaskCommand cmd) {
        var result = TaskResult.createNew(new TaskId(cmd.taskId()), cmd.childId());
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
