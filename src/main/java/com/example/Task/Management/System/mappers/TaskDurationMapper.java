package com.example.Task.Management.System.mappers;

import com.example.Task.Management.System.dtos.TaskRecurrence.TaskDurationDto;
import com.example.Task.Management.System.models.recurrence.TaskDuration;
import org.springframework.stereotype.Component;

@Component
public class TaskDurationMapper {

    public TaskDurationDto toDto(TaskDuration taskDuration) {
        if (taskDuration == null || taskDuration.isEmpty()) return null;
        return TaskDurationDto.builder()
                .minutes(taskDuration.getMinutes())
                .hours(taskDuration.getHours())
                .days(taskDuration.getDays())
                .weeks(taskDuration.getWeeks())
                .months(taskDuration.getMonths())
                .years(taskDuration.getYears())
                .build();

    }

    public TaskDuration toEntity(TaskDurationDto taskDurationDto) {
        if (taskDurationDto == null || taskDurationDto.isEmpty()) return null;

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
