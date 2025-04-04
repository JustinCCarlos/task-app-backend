package com.example.Task.Management.System.DTO.Task;

import com.example.Task.Management.System.DTO.CategoryDto;
import com.example.Task.Management.System.Service.CategoryService;
import com.example.Task.Management.System.models.Task;

public class TaskMapper {
    private static CategoryService categoryService;

    public static TaskDto toDto(Task task){
        return TaskDto.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .completed(task.isCompleted())
                .categoryId((task.getCategory() != null) ? task.getCategory().getCategoryId() : null)
                .priority(task.getPriority())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .finishedDate(task.getFinishedDate())
                .overdue(task.isOverdue())
                .build();
    }

    public static Task toEntity(TaskDto taskDto) {
        return Task.builder()
                .taskId(taskDto.getTaskId())
                .title(taskDto.getTitle())
                .completed(taskDto.isCompleted())
                .category((taskDto.getCategoryId() != null) ? categoryService.findById(taskDto.getCategoryId()) : null)
                .priority(taskDto.getPriority())
                .startDate(taskDto.getStartDate())
                .endDate(taskDto.getEndDate())
                .finishedDate(taskDto.getFinishedDate())
                .overdue(taskDto.isOverdue())
                .build();
    }
}
