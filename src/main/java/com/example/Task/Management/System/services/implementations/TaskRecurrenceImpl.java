package com.example.Task.Management.System.services.implementations;

import com.example.Task.Management.System.domainservice.TaskFactory;
import com.example.Task.Management.System.domainservice.TaskRecurrenceFactory;
import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.mappers.TaskRecurrenceMapper;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.repository.RecurrencePatternRepository;
import com.example.Task.Management.System.repository.TaskRepository;
import com.example.Task.Management.System.services.TaskRecurrenceService;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import com.example.Task.Management.System.models.Task;
import com.example.Task.Management.System.repository.TaskRecurrenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class TaskRecurrenceImpl implements TaskRecurrenceService {

    private static final Logger logger = LoggerFactory.getLogger(TaskRecurrenceImpl.class);

    private final TaskRecurrenceRepository recurrenceRepository;
    private final RecurrencePatternRepository patternRepository;
    private final TaskRepository taskRepository;
    private final TaskFactory taskFactory;
    private final TaskRecurrenceFactory recurrenceFactory;
    private final TaskRecurrenceMapper recurrenceMapper;

    @Autowired
    public TaskRecurrenceImpl(
            TaskRecurrenceRepository recurrenceRepository, RecurrencePatternRepository patternRepository,
            TaskRepository taskRepository,
            TaskFactory taskFactory,
            TaskRecurrenceFactory recurrenceFactory, TaskRecurrenceMapper recurrenceMapper)
    {
        this.recurrenceRepository = recurrenceRepository;
        this.patternRepository = patternRepository;
        this.taskRepository = taskRepository;
        this.taskFactory = taskFactory;
        this.recurrenceFactory = recurrenceFactory;
        this.recurrenceMapper = recurrenceMapper;
    }

    public TaskRecurrence findById(Long id){
        return recurrenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recurrence not found with id " + id ));
    }

    public TaskRecurrenceDto createRecurrenceForExistingTask(TaskRecurrenceDto recurrenceDto, RecurrencePatternDto patternDto) {
        TaskRecurrence taskRecurrence = recurrenceFactory.createRecurrence(recurrenceDto, patternDto);

        patternRepository.save(taskRecurrence.getRecurrencePattern());
        TaskRecurrence savedRecurrence = recurrenceRepository.save(taskRecurrence);
        return recurrenceMapper.toDto(savedRecurrence);
    }

    public void generateNextTask(TaskRecurrenceDto recurrenceDto) {
        try{
            logger.info("Generating next task...");
            RecurrencePattern pattern = patternRepository.findById(recurrenceDto.recurrencePatternId())
                    .orElseThrow(() -> new EntityNotFoundException("Pattern not found with id: " + recurrenceDto.recurrencePatternId()));
            TaskRecurrence taskRecurrence = recurrenceMapper.toEntity(recurrenceDto, pattern);
            Task currentTask = taskRecurrence.getGeneratedTasks().getLast();

            Task nextTask = taskFactory.createNextTask(currentTask, taskRecurrence);

            taskRepository.save(nextTask);

            taskRecurrence.getGeneratedTasks().add(nextTask);

            logger.info("Next task generated with dates: start={}, end={}", nextTask.getStartDate(), nextTask.getEndDate());
        } catch (Exception e) {
            logger.error("Error generating next task: ", e);
        }


    }

    public void deleteTaskRecurrence(Long id){
        if (!recurrenceRepository.existsById(id)){
            throw new EntityNotFoundException("Recurrence not found with id: " + id);
        }
        recurrenceRepository.deleteById(id);
    }

}
