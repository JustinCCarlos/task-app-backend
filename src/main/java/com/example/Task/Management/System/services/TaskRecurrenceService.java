package com.example.Task.Management.System.services;

import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;

public interface TaskRecurrenceService {
    TaskRecurrence findById(Long id);
    TaskRecurrenceDto createRecurrence(TaskRecurrenceDto recurrenceDto);
    void generateNextTask(TaskRecurrenceDto recurrenceDto, RecurrencePatternDto patternDto);
    void deleteTaskRecurrence(Long id);

}
