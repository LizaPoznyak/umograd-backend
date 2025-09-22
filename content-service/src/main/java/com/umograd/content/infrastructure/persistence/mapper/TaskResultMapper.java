package com.umograd.content.infrastructure.persistence.mapper;

import com.umograd.content.domain.result.*;
import com.umograd.content.domain.task.TaskId;
import com.umograd.content.infrastructure.persistence.jpa.TaskResultJpaEntity;

public class TaskResultMapper {
    public static TaskResultJpaEntity toJpa(TaskResult r) {
        var e = new TaskResultJpaEntity();
        if (r.id() != null) e.setId(r.id().value());
        e.setTaskId(r.taskId().value());
        e.setChildId(r.childId());
        e.setStatus(r.status().name());
        e.setScore(r.score());
        e.setFinishedAt(r.finishedAt());
        return e;
    }

    public static TaskResult toDomain(TaskResultJpaEntity e) {
        return TaskResult.restore(
                e.getId() != null ? new TaskResultId(e.getId()) : null,
                new TaskId(e.getTaskId()),
                e.getChildId(),
                TaskResultStatus.valueOf(e.getStatus()),
                e.getScore(),
                e.getFinishedAt()
        );
    }
}
