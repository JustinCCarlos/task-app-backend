package com.example.Task.Management.System.services;

import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;

public interface TaskRecurrenceService {
    TaskRecurrence findById(Long id);
    TaskRecurrenceDto createRecurrenceForExistingTask(TaskRecurrenceDto recurrenceDto, RecurrencePatternDto patternDto);
    void generateNextTask(TaskRecurrenceDto recurrenceDto);
    void deleteTaskRecurrence(Long id);

}
