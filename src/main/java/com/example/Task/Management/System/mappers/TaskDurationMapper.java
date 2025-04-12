package com.example.Task.Management.System.mappers;

import com.example.Task.Management.System.dtos.TaskRecurrence.TaskDurationDto;
import com.example.Task.Management.System.models.recurrence.TaskDuration;

public class TaskDurationMapper {

    public static TaskDurationDto toDto(TaskDuration taskDuration) {
        return new TaskDurationDto(
                taskDuration.getMinutes(),
                taskDuration.getHours(),
                taskDuration.getDays(),
                taskDuration.getWeeks(),
                taskDuration.getMonths(),
                taskDuration.getYears()
        );
    }

    public static TaskDuration toEntity(TaskDurationDto taskDurationDto) {
        return TaskDuration.builder()
                .minutes(taskDurationDto.minutes())
                .hours(taskDurationDto.hours())
                .days(taskDurationDto.days())
                .weeks(taskDurationDto.weeks())
                .months(taskDurationDto.months())
                .years(taskDurationDto.years())
                .build();
    }
}
