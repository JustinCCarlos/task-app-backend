package com.example.Task.Management.System.service;

import com.example.Task.Management.System.DTO.CategoryDto;
import com.example.Task.Management.System.Service.CategoryService;
import com.example.Task.Management.System.models.Category;
import com.example.Task.Management.System.repository.CategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void CategoryService_UpdateCategory_Name_CategoryDto(){
        Category category = Category.builder()
                .name("old category")
                .build();

        CategoryDto newCategoryDto = CategoryDto.builder()
                .name("new category")
                .build();

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto updatedCategory = categoryService.updateCategory(1L, newCategoryDto);

        verify(categoryRepository).save(any(Category.class));
        Assertions.assertThat(updatedCategory.getName()).isEqualTo("new category");
    }
}
