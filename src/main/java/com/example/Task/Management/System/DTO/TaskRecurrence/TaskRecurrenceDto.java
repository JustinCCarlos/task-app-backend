package com.example.Task.Management.System.DTO.TaskRecurrence;

import com.example.Task.Management.System.models.Recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.Recurrence.RecurrenceType;
import com.example.Task.Management.System.models.Recurrence.TaskDuration;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskRecurrenceDto(
        Long taskRecurrenceId,

        @NotNull(message = "Recurrence type is required")
        RecurrenceType recurrenceType,

        @Min(value = 1, message = "Interval must be at least 1")
        @Max(value = 99, message = "Interval cannot exceed 99")
        int interval,

        @NotNull(message = "Start date is required")
        LocalDateTime recurrenceStartDate,

        TaskDuration taskDuration,

        LocalDateTime recurrenceEndDate,

        @Min(value = 1, message = "Max occurrences must be at least 1")
        Integer maxOccurrences,

        Boolean active,

        RecurrencePattern recurrencePattern
) {}
