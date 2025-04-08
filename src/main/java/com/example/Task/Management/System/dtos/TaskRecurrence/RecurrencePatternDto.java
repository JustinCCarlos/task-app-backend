package com.example.Task.Management.System.dtos.TaskRecurrence;

import com.example.Task.Management.System.models.recurrence.RecurrenceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.DayOfWeek;
import java.util.List;

@Builder
public record RecurrencePatternDto(
    Long recurrencePatternId,

    @Min(value = 1, message = "Interval must be at least 1")
    @Max(value = 99, message = "Interval cannot exceed 99")
    int interval,

    @NotNull(message = "Recurrence Type cannot be empty")
    RecurrenceType recurrenceType,

    @Valid
    TaskDurationDto taskDurationDto,

    List<DayOfWeek> daysOfWeek,

    String monthDayRule

) {}
