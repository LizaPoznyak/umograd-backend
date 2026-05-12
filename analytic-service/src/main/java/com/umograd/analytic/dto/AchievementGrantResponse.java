package com.umograd.analytic.dto;

public record AchievementGrantResponse(
        String name,
        String description,
        String iconUrl,
        boolean newlyEarned
) {}

