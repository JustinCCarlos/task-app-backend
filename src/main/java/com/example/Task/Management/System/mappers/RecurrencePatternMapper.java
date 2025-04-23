package com.example.Task.Management.System.mappers;

import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskDurationDto;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.recurrence.TaskDuration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecurrencePatternMapper {
    private final TaskDurationMapper durationMapper;
    public RecurrencePatternDto toDto(RecurrencePattern entity, TaskDurationDto durationDto){
        return RecurrencePatternDto.builder()
                .recurrencePatternId(entity.getRecurrencePatternId())
                .interval(entity.getInterval())
                .recurrenceType(entity.getRecurrenceType())
                .taskDurationDto(durationDto)
                .daysOfWeek(entity.getDaysOfWeek())
                .monthDayRule(entity.getMonthDayRule())
                .build();
    }

    public RecurrencePattern toEntity(RecurrencePatternDto dto, TaskDuration duration){
        return RecurrencePattern.builder()
                .recurrencePatternId(dto.recurrencePatternId())
                .interval(dto.interval())
                .recurrenceType(dto.recurrenceType())
                .taskDuration(duration)
                .daysOfWeek(dto.daysOfWeek())
                .monthDayRule(dto.monthDayRule())
                .build();

    }
}
