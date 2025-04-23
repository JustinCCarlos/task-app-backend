package com.example.Task.Management.System.mappers;

import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskDurationDto;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.recurrence.RecurrenceType;
import com.example.Task.Management.System.models.recurrence.TaskDuration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecurrencePatternMapperTest {
    @Mock
    private TaskDurationMapper durationMapper;

    private RecurrencePatternMapper patternMapper;

    private RecurrencePattern defaultPattern;
    private RecurrencePatternDto defaultPatternDto;
    private TaskDuration defaultDuration;
    private TaskDurationDto defaultDurationDto;


    @BeforeEach
    void init() {
        patternMapper = new RecurrencePatternMapper(durationMapper);

        defaultDuration = TaskDuration.builder()
                .hours(1)
                .minutes(30)
                .build();

        defaultDurationDto = TaskDurationDto.builder()
                .hours(1)
                .minutes(30)
                .build();

        defaultPattern = RecurrencePattern.builder()
                .recurrencePatternId(1L)
                .interval(1)
                .recurrenceType(RecurrenceType.DAILY)
                .taskDuration(defaultDuration)
                .daysOfWeek(List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
                .monthDayRule("first_monday")
                .build();

        defaultPatternDto = RecurrencePatternDto.builder()
                .recurrencePatternId(1L)
                .interval(1)
                .recurrenceType(RecurrenceType.DAILY)
                .taskDurationDto(defaultDurationDto)
                .daysOfWeek(List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
                .monthDayRule("first_monday")
                .build();
    }

    @Test
    void should_map_entity_to_dto_correctly() {

        RecurrencePatternDto result = patternMapper.toDto(defaultPattern, defaultDurationDto);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.recurrencePatternId()).isEqualTo(defaultPattern.getRecurrencePatternId());
        Assertions.assertThat(result.interval()).isEqualTo(defaultPattern.getInterval());
        Assertions.assertThat(result.recurrenceType()).isEqualTo(defaultPattern.getRecurrenceType());
        Assertions.assertThat(result.taskDurationDto()).isEqualTo(defaultDurationDto);
        Assertions.assertThat(result.daysOfWeek()).containsExactlyElementsOf(defaultPattern.getDaysOfWeek());
        Assertions.assertThat(result.monthDayRule()).isEqualTo(defaultPattern.getMonthDayRule());
    }

    @Test
    void should_map_dto_to_entity_correctly() {

        RecurrencePattern result = patternMapper.toEntity(defaultPatternDto, defaultDuration);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getRecurrencePatternId()).isEqualTo(defaultPatternDto.recurrencePatternId());
        Assertions.assertThat(result.getInterval()).isEqualTo(defaultPatternDto.interval());
        Assertions.assertThat(result.getRecurrenceType()).isEqualTo(defaultPatternDto.recurrenceType());
        Assertions.assertThat(result.getTaskDuration()).isEqualTo(defaultDuration);
        Assertions.assertThat(result.getDaysOfWeek()).containsExactlyElementsOf(defaultPatternDto.daysOfWeek());
        Assertions.assertThat(result.getMonthDayRule()).isEqualTo(defaultPatternDto.monthDayRule());
    }
}
