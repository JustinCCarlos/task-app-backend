package com.example.Task.Management.System.controllers;

import com.example.Task.Management.System.DTO.TaskDto;
import com.example.Task.Management.System.Service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Add a new task
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        TaskDto savedTaskDto = taskService.addTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTaskDto);
    }

    // List all tasks
    @GetMapping
    public List<TaskDto> getAllTasks(){
        return taskService.getAllTasks();
    }

    // Get a single task by ID
    @GetMapping("/{id}")
    public TaskDto getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    // Get a list of task that contains searched string
    @GetMapping("/Search")
    public List<TaskDto> getTaskContaining(@RequestParam("query") String query) {
        return taskService.getTaskContaining(query);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDto taskDto){
        TaskDto updatedTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TaskDto>> getFilteredTasks(
            @RequestParam(required = false) Boolean isComplete,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ){
        List<TaskDto> filteredTasks = taskService.getFilteredTasks(isComplete, title, sortBy, sortDirection, startDate, endDate);
        return ResponseEntity.ok(filteredTasks);
    }

    //Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted successfully.");
    }

}
