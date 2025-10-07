package com.umograd.content.domain.external;

import java.util.List;

/**
 * DTO для содержимого внешней задачи.
 */
public record ExternalTaskContentDto(
        String type,            // тип задачи (например, "quiz", "text", "video")
        String question,        // основной текст/вопрос
        List<String> options,   // варианты ответа
        String answer           // правильный ответ
) {}
