package com.example.Task.Management.System.controller;

import com.example.Task.Management.System.DTO.CategoryDto;
import com.example.Task.Management.System.Service.CategoryService;
import com.example.Task.Management.System.controllers.CategoryController;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO:: search about mockrequest static import

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDto defaultCategoryDto;

    @BeforeEach
    public void init() {
        defaultCategoryDto = CategoryDto.builder()
                .categoryId(1L)
                .name("Default CategoryDto")
                .build();
    }

    @Test
    public void CategoryController_CreateCategory_ReturnCreated() throws Exception{
        when(categoryService.createCategory(any(CategoryDto.class))).thenReturn(defaultCategoryDto);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defaultCategoryDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.name").value("Default CategoryDto"))
                .andReturn();

        verify(categoryService).createCategory(any(CategoryDto.class));
    }

    @Test
    public void CategoryController_UpdateCategory_ReturnUpdated() throws Exception {
        Long categoryId = 1L;
        CategoryDto updatedCategoryDto = defaultCategoryDto.toBuilder().name("Updated CategoryDto").build();

        when(categoryService.updateCategory(eq(categoryId), any(CategoryDto.class))).thenReturn(updatedCategoryDto);

        mockMvc.perform(put("/api/categories/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategoryDto)))
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.name").value("Updated CategoryDto"))
                .andExpect(status().isOk());

        verify(categoryService).updateCategory(eq(1L), any(CategoryDto.class));
    }

}
