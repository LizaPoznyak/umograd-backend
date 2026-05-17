package com.umograd.analytic.dto;

public record AchievementResponse(
        Long id,
        String name,
        String description,
        String iconUrl,
        Integer conditionValue
) {
}
