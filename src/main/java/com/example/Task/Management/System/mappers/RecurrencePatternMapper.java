package com.example.Task.Management.System.mappers;

import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;

public class RecurrencePatternMapper {
    public static RecurrencePatternDto toDto(RecurrencePattern entity){
        return RecurrencePatternDto.builder()
                .recurrencePatternId(entity.getRecurrencePatternId())
                .interval(entity.getInterval())
                .recurrenceType(entity.getRecurrenceType())
                .taskDurationDto(TaskDurationMapper.toDto(entity.getTaskDuration()))
                .daysOfWeek(entity.getDaysOfWeek())
                .monthDayRule(entity.getMonthDayRule())
                .build();
    }

    public static RecurrencePattern toEntity(RecurrencePatternDto dto){
        return RecurrencePattern.builder()
                .recurrencePatternId(dto.recurrencePatternId())
                .interval(dto.interval())
                .taskDuration(TaskDurationMapper.toEntity(dto.taskDurationDto()))
                .daysOfWeek(dto.daysOfWeek())
                .monthDayRule(dto.monthDayRule())
                .build();

    }
}
