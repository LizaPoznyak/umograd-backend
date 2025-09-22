package com.umograd.content.application.dto;

public record TaskDto(
        Long id,
        String title,
        String description,
        String createdBy,
        String createdAt,
        String updatedAt,
        int minAge,
        int maxAge,
        String difficulty
) {}
