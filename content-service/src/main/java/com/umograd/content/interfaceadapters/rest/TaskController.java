package com.umograd.content.interfaceadapters.rest;

import com.umograd.content.application.dto.TaskDto;
import com.umograd.content.application.task.command.CreateTaskCommand;
import com.umograd.content.application.task.command.CreateTaskHandler;
import com.umograd.content.application.task.query.ListTasksForChildHandler;
import com.umograd.content.application.task.query.ListTasksForChildQuery;
import com.umograd.content.application.task.query.ListTasksHandler;
import com.umograd.content.application.task.query.ListTasksQuery;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final CreateTaskHandler createTaskHandler;
    private final ListTasksHandler listTasksHandler;
    private final ListTasksForChildHandler listTasksForChildHandler;

    public TaskController(CreateTaskHandler createTaskHandler,
                          ListTasksHandler listTasksHandler,
                          ListTasksForChildHandler listTasksForChildHandler) {
        this.createTaskHandler = createTaskHandler;
        this.listTasksHandler = listTasksHandler;
        this.listTasksForChildHandler = listTasksForChildHandler;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MODERATOR')")
    public TaskDto create(@RequestBody CreateTaskCommand cmd) {
        return createTaskHandler.handle(cmd);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MODERATOR','PARENT')")
    public List<TaskDto> list() {
        return listTasksHandler.handle(new ListTasksQuery());
    }

    @GetMapping("/for-child")
    @PreAuthorize("hasRole('CHILD')")
    public List<TaskDto> listForChild(@RequestParam int age,
                                      @RequestParam(required = false) String difficulty) {
        return listTasksForChildHandler.handle(new ListTasksForChildQuery(age, difficulty));
    }
}
