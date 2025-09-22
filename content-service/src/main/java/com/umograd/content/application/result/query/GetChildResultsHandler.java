package com.umograd.content.application.result.query;

import com.umograd.content.application.dto.TaskResultDto;
import com.umograd.content.domain.result.TaskResultRepository;

import java.util.List;

public class GetChildResultsHandler {
    private final TaskResultRepository repository;

    public GetChildResultsHandler(TaskResultRepository repository) {
        this.repository = repository;
    }

    public List<TaskResultDto> handle(GetChildResultsQuery q) {
        return repository.findByChildId(q.childId()).stream()
                .map(r -> new TaskResultDto(
                        r.id() != null ? r.id().value() : null,
                        r.taskId().value(),
                        r.childId(),
                        r.status().name(),
                        r.score(),
                        r.finishedAt() != null ? r.finishedAt().toString() : null
                ))
                .toList();
    }
}
