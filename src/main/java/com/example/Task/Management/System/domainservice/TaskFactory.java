package com.example.Task.Management.System.domainservice;

import com.example.Task.Management.System.dtos.Task.TaskDto;
import com.example.Task.Management.System.mappers.TaskMapper;
import com.example.Task.Management.System.models.Category;
import com.example.Task.Management.System.models.Task;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TaskFactory {
    private static final Logger logger = LoggerFactory.getLogger(TaskFactory.class);

    private final TaskMapper taskMapper;
    private final TaskRecurrenceCalculator calculator;

    public Task createTask(TaskDto taskDto, Category category){
        try {
            validateTask(taskDto);
        } catch (ValidationException e) {
            logger.error("Validation failed while creating task", e);
            throw new RuntimeException(e);
        }

        return taskMapper.toEntity(taskDto, category, null);
    }

    public Task createTask(TaskDto taskDto, Category category, TaskRecurrence taskRecurrence) {
        try {
            validateTask(taskDto);

            if (taskDto.getEndDate() != null) {
                LocalDateTime nextStart = calculator.calculateNextStartDate(
                        taskDto.getStartDate(), taskRecurrence.getRecurrencePattern()
                );

                if (taskDto.getEndDate().isAfter(nextStart)) {
                    throw new ValidationException("Start date must be before end date");
                }
            }

            if (taskDto.getEndDate() == null) {
                taskDto.setEndDate(calculator.calculateNextEndDate(
                        taskDto.getStartDate(), taskRecurrence.getRecurrencePattern()
                ));
            }
        } catch (ValidationException e) {
            logger.error("Validation failed while creating task", e);
            throw new RuntimeException(e);
        }

        return taskMapper.toEntity(taskDto, category, taskRecurrence);
    }

    public Task createNextTask(Task currentTask, TaskRecurrence recurrence){
        LocalDateTime nextStartDate = calculator.calculateNextStartDate(currentTask.getStartDate(), recurrence.getRecurrencePattern());
        LocalDateTime nextEndDate = calculator.calculateNextEndDate(nextStartDate, recurrence.getRecurrencePattern());

        return currentTask.toBuilder()
                .startDate(nextStartDate)
                .endDate(nextEndDate)
                .taskRecurrence(recurrence)
                .completed(false)
                .finishedDate(null)
                .overdue(false)
                .build();
    }

    private void validateTask(TaskDto taskDto) throws ValidationException {
        if(taskDto.getStartDate() == null) {
            taskDto.setStartDate(LocalDateTime.now());
            logger.info("Task start date not found. Setting start date with date:{}", taskDto.getStartDate());
        }

        if (taskDto.getPriority() == null) {
            taskDto.setPriority(3);
            logger.info("Task priority is null. Setting priority to 3");
        }

        if (taskDto.getPriority() < 1 || taskDto.getPriority() > 5){
            throw new ValidationException("Priority must be between 1 and 5");
        }

        if(taskDto.getEndDate() != null && taskDto.getStartDate().isAfter(taskDto.getEndDate())){
            throw new ValidationException("Start date must be before end date");
        }
    }

    public void validateUpdate(Task task) throws ValidationException{
        if (task.getPriority() < 1 || task.getPriority() > 5){
            throw new ValidationException("Priority must be between 1 and 5");
        }

        if (task.getStartDate() != null && task.getEndDate() != null &&
        task.getStartDate().isAfter(task.getEndDate())){
            throw new ValidationException("Start date must be before end date");
        }
    }

}
