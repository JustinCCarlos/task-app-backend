package com.example.Task.Management.System.controller;

import com.example.Task.Management.System.dtos.Task.CreateTaskWithRecurrenceRequest;
import com.example.Task.Management.System.dtos.Task.TaskDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskDurationDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.models.recurrence.RecurrenceType;
import com.example.Task.Management.System.models.recurrence.TaskDuration;
import com.example.Task.Management.System.services.implementations.TaskServiceImpl;
import com.example.Task.Management.System.controllers.TaskController;
import com.example.Task.Management.System.models.Category;
import com.example.Task.Management.System.models.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import javax.persistence.EntityNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskServiceImpl taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<TaskDto> taskCaptor;
    @Captor
    private ArgumentCaptor<TaskRecurrenceDto> recurrenceCaptor;
    @Captor
    private ArgumentCaptor<RecurrencePatternDto> patternCaptor;

    private Task defaultTask;
    private TaskDto defaultTaskDto;
    private Category defaultCategory;
    private TaskRecurrenceDto defaultRecurrenceDto;
    private RecurrencePatternDto defaultPatternDto;
    private TaskDurationDto defaultDurationDto;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        LocalDateTime defaultStartDate = LocalDateTime.of(2026, Month.FEBRUARY, 27,0,0);

        defaultCategory = Category.builder()
                .categoryId(1L)
                .name("Home")
                .build();

        defaultTask = Task.builder()
                .taskId(1L)
                .title("Default Task")
                .completed(false)
                .category(defaultCategory)
                .priority(3)
                .startDate(defaultStartDate)
                .endDate(null)
                .finishedDate(null)
                .overdue(false)
                .build();

        defaultTaskDto = TaskDto.builder()
                .taskId(defaultTask.getTaskId())
                .title(defaultTask.getTitle())
                .completed(defaultTask.isCompleted())
                .categoryId(defaultTask.getCategory().getCategoryId())
                .priority(defaultTask.getPriority())
                .startDate(defaultTask.getStartDate())
                .endDate(defaultTask.getEndDate())
                .finishedDate(defaultTask.getFinishedDate())
                .overdue(defaultTask.isOverdue())
                .build();

        defaultRecurrenceDto = TaskRecurrenceDto.builder()
                .recurrenceStartDate(LocalDateTime.of(2026, Month.APRIL,1,0,0,0))
                .active(true)
                .build();

        defaultDurationDto = TaskDurationDto.builder()
                .hours(1)
                .minutes(30)
                .build();

        defaultPatternDto = RecurrencePatternDto.builder()
                .interval(1)
                .recurrenceType(RecurrenceType.DAILY)
                .daysOfWeek(List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
                .monthDayRule("first_monday")
                .taskDurationDto(defaultDurationDto)
                .build();
    }

    @Test
    public void TaskController_GetAllTasks_ReturnListOfTasks() throws Exception {
        List<TaskDto> taskDtos = List.of(
                defaultTaskDto,
                defaultTaskDto
        );

        when(taskService.getAllTasks()).thenReturn(taskDtos);

        mockMvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    public void TaskController_CreateTask_ReturnCreated() throws Exception {
        when(taskService.createTask(any(TaskDto.class))).thenReturn(defaultTaskDto);

        MvcResult result = mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defaultTaskDto)))
                .andExpect(jsonPath("$.taskId").value(1L))
                .andExpect(jsonPath("$.title").value("Default Task"))
                .andExpect(status().isCreated())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

        verify(taskService).createTask(any(TaskDto.class));
    }

    @Test
    public void TaskController_CreateTask_NullStartDate_ReturnsCreated() throws Exception {
        TaskDto taskDto = defaultTaskDto.toBuilder().startDate(null).build();
        TaskDto savedTaskDto = defaultTaskDto.toBuilder().startDate(LocalDateTime.now()).build();

        when(taskService.createTask(any(TaskDto.class))).thenReturn(savedTaskDto);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startDate").exists());

        verify(taskService).createTask(any(TaskDto.class));

    }

    @Test
    public void TaskController_UpdateTask_Title_ReturnsUpdatedTask() throws Exception {
        Long taskId = 1L;
        TaskDto updatedTaskDto = defaultTaskDto.toBuilder().title("Updated Task").build();

        when(taskService.updateTask(eq(taskId),any(TaskDto.class))).thenReturn(updatedTaskDto);

        mockMvc.perform(put("/api/tasks/{id}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTaskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));


    }

    @Test
    public void TaskController_DeleteTask_ReturnResponseEntityOk() throws Exception {
        Long taskId = 1L;

        doNothing().when(taskService).deleteTask(taskId);

        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isOk());

        verify(taskService).deleteTask(taskId);
    }

    @Test
    public void TaskController_GetTaskById_NotFound() throws Exception {
        Long invalidId = 99L;

        when(taskService.getTaskById(invalidId)).thenThrow(new EntityNotFoundException("Task not found with id: " + invalidId));

        mockMvc.perform(get("/api/tasks/{id}", invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    void receive_full_data_and_create_task_with_recurrence_and_return_created() throws Exception {
        CreateTaskWithRecurrenceRequest request = new CreateTaskWithRecurrenceRequest(
                defaultTaskDto, defaultRecurrenceDto, defaultPatternDto
        );

        when(taskService.createTaskWithRecurrence(any(TaskDto.class), any(TaskRecurrenceDto.class), any(RecurrencePatternDto.class)))
                .thenReturn(defaultTaskDto);

        mockMvc.perform(post("/api/tasks/recurring-tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(taskService).createTaskWithRecurrence(taskCaptor.capture(), recurrenceCaptor.capture(), patternCaptor.capture());

        Assertions.assertThat(taskCaptor.getValue().getTitle()).isEqualTo("Default Task");
        Assertions.assertThat(taskCaptor.getValue().getPriority()).isEqualTo(3);
        Assertions.assertThat(taskCaptor.getValue().getCategoryId()).isEqualTo(1L);

        Assertions.assertThat(recurrenceCaptor.getValue().recurrenceStartDate()).isEqualTo("2026-04-01T00:00");

        Assertions.assertThat(patternCaptor.getValue().interval()).isEqualTo(1);
        Assertions.assertThat(patternCaptor.getValue().monthDayRule()).isEqualTo("first_monday");

        Assertions.assertThat(patternCaptor.getValue().taskDurationDto().hours()).isEqualTo(1);
        Assertions.assertThat(patternCaptor.getValue().taskDurationDto().minutes()).isEqualTo(30);
    }

    @Test
    void invalid_data_returns_validation_error() throws Exception {
        TaskDto invalidTask = defaultTaskDto.toBuilder()
                .title("") // invalid
                .priority(7) // invalid, out of bounds
                .build();

        // Pattern missing recurrenceType
        RecurrencePatternDto invalidPattern = defaultPatternDto.toBuilder()
                .recurrenceType(null) // invalid
                .build();

        TaskRecurrenceDto recurrence = defaultRecurrenceDto.toBuilder()
                .recurrenceStartDate(null) // @NotNull violated
                .build();

        CreateTaskWithRecurrenceRequest request = new CreateTaskWithRecurrenceRequest(
                invalidTask, recurrence, invalidPattern
        );

        mockMvc.perform(post("/api/tasks/recurring-tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[*].field").isNotEmpty())
                .andExpect(jsonPath("$.errors[*].message").isNotEmpty())
                .andDo(print());
    }

}
