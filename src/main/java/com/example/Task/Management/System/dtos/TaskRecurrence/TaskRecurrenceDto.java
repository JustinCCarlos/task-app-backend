package com.example.Task.Management.System.dtos.TaskRecurrence;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskRecurrenceDto(
        Long taskRecurrenceId,

        @NotNull(message = "Start date is required")
        LocalDateTime recurrenceStartDate,

        LocalDateTime recurrenceEndDate,

        @Min(value = 1, message = "Max occurrences must be at least 1")
        Integer maxOccurrences,

        @NotNull
        Boolean active,

        Long recurrencePatternId
) {}
