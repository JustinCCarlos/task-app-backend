package com.example.Task.Management.System.domainservice;

import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskDurationDto;
import com.example.Task.Management.System.exceptions.DomainValidationException;
import com.example.Task.Management.System.mappers.RecurrencePatternMapper;
import com.example.Task.Management.System.mappers.TaskDurationMapper;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.recurrence.RecurrenceType;
import com.example.Task.Management.System.models.recurrence.TaskDuration;
import jakarta.validation.ValidationException;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecurrencePatternFactoryTest {
    @Mock
    private RecurrencePatternMapper patternMapper;
    @Mock
    private TaskDurationMapper durationMapper;

    @InjectMocks
    private RecurrencePatternFactory factory;

    private TaskDurationDto defaultDurationDto;
    private TaskDuration defaultDuration;
    private RecurrencePatternDto defaultPatternDto;
    private RecurrencePattern defaultPattern;

    @BeforeEach
    void setup() {
        defaultDurationDto = TaskDurationDto.builder().hours(1).minutes(30).build();
        defaultDuration = TaskDuration.builder().hours(1).minutes(30).build();

        defaultPatternDto = RecurrencePatternDto.builder()
                .interval(1)
                .recurrenceType(RecurrenceType.DAILY)
                .taskDurationDto(defaultDurationDto)
                .monthDayRule("first_monday")
                .daysOfWeek(List.of())
                .build();

        defaultPattern = RecurrencePattern.builder()
                .interval(1)
                .recurrenceType(RecurrenceType.DAILY)
                .taskDuration(defaultDuration)
                .monthDayRule("first_monday")
                .daysOfWeek(List.of())
                .build();
    }

    @Test
    void should_create_and_validate_pattern_successfully() {
        when(durationMapper.toEntity(defaultDurationDto)).thenReturn(defaultDuration);
        when(patternMapper.toEntity(defaultPatternDto, defaultDuration)).thenReturn(defaultPattern);

        RecurrencePattern result = factory.createPattern(defaultPatternDto);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getInterval()).isEqualTo(1);
        Assertions.assertThat(result.getRecurrenceType()).isEqualTo(RecurrenceType.DAILY);
        Assertions.assertThat(result.getTaskDuration()).isEqualTo(defaultDuration);
    }

    @Test
    void should_throw_when_monthDayRule_is_invalid_format() {
        RecurrencePatternDto invalidDto = defaultPatternDto.toBuilder()
                .monthDayRule("invalid_format")
                .build();
        RecurrencePattern invalidPattern = defaultPattern.toBuilder()
                .monthDayRule("invalid_format")
                .build();

        when(durationMapper.toEntity(defaultDurationDto)).thenReturn(defaultDuration);
        when(patternMapper.toEntity(invalidDto, defaultDuration)).thenReturn(invalidPattern);

        Throwable thrown = catchThrowable(() -> factory.createPattern(invalidDto));

        Assertions.assertThat(thrown).isInstanceOf(DomainValidationException.class);

        DomainValidationException exception = (DomainValidationException) thrown;
        Assertions.assertThat(exception.getErrors()).containsExactly(
                "Invalid nth value: 'invalid'",
                "Invalid day value: 'format'"
        );
    }

    @Test
    void should_throw_when_interval_is_out_of_bounds() {
        RecurrencePatternDto dto = defaultPatternDto.toBuilder().interval(100).build();
        RecurrencePattern pattern = defaultPattern.toBuilder().interval(100).build();

        when(durationMapper.toEntity(defaultDurationDto)).thenReturn(defaultDuration);
        when(patternMapper.toEntity(dto, defaultDuration)).thenReturn(pattern);

        Assertions.assertThatThrownBy(() -> factory.createPattern(dto))
                .isInstanceOf(RuntimeException.class)
                .hasRootCauseInstanceOf(ValidationException.class)
                .hasMessageContaining("Interval must be between 1 and 99");
    }

    @Test
    void should_throw_when_recurrenceType_is_null() {
        RecurrencePatternDto dto = defaultPatternDto.toBuilder().recurrenceType(null).build();
        RecurrencePattern pattern = defaultPattern.toBuilder().recurrenceType(null).build();

        when(durationMapper.toEntity(defaultDurationDto)).thenReturn(defaultDuration);
        when(patternMapper.toEntity(dto, defaultDuration)).thenReturn(pattern);

        Assertions.assertThatThrownBy(() -> factory.createPattern(dto))
                .isInstanceOf(RuntimeException.class)
                .hasRootCauseInstanceOf(ValidationException.class)
                .hasMessageContaining("Recurrence Type is required");
    }
}
