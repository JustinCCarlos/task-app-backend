package com.example.Task.Management.System.dtos.Task;

import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskDurationDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import jakarta.validation.Valid;

public record CreateTaskWithRecurrenceRequest(
        @Valid TaskDto task,
        @Valid TaskRecurrenceDto recurrence,
        @Valid RecurrencePatternDto pattern
) {
}
