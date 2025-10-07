package com.umograd.content.domain.external;

import java.util.List;

/**
 * Универсальное DTO для задач, импортируемых из внешних источников.
 */
public record ExternalTaskDto(
        String sourceId,                  // внешний идентификатор задачи (например, "opentdb-19-12345")
        String title,                     // заголовок
        String description,               // описание или условие
        int minAge,                       // минимальный возраст
        int maxAge,                       // максимальный возраст
        String difficulty,                // уровень сложности (easy, medium, hard)
        List<String> tags,                // список тем/тегов
        List<String> options,             // варианты ответа (для тестов)
        String correctAnswer,             // правильный ответ (если применимо)
        ExternalTaskContentDto content    // вложенный объект с медиа/текстом
) {}
