package com.example.Task.Management.System.Service;


import com.example.Task.Management.System.DTO.Task.TaskDto;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {
    List<TaskDto> getAllTasks();
    TaskDto getTaskById(Long id);
    List<TaskDto> getTaskContaining(String toSearch);
    TaskDto addTask(
            TaskDto taskDto);
    TaskDto updateTask(Long id, TaskDto taskDto);
    List<TaskDto> getFilteredTasks(Boolean isComplete, String title, String sortBy, String sortDirection, LocalDateTime startDate, LocalDateTime endDate);
    void deleteTask(Long id);
}
