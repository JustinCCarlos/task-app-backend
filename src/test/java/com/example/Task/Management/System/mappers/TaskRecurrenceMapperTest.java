package com.example.Task.Management.System.mappers;

import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.models.Task;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.recurrence.RecurrenceType;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class TaskRecurrenceMapperTest {
    private TaskRecurrenceMapper  recurrenceMapper;

    private TaskRecurrence defaultRecurrence;
    private TaskRecurrenceDto defaultRecurrenceDto;
    private RecurrencePattern  defaultPattern;
    private Task defaultTask;

    @BeforeEach
    void init() {
        recurrenceMapper = new TaskRecurrenceMapper();

        defaultPattern = RecurrencePattern.builder()
                .recurrencePatternId(1L)
                .interval(1)
                .recurrenceType(RecurrenceType.DAILY)
                .build();

        defaultTask = Task.builder()
                .taskId(1L)
                .title("Default Task")
                .completed(false)
                .category(null)
                .priority(3)
                .startDate(LocalDateTime.of(2026, Month.FEBRUARY, 27,0,0))
                .endDate(null)
                .finishedDate(null)
                .overdue(false)
                .taskRecurrence(null)
                .build();

        defaultRecurrence = TaskRecurrence.builder()
                .generatedTasks(new ArrayList<>(List.of(defaultTask)))
                .recurrenceStartDate(LocalDateTime.of(2026, Month.FEBRUARY, 27,0,0))
                .recurrencePattern(defaultPattern)
                .build();

        defaultRecurrenceDto = TaskRecurrenceDto.builder()
                .recurrenceStartDate(LocalDateTime.of(2026, Month.FEBRUARY, 27,0,0))
                .recurrencePatternId(1L)
                .build();
    }

    @Test
    void all_TaskRecurrenceDto_field_is_mapped_from_dto_to_entity(){
        TaskRecurrenceDto recurrenceDto = defaultRecurrenceDto.toBuilder()
                .taskRecurrenceId(1L)
                .recurrenceEndDate(LocalDateTime.of(2026, Month.MARCH, 27,0,0))
                .active(true)
                .recurrencePatternId(1L)
                .build();

        TaskRecurrence result = recurrenceMapper.toEntity(recurrenceDto, defaultPattern);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTaskRecurrenceId()).isEqualTo(1L);
        Assertions.assertThat(result.getRecurrenceStartDate()).isEqualTo(LocalDateTime.of(2026, Month.FEBRUARY, 27,0,0));
        Assertions.assertThat(result.getRecurrenceEndDate()).isEqualTo(LocalDateTime.of(2026, Month.MARCH, 27,0,0));
        Assertions.assertThat(result.getMaxOccurrences()).isNull();
        Assertions.assertThat(result.getActive()).isTrue();
        Assertions.assertThat(result.getRecurrencePattern()).isEqualTo(defaultPattern);
    }

}
