package com.example.Task.Management.System.services.implementations;

import com.example.Task.Management.System.domainservice.TaskRecurrenceCalculator;
import com.example.Task.Management.System.dtos.Task.TaskDto;
import com.example.Task.Management.System.mappers.TaskMapper;
import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.mappers.RecurrencePatternMapper;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.mappers.TaskRecurrenceMapper;
import com.example.Task.Management.System.services.TaskRecurrenceService;
import com.example.Task.Management.System.services.TaskService;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import com.example.Task.Management.System.models.Task;
import com.example.Task.Management.System.repository.TaskRecurrenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Service
public class TaskRecurrenceImpl implements TaskRecurrenceService {

    private static final Logger logger = LoggerFactory.getLogger(TaskRecurrenceImpl.class);

    private final TaskRecurrenceRepository taskRecurrenceRepository;
    private final TaskService taskService;
    private final TaskRecurrenceCalculator calculator;

    @Autowired
    public TaskRecurrenceImpl(
            TaskRecurrenceRepository taskRecurrenceRepository,
            TaskService taskService,
            TaskRecurrenceCalculator calculator)
    {
        this.taskRecurrenceRepository = taskRecurrenceRepository;
        this.taskService = taskService;
        this.calculator = calculator;
    }

    public TaskRecurrence findById(Long id){
        return taskRecurrenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recurrence not found with id " + id ));
    }

    public TaskRecurrenceDto createRecurrence(TaskRecurrenceDto recurrenceDto) {
        TaskRecurrence taskRecurrence = TaskRecurrenceMapper.toEntity(recurrenceDto);

        TaskRecurrence savedRecurrence = taskRecurrenceRepository.save(taskRecurrence);
        return TaskRecurrenceMapper.toDto(savedRecurrence);
    }

    public void generateNextTask(TaskRecurrenceDto recurrenceDto, RecurrencePatternDto patternDto) {
        try{
            logger.info("Generating next task...");
            TaskRecurrence taskRecurrence = TaskRecurrenceMapper.toEntity(recurrenceDto);
            RecurrencePattern pattern = RecurrencePatternMapper.toEntity(patternDto);
            Task currentTask = taskRecurrence.getGeneratedTasks().getLast();

            LocalDateTime nextStartDate = calculator.calculateNextStartDate(currentTask.getStartDate(), pattern);
            LocalDateTime nextEndDate = calculator.calculateNextEndDate(nextStartDate, pattern);

            Task nextTask = currentTask.toBuilder()
                    .completed(false)
                    .startDate(nextStartDate)
                    .endDate(nextEndDate)
                    .finishedDate(null)
                    .overdue(false)
                    .taskRecurrence(taskRecurrence)
                    .build();
            TaskDto nextTaskDto = TaskMapper.toDto(nextTask);

            taskService.createTask(nextTaskDto);
            logger.info("Next task generated with dates: start={}, end{}", nextStartDate, nextEndDate);
        } catch (Exception e) {
            logger.error("Error generating next task: ", e);
        }


    }

    public void deleteTaskRecurrence(Long id){
        if (!taskRecurrenceRepository.existsById(id)){
            throw new EntityNotFoundException("Recurrence not found with id: " + id);
        }
        taskRecurrenceRepository.deleteById(id);
    }

}
