package com.umograd.content.application.task.command;

public record CreateTaskCommand(
        String title,
        String description,
        String createdBy,
        int minAge,
        int maxAge,
        String difficulty
) {}
