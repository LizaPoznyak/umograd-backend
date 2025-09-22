package com.umograd.content.domain.external;

import java.util.List;

public interface ContentProvider {
    record ExternalTask(
            String title,
            String description,
            int minAge,
            int maxAge,
            String difficulty
    ) {}

    List<ExternalTask> fetchTasks(String topic, int limit);
}
