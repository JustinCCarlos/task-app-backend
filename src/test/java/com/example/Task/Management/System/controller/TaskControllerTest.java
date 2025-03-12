package com.example.Task.Management.System.controller;

import com.example.Task.Management.System.DTO.TaskDto;
import com.example.Task.Management.System.Service.TaskService;
import com.example.Task.Management.System.controllers.TaskController;
import com.example.Task.Management.System.models.Category;
import com.example.Task.Management.System.models.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task defaultTask;

    private TaskDto defaultTaskDto;

    private Category defaultCategory;

    @BeforeEach
    public void init() {
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
        when(taskService.addTask(any(TaskDto.class))).thenReturn(defaultTaskDto);

        MvcResult result = mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defaultTaskDto)))
                .andExpect(jsonPath("$.taskId").value(1L))
                .andExpect(jsonPath("$.title").value("Default Task"))
                .andExpect(status().isCreated())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

        verify(taskService).addTask(any(TaskDto.class));
    }

    @Test
    public void TaskController_CreateTask_NullStartDate_ReturnsCreated() throws Exception {
        TaskDto taskDto = defaultTaskDto.toBuilder().startDate(null).build();
        TaskDto savedTaskDto = defaultTaskDto.toBuilder().startDate(LocalDateTime.now()).build();

        when(taskService.addTask(any(TaskDto.class))).thenReturn(savedTaskDto);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startDate").exists());

        verify(taskService).addTask(any(TaskDto.class));

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

}
