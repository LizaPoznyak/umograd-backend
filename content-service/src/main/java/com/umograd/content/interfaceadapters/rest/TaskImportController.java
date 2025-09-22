package com.umograd.content.interfaceadapters.rest;

import com.umograd.content.application.dto.TaskDto;
import com.umograd.content.application.task.command.ImportTasksCommand;
import com.umograd.content.application.task.command.ImportTasksHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks/import")
public class TaskImportController {

    private final ImportTasksHandler importTasksHandler;

    public TaskImportController(ImportTasksHandler importTasksHandler) {
        this.importTasksHandler = importTasksHandler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MODERATOR')")
    public List<TaskDto> importFromProvider(@RequestParam String provider,
                                            @RequestParam String topic,
                                            @RequestParam(defaultValue = "10") int limit,
                                            @RequestParam String createdBy) {
        return importTasksHandler.handle(new ImportTasksCommand(provider, topic, limit, createdBy));
    }
}
