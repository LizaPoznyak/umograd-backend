package com.umograd.content.application.result.command;

public record FinishTaskCommand(
        Long taskResultId,
        int score,
        Long childId
) {}
