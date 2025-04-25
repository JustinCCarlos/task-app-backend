package com.example.Task.Management.System.mappers;

import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.services.RecurrencePatternService;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class TaskRecurrenceMapper {
    public TaskRecurrenceDto toDto(TaskRecurrence entity) {
        return TaskRecurrenceDto.builder()
                .taskRecurrenceId(entity.getTaskRecurrenceId())
                .recurrenceStartDate(entity.getRecurrenceStartDate())
                .recurrenceEndDate(entity.getRecurrenceEndDate())
                .maxOccurrences(entity.getMaxOccurrences())
                .active(entity.getActive())
                .recurrencePatternId(entity.getTaskRecurrenceId())
                .build();
    }

    public TaskRecurrence toEntity(TaskRecurrenceDto dto, RecurrencePattern pattern) {
        return TaskRecurrence.builder()
                .taskRecurrenceId(dto.taskRecurrenceId())
                .recurrenceStartDate(dto.recurrenceStartDate())
                .recurrenceEndDate(dto.recurrenceEndDate())
                .maxOccurrences(dto.maxOccurrences())
                .active(dto.active())
                .recurrencePattern(pattern)
                .build();
    }
}
