package com.example.Task.Management.System.DTO.TaskRecurrence;

import com.example.Task.Management.System.models.Recurrence.TaskRecurrence;

public class TaskRecurrenceMapper {
    public static TaskRecurrenceDto toDto(TaskRecurrence entity) {
        return new TaskRecurrenceDto(
                entity.getTaskRecurrenceId(),
                entity.getRecurrenceType(),
                entity.getInterval(),
                entity.getRecurrenceStartDate(),
                entity.getTaskDuration(),
                entity.getRecurrenceEndDate(),
                entity.getMaxOccurrences(),
                entity.getActive(),
                entity.getRecurrencePattern()
        );
    }

    public static TaskRecurrence toEntity(TaskRecurrenceDto dto) {
        return TaskRecurrence.builder()
                .recurrenceType(dto.recurrenceType())
                .interval(dto.interval())
                .recurrenceStartDate(dto.recurrenceStartDate())
                .taskDuration(dto.taskDuration())
                .recurrenceEndDate(dto.recurrenceEndDate())
                .maxOccurrences(dto.maxOccurrences())
                .active(dto.active() != null ? dto.active() : true) // Default to true if null
                .recurrencePattern(dto.recurrencePattern())
                .build();
    }
}
