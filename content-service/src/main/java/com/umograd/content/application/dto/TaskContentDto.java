package com.umograd.content.application.dto;

import java.util.List;

public record TaskContentDto(
        String type,
        String question,
        List<String> options,
        String answer
) {}
