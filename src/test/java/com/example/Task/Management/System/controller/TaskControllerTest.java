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


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    private Task task;

    private TaskDto taskDto;

    private Category category;

    @BeforeEach
    public void init() {
        category = Category.builder()
                .categoryId(1L)
                .name("Home")
                .build();

        task = Task.builder()
                .title("test task")
                .priority(3)
                .completed(false)
                .category(null)
                .build();

        taskDto = TaskDto.builder()
                .taskId(1L)
                .title("test taskDto")
                .priority(null)
                .completed(false)
                .categoryId(1L)
                .build();
    }

    @Test
    public void TaskController_GetAllTasks_ReturnListOfTasks() throws Exception {
        List<TaskDto> taskDtos = List.of(
                taskDto,
                taskDto
        );

        when(taskService.getAllTasks()).thenReturn(taskDtos);

        mockMvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    public void TaskController_CreateTask_ReturnCreated() throws Exception {
        when(taskService.addTask(any(TaskDto.class))).thenReturn(taskDto);

        MvcResult result = mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(jsonPath("$.task_id").value(1L))
                .andExpect(jsonPath("$.title").value("test taskDto"))
                .andExpect(status().isCreated())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

        verify(taskService).addTask(any(TaskDto.class));
    }



}
