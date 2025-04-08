package com.example.Task.Management.System.controllers;

import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.services.TaskRecurrenceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recurrence")
public class TaskRecurrenceController {
    private final TaskRecurrenceService taskRecurrenceService;

    @Autowired
    public TaskRecurrenceController(TaskRecurrenceService recurrenceService){
        this.taskRecurrenceService = recurrenceService;
    }

    @PostMapping
    public ResponseEntity<TaskRecurrenceDto> createRecurrence(@Valid @RequestBody TaskRecurrenceDto recurrenceDto){
        TaskRecurrenceDto savedRecurrenceDto = taskRecurrenceService.createRecurrence(recurrenceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRecurrenceDto);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteRecurrence(@PathVariable Long id){
        taskRecurrenceService.deleteTaskRecurrence(id);
        return ResponseEntity.ok("Recurrence deleted successfully");
    }

}
