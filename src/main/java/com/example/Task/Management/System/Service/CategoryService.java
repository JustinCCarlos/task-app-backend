package com.example.Task.Management.System.Service;

import com.example.Task.Management.System.DTO.CategoryDto;
import com.example.Task.Management.System.DTO.TaskDto;
import com.example.Task.Management.System.models.Category;
import com.example.Task.Management.System.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAllCategories(){
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::convertToCategoryDto)
                .collect(Collectors.toList());
    }

    public List<CategoryDto> findByName(String toSearch){
        List<Category> categories = categoryRepository.findByNameContaining(toSearch);
        return categories.stream().map(this::convertToCategoryDto).collect(Collectors.toList());
    }

    public CategoryDto createCategory(CategoryDto categoryDto){
        Category newCategory = Category.builder()
                .name(categoryDto.getName())
                .build();

        Category savedCategory = categoryRepository.save(newCategory);

        return convertToCategoryDto(savedCategory);
    }

    public CategoryDto updateCategory(Long id, CategoryDto categoryDto){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        if(category.getName() != null && !categoryDto.getName().equals(category.getName())){
            category.setName(categoryDto.getName());
        }

        Category savedCategory = categoryRepository.save(category);

        return convertToCategoryDto(savedCategory);
    }

    public void deleteCategory(Long id){
        if(!categoryRepository.existsById(id)){
            throw new EntityNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryDto convertToCategoryDto(Category category){
        return CategoryDto.builder()
                .category_id(category.getCategory_id())
                .name(category.getName())
                .build();
    }
}
