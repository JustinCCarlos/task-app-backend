//package com.example.Task.Management.System;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import static org.mockito.Mockito.when;
//
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(TaskController.class)
//public class TaskControllerTest {
//    @Mock
//    private TaskService taskService; // Mocking TaskService
//
//    @InjectMocks
//    private TaskController taskController; // Injecting mocked service into TaskController
//
//    @Autowired
//    private MockMvc mockMvc; // MockMvc for sending requests and receiving responses
//
//    @Test
//    public void testGetTaskStatus() throws Exception {
//        when(taskService.getTaskStatus()).thenReturn("All tasks are running smoothly!");
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/status"))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string("All tasks are running smoothly!"));
//    }
//}
