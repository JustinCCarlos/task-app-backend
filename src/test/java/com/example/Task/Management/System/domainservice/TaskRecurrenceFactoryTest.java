package com.example.Task.Management.System.domainservice;

import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.mappers.TaskDurationMapper;
import com.example.Task.Management.System.mappers.TaskRecurrenceMapper;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.recurrence.RecurrenceType;
import com.example.Task.Management.System.models.recurrence.TaskDuration;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskRecurrenceFactoryTest {
    @Mock
    private TaskDurationMapper durationMapper;
    @Mock
    private RecurrencePatternFactory patternFactory;
    @Mock
    private TaskRecurrenceMapper recurrenceMapper;

    @InjectMocks
    private TaskRecurrenceFactory recurrenceFactory;

    private TaskRecurrenceDto defaultRecurrenceDto;
    private RecurrencePatternDto defaultPatternDto;
    private RecurrencePattern defaultPattern;
    private TaskRecurrence defaultRecurrence;

    @BeforeEach
    void setup() {
        defaultRecurrenceDto = TaskRecurrenceDto.builder()
                .recurrenceStartDate(LocalDateTime.of(2026, Month.APRIL, 1, 0,0,0))
                .recurrenceEndDate(null)
                .maxOccurrences(null)
                .recurrencePatternId(null)
                .build();

        defaultPatternDto = RecurrencePatternDto.builder()
                .interval(1)
                .recurrenceType(RecurrenceType.DAILY)
                .taskDurationDto(null)
                .build();

        defaultPattern = RecurrencePattern.builder()
                .interval(1)
                .recurrenceType(RecurrenceType.DAILY)
                .taskDuration(null)
                .build();

        defaultRecurrence = TaskRecurrence.builder()
                .recurrenceStartDate(LocalDateTime.of(2026, Month.APRIL, 1, 0,0,0))
                .recurrenceEndDate(null)
                .maxOccurrences(null)
                .recurrencePattern(defaultPattern)
                .build();

    }

    @Test
    void TaskRecurrence_is_created_and_validated_successfully(){
        when(patternFactory.createPattern(defaultPatternDto)).thenReturn(defaultPattern);
        when(recurrenceMapper.toEntity(defaultRecurrenceDto, defaultPattern)).thenReturn(defaultRecurrence);

        TaskRecurrence result = recurrenceFactory.createRecurrence(defaultRecurrenceDto, defaultPatternDto);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTaskRecurrenceId()).isNull();
        Assertions.assertThat(result.getRecurrenceStartDate()).isNotNull();
        Assertions.assertThat(result.getRecurrenceStartDate()).isEqualTo(LocalDateTime.of(2026, Month.APRIL, 1, 0, 0, 0));
        Assertions.assertThat(result.getActive()).isTrue();
    }
}
