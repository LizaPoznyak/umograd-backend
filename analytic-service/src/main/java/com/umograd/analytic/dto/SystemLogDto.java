package com.umograd.analytic.dto;

import java.time.LocalDateTime;

public record SystemLogDto (
        Long id,
        Long userId,
        String username,
        String eventType,
        String description,
        LocalDateTime createdAt
){
}
