package com.example.Task.Management.System.services;

import com.example.Task.Management.System.domainservice.TaskRecurrenceCalculator;
import com.example.Task.Management.System.dtos.Task.TaskDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskDurationDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.mappers.RecurrencePatternMapper;
import com.example.Task.Management.System.mappers.TaskMapper;
import com.example.Task.Management.System.mappers.TaskRecurrenceMapper;
import com.example.Task.Management.System.models.Task;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.recurrence.RecurrenceType;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import com.example.Task.Management.System.services.implementations.TaskRecurrenceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskRecurrenceTest {
    @Mock
    private TaskService taskService;

    @Mock
    private TaskRecurrenceCalculator calculator;

    @Spy
    @InjectMocks
    private TaskRecurrenceImpl taskRecurrenceImpl;

    @Captor
    private ArgumentCaptor<TaskDto> taskDtoCaptor;

    private TaskRecurrenceDto recurrenceDto;
    private RecurrencePatternDto patternDto;
    private TaskRecurrence taskRecurrence;
    private RecurrencePattern recurrencePattern;
    private Task lastTask;
    private TaskDto expectedTaskDto;

    @BeforeEach
    void setUp() {
        taskDtoCaptor = ArgumentCaptor.forClass(TaskDto.class);

        recurrenceDto = new TaskRecurrenceDto(1L,
                LocalDateTime.of(2025, 4, 8, 10, 0),
                null,
                5,
                true,
                1L);

        patternDto = new RecurrencePatternDto(
                1L,
                1,
                RecurrenceType.DAILY,
                new TaskDurationDto(0, 0, 0, 0, 0, 0),
                List.of(DayOfWeek.MONDAY),
                null);

        lastTask = Task.builder()
                .taskId(101L)
                .title("Mock Task")
                .completed(true)
                .priority(3)
                .startDate(LocalDateTime.of(2025, 4, 8, 10, 0))
                .endDate(LocalDateTime.of(2025, 4, 8, 11, 0))
                .overdue(false)
                .build();

        taskRecurrence = TaskRecurrence.builder()
                .generatedTasks(new ArrayList<>(List.of(lastTask)))
                .recurrenceStartDate(LocalDateTime.of(2025, 4, 8, 10, 0))
                .build();

        recurrencePattern = RecurrencePattern.builder()
                .recurrencePatternId(1L)
                .interval(1)
                .recurrenceType(RecurrenceType.DAILY)
                .build();

        lastTask = lastTask.toBuilder()
                .taskRecurrence(taskRecurrence)
                .build();

        expectedTaskDto = TaskDto.builder()
                .taskId(101L)
                .title("Mock Task")
                .completed(false)
                .startDate(LocalDateTime.of(2025, 4, 9, 10, 0))
                .endDate(LocalDateTime.of(2025, 4, 9, 11, 0))
                .overdue(false)
                .build();

    }

    @Test
    void testGenerateNextTask_createsExpectedTask() {
        try (MockedStatic<TaskRecurrenceMapper> mockRecurrenceMapper = mockStatic(TaskRecurrenceMapper.class);
             MockedStatic<RecurrencePatternMapper> mockPatternMapper = mockStatic(RecurrencePatternMapper.class);
             MockedStatic<TaskMapper> mockTaskMapper = mockStatic(TaskMapper.class)) {

            mockRecurrenceMapper.when(() -> TaskRecurrenceMapper.toEntity(recurrenceDto))
                    .thenReturn(taskRecurrence);

            mockPatternMapper.when(() -> RecurrencePatternMapper.toEntity(patternDto))
                    .thenReturn(recurrencePattern);

            mockTaskMapper.when(() -> TaskMapper.toDto(any(Task.class))).thenReturn(expectedTaskDto);

            when (calculator.calculateNextStartDate(any(), any(), any()))
                    .thenReturn(LocalDateTime.of(2025, 4, 9, 10, 0));
            when (calculator.calculateNextEndDate(any(), any(), any(), any()))
                    .thenReturn(LocalDateTime.of(2025, 4, 9, 11, 0));

            when (taskService.addTask(any()))
                    .thenReturn(expectedTaskDto);

            taskRecurrenceImpl.generateNextTask(recurrenceDto, patternDto);

//            verify(taskService, times(1)).addTask(argThat(task ->
//                    task.getStartDate().equals(expectedTaskDto.getStartDate()) &&
//                    task.getEndDate().equals(expectedTaskDto.getEndDate()) &&
//                    !task.isCompleted()
//            ));

            verify(taskService).addTask(taskDtoCaptor.capture());

            TaskDto capturedTaskDto = taskDtoCaptor.getValue();
            Assertions.assertThat(capturedTaskDto.getStartDate()).isEqualTo(expectedTaskDto.getStartDate());
            Assertions.assertThat(capturedTaskDto.getEndDate()).isEqualTo(expectedTaskDto.getEndDate());
            Assertions.assertThat(capturedTaskDto.isCompleted()).isFalse();
        }
    }
}
