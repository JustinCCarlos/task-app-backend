package com.example.Task.Management.System.services;


import com.example.Task.Management.System.dtos.Task.TaskDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {
    List<TaskDto> getAllTasks();
    TaskDto getTaskById(Long id);
    List<TaskDto> getTaskContaining(String toSearch);
    TaskDto createTask(TaskDto taskDto);
    TaskDto createTaskWithRecurrence(TaskDto taskDto, TaskRecurrenceDto recurrenceDto, RecurrencePatternDto patternDto);
    TaskDto updateTask(Long id, TaskDto taskDto);
    List<TaskDto> getFilteredTasks(Boolean isComplete, String title, String sortBy, String sortDirection, LocalDateTime startDate, LocalDateTime endDate);
    void deleteTask(Long id);
}
