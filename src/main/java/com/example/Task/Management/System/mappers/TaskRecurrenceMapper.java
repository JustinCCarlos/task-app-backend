package com.example.Task.Management.System.mappers;

import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.services.RecurrencePatternService;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskRecurrenceMapper {
    private static RecurrencePatternService pattern;
    public static TaskRecurrenceDto toDto(TaskRecurrence entity) {
        return new TaskRecurrenceDto(
                entity.getTaskRecurrenceId(),
                entity.getRecurrenceStartDate(),
                entity.getRecurrenceEndDate(),
                entity.getMaxOccurrences(),
                entity.getActive(),
                entity.getRecurrencePattern().getRecurrencePatternId()
        );
    }

    public static TaskRecurrence toEntity(TaskRecurrenceDto dto) {
        return TaskRecurrence.builder()
                .recurrenceStartDate(dto.recurrenceStartDate())
                .recurrenceEndDate(dto.recurrenceEndDate())
                .maxOccurrences(dto.maxOccurrences())
                .active(dto.active() != null ? dto.active() : true) // Default to true if null
                .recurrencePattern(pattern.findById(dto.recurrencePatternId()))
                .build();
    }
}
