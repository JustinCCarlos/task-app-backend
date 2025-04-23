package com.example.Task.Management.System.mappers;

import com.example.Task.Management.System.dtos.Task.TaskDto;
import com.example.Task.Management.System.models.Category;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import com.example.Task.Management.System.services.CategoryService;
import com.example.Task.Management.System.models.Task;
import com.example.Task.Management.System.services.TaskRecurrenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public TaskDto toDto(Task task){
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
                .taskRecurrenceId((task.getTaskRecurrence() != null) ? task.getTaskRecurrence().getTaskRecurrenceId() : null)
                .build();
    }

    public Task toEntity(TaskDto taskDto, Category category, TaskRecurrence recurrence) {
        return Task.builder()
                .taskId(taskDto.getTaskId())
                .title(taskDto.getTitle())
                .completed(taskDto.isCompleted())
                .category(category)
                .priority(taskDto.getPriority())
                .startDate(taskDto.getStartDate())
                .endDate(taskDto.getEndDate())
                .finishedDate(taskDto.getFinishedDate())
                .overdue(taskDto.isOverdue())
                .taskRecurrence(recurrence)
                .build();
    }

    public void updateEntity(Task existingTask, TaskDto taskDto, Category category){
        if (taskDto.getTitle() != null) existingTask.setTitle(taskDto.getTitle());
        if (taskDto.getPriority() != null) existingTask.setPriority(taskDto.getPriority());
        if (taskDto.getStartDate() != null) existingTask.setStartDate(taskDto.getStartDate());
        if (taskDto.getEndDate() != null) existingTask.setEndDate(taskDto.getEndDate());
        if (taskDto.getFinishedDate() != null) existingTask.setFinishedDate(taskDto.getFinishedDate());

        existingTask.setCompleted(taskDto.isCompleted());
        existingTask.setCategory(category);
    }
}
