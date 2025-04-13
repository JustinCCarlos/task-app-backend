package com.example.Task.Management.System.dtos.Task;

import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskDurationDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;

public record CreateTaskWithRecurrenceRequest(
        TaskDto task,
        TaskRecurrenceDto recurrence,
        RecurrencePatternDto pattern
) {
}
