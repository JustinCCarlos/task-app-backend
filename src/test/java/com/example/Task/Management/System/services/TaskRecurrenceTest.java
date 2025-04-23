package com.example.Task.Management.System.services;

import com.example.Task.Management.System.domainservice.TaskFactory;
import com.example.Task.Management.System.domainservice.TaskRecurrenceFactory;
import com.example.Task.Management.System.dtos.Task.TaskDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.mappers.TaskRecurrenceMapper;
import com.example.Task.Management.System.models.Task;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.recurrence.RecurrenceType;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import com.example.Task.Management.System.repository.RecurrencePatternRepository;
import com.example.Task.Management.System.repository.TaskRecurrenceRepository;
import com.example.Task.Management.System.repository.TaskRepository;
import com.example.Task.Management.System.services.implementations.TaskRecurrenceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskRecurrenceTest {
    @Mock
    private TaskRecurrenceRepository recurrenceRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private RecurrencePatternRepository patternRepository;
    @InjectMocks
    private TaskRecurrenceImpl TaskRecurrenceService;
    @Spy
    private TaskRecurrenceMapper recurrenceMapper;
    @Mock
    private TaskFactory taskFactory;
    @Mock
    private TaskRecurrenceFactory recurrenceFactory;
    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    private TaskRecurrenceDto defaultRecurrenceDto;
    private RecurrencePatternDto defaultPatternDto;
    private TaskRecurrence defaultRecurrence;
    private RecurrencePattern defaultPattern;
    private Task defaultCurrentTask;
    private Task defaultNextTask;
    private TaskDto defaultNextTaskDto;

    @BeforeEach
    void setUp() {
        taskCaptor = ArgumentCaptor.forClass(Task.class);

        defaultRecurrenceDto = TaskRecurrenceDto.builder()
                .taskRecurrenceId(1L)
                .recurrenceStartDate(LocalDateTime.of(2025, 4, 8, 10, 0))
                .recurrenceEndDate(null)
                .maxOccurrences(5)
                .active(true)
                .recurrencePatternId(1L)
                .build();

        defaultPatternDto = RecurrencePatternDto.builder()
                .recurrencePatternId(1L)
                .interval(1)
                .recurrenceType(RecurrenceType.DAILY)
                .taskDurationDto(null)
                .daysOfWeek(null)
                .monthDayRule(null)
                .build();;

        defaultCurrentTask = Task.builder()
                .taskId(101L)
                .title("Mock Task")
                .completed(true)
                .priority(3)
                .startDate(LocalDateTime.of(2025, 4, 8, 10, 0))
                .endDate(LocalDateTime.of(2025, 4, 8, 11, 0))
                .overdue(false)
                .build();

        defaultPattern = RecurrencePattern.builder()
                .recurrencePatternId(1L)
                .interval(1)
                .recurrenceType(RecurrenceType.DAILY)
                .build();

        defaultRecurrence = TaskRecurrence.builder()
                .generatedTasks(new ArrayList<>(List.of(defaultCurrentTask)))
                .recurrenceStartDate(LocalDateTime.of(2025, 4, 8, 10, 0))
                .recurrencePattern(defaultPattern)
                .build();

        defaultCurrentTask = defaultCurrentTask.toBuilder()
                .taskRecurrence(defaultRecurrence)
                .build();

        defaultNextTask = Task.builder()
                .taskId(101L)
                .title("Mock Task")
                .completed(false)
                .startDate(LocalDateTime.of(2025, 4, 9, 10, 0))
                .endDate(LocalDateTime.of(2025, 4, 9, 11, 0))
                .overdue(false)
                .build();

        defaultNextTaskDto = TaskDto.builder()
                .taskId(101L)
                .title("Mock Task")
                .completed(false)
                .startDate(LocalDateTime.of(2025, 4, 9, 10, 0))
                .endDate(LocalDateTime.of(2025, 4, 9, 11, 0))
                .overdue(false)
                .build();

    }

    @Test
    void recurrence_and_pattern_created_are_valid_and_saved_properly(){
        TaskRecurrenceDto receivedRecurrenceDto = defaultRecurrenceDto.toBuilder()
                .recurrencePatternId(null)
                .build();

        RecurrencePatternDto receivedPatternDto = defaultPatternDto.toBuilder()
                .recurrencePatternId(null)
                .build();

        TaskRecurrence expectedRecurrence = defaultRecurrence;
        RecurrencePattern expectedPattern = defaultPattern;

        when(recurrenceFactory.createRecurrence(any(TaskRecurrenceDto.class), any(RecurrencePatternDto.class)))
                .thenReturn(expectedRecurrence);

        TaskRecurrenceDto result = TaskRecurrenceService.createRecurrenceForExistingTask(receivedRecurrenceDto, receivedPatternDto);

        verify(patternRepository).save(expectedPattern);
        verify(recurrenceRepository).save(expectedRecurrence);

        Assertions.assertThat(defaultRecurrenceDto).isEqualTo(result);



    }

    @Test
    void next_task_is_generated_successfully() {
        when(patternRepository.findById(any())).thenReturn(Optional.of(defaultPattern));
        doReturn(defaultRecurrence).when(recurrenceMapper).toEntity(defaultRecurrenceDto, defaultPattern);
        when(taskFactory.createNextTask(any(Task.class), any(TaskRecurrence.class))).thenReturn(defaultNextTask);

        TaskRecurrenceService.generateNextTask(defaultRecurrenceDto);

        verify(taskRepository).save(defaultNextTask);
        Assertions.assertThat(defaultRecurrence.getGeneratedTasks()).contains(defaultNextTask);
    }
}
