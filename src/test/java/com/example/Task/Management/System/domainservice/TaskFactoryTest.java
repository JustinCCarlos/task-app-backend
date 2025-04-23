package com.example.Task.Management.System.domainservice;

import com.example.Task.Management.System.dtos.Task.TaskDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.mappers.TaskDurationMapper;
import com.example.Task.Management.System.mappers.TaskMapper;
import com.example.Task.Management.System.models.Category;
import com.example.Task.Management.System.models.Task;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.recurrence.RecurrenceType;
import com.example.Task.Management.System.models.recurrence.TaskDuration;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import jakarta.validation.ValidationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.BDDAssertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskFactoryTest {
    @Mock
    private TaskDurationMapper durationMapper;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskRecurrenceCalculator calculator;

    @InjectMocks
    private TaskFactory taskFactory;

    @Captor
    ArgumentCaptor<TaskDto> dtoCaptor;

    private TaskRecurrence defaultRecurrence;
    private RecurrencePattern defaultPattern;
    private TaskDuration defaultDuration;
    private TaskDto defaultTaskDto;
    private Category defaultCategory;
    private Task defaultTask;

    @BeforeEach
    void setup() {
        defaultDuration = TaskDuration.builder()
                .hours(1)
                .build();

        defaultPattern = RecurrencePattern.builder()
                .interval(1)
                .recurrenceType(RecurrenceType.DAILY)
                .taskDuration(null)
                .build();

        defaultRecurrence = TaskRecurrence.builder()
                .taskRecurrenceId(1L)
                .recurrenceStartDate(LocalDateTime.of(2026, Month.APRIL, 1, 0,0,0))
                .recurrenceEndDate(null)
                .maxOccurrences(null)
                .recurrencePattern(defaultPattern)
                .build();

        defaultTaskDto = TaskDto.builder()
                .title("Task Dto")
                .priority(3)
                .startDate(LocalDateTime.of(2026, Month.APRIL, 1, 0,0,0))
                .build();

        defaultCategory = Category.builder()
                .categoryId(1L)
                .name("Default Category")
                .build();

        defaultTask = Task.builder()
                .title("Task")
                .startDate(LocalDateTime.of(2026, Month.APRIL, 1, 0,0,0))
                .category(null)
                .build();
    }

    @Test
    void task_without_category_created_is_valid_and_saved_properly(){

        when(taskMapper.toEntity(any(TaskDto.class), isNull(), isNull())).thenReturn(defaultTask);

        Task result = taskFactory.createTask(defaultTaskDto, null);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getCategory()).isNull();

        verify(taskMapper).toEntity(defaultTaskDto, null, null);
    }

    @Test
    void task_with_recurrence_is_created_and_validated_properly(){
        LocalDateTime expectedEndDate = LocalDateTime.of(2026, Month.APRIL,2,0,0);

        when(calculator.calculateNextEndDate(any(LocalDateTime.class), any(RecurrencePattern.class)))
                .thenReturn(expectedEndDate);
        when(taskMapper.toEntity(any(TaskDto.class), any(Category.class), any(TaskRecurrence.class)))
                .thenAnswer(inv -> {
                    TaskDto taskDto = inv.getArgument(0);
                    return defaultTask.toBuilder().endDate(taskDto.getEndDate()).build();
        });

        Task task = taskFactory.createTask(defaultTaskDto, defaultCategory, defaultRecurrence);

        verify(taskMapper).toEntity(dtoCaptor.capture(), any(Category.class), any(TaskRecurrence.class));
        TaskDto captured = dtoCaptor.getValue();

        Assertions.assertThat(task).isNotNull();
        Assertions.assertThat(task.getEndDate()).isEqualTo(LocalDateTime.of(2026, Month.APRIL,2,0,0));
        Assertions.assertThat(captured.getEndDate()).isEqualTo(LocalDateTime.of(2026, Month.APRIL,2,0,0));
    }

    @Test
    void task_with_null_startDate_will_be_given_current_date(){
        TaskDto taskDto = defaultTaskDto.toBuilder()
                .startDate(null)
                .build();

        when(taskMapper.toEntity(any(TaskDto.class), isNull(), isNull())).thenReturn(defaultTask.toBuilder().startDate(LocalDateTime.now()).build());

        taskFactory.createTask(taskDto, null);

        verify(taskMapper).toEntity(dtoCaptor.capture(), isNull(),isNull());
        TaskDto captured = dtoCaptor.getValue();

        Assertions.assertThat(captured.getStartDate()).isNotNull();
        Assertions.assertThat(captured.getStartDate()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    public void created_task_with_null_priority_defaults_to_3(){
        TaskDto taskDto = defaultTaskDto.toBuilder()
                .priority(null)
                .build();

        when(taskMapper.toEntity(any(TaskDto.class), isNull(), isNull())).thenReturn(defaultTask);

        taskFactory.createTask(taskDto, null);

        verify(taskMapper).toEntity(dtoCaptor.capture(), isNull(), isNull());
        TaskDto captured = dtoCaptor.getValue();

        Assertions.assertThat(captured.getPriority()).isEqualTo(3);
    }

    @Test
    void priority_out_of_bounds_should_throw_validation_exception() {
        Task invalidPriority = defaultTask.toBuilder().priority(10).build();

        Assertions.assertThatThrownBy(() -> taskFactory.validateUpdate(invalidPriority))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Priority must be between 1 and 5");
    }

    @Test
    void startDate_after_endDate_should_throw_validation_exception() {
        Task invalidDate = defaultTask.toBuilder()
                .startDate(LocalDateTime.of(2026, 4, 10, 0, 0))
                .endDate(LocalDateTime.of(2026, 4, 1, 0, 0))
                .build();

        Assertions.assertThatThrownBy(() -> taskFactory.validateUpdate(invalidDate))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Start date must be before end date");
    }
}
