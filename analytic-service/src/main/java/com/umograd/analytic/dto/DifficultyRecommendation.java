package com.umograd.analytic.dto;

import java.util.List;

public record DifficultyRecommendation(
        String recommendedDifficulty,
        String message,
        List<Long> parentTaskIds
) {
    public DifficultyRecommendation(String difficulty, String text) {
        this(difficulty, text, java.util.Collections.emptyList());
    }
}

