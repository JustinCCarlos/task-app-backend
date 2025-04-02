package com.example.Task.Management.System.Service;

import com.example.Task.Management.System.DTO.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.DTO.TaskRecurrence.TaskRecurrenceMapper;
import com.example.Task.Management.System.models.Recurrence.TaskRecurrence;
import com.example.Task.Management.System.repository.TaskRecurrenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class TaskRecurrenceService {

    private final TaskRecurrenceRepository taskRecurrenceRepository;

    @Autowired
    public TaskRecurrenceService(TaskRecurrenceRepository taskRecurrenceRepository){
        this.taskRecurrenceRepository = taskRecurrenceRepository;
    }

    public TaskRecurrenceDto createRecurrence(TaskRecurrenceDto recurrenceDto) {
        TaskRecurrence taskRecurrence = TaskRecurrenceMapper.toEntity(recurrenceDto);
        TaskRecurrence savedRecurrence = taskRecurrenceRepository.save(taskRecurrence);
        return TaskRecurrenceMapper.toDto(savedRecurrence);
    }

    public void deleteTaskRecurrence(Long id){
        if (!taskRecurrenceRepository.existsById(id)){
            throw new EntityNotFoundException("Recurrence not found with id: " + id);
        }
        taskRecurrenceRepository.deleteById(id);
    }

}
