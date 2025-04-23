package com.example.Task.Management.System.dtos.TaskRecurrence;

import com.example.Task.Management.System.domainservice.TaskRecurrenceFactory;

public record CreateRecurrenceRequest(
        TaskRecurrenceDto recurrenceDto,
        RecurrencePatternDto patternDto
) {
}
