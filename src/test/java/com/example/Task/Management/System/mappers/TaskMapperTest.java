package com.example.Task.Management.System.mappers;

import com.example.Task.Management.System.dtos.Task.TaskDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.models.Category;
import com.example.Task.Management.System.models.Task;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class TaskMapperTest {
    private TaskMapper taskMapper;
    private Task defaultTask;
    private TaskDto defaultTaskDto;
    private Category defaultCategory;
    private TaskRecurrence defaultRecurrence;

    @BeforeEach
    void setUp(){
        taskMapper = new TaskMapper();

        defaultTask = Task.builder()
                .title("Default Task")
                .priority(3)
                .category(null)
                .startDate(LocalDateTime.of(2026, Month.APRIL, 1, 0,0,0))
                .build();

        defaultTaskDto = TaskDto.builder()
                .title("Default Dto")
                .priority(3)
                .startDate(LocalDateTime.of(2026, Month.APRIL, 1, 0,0,0))
                .build();

        defaultCategory = Category.builder()
                .categoryId(1L)
                .name("Default Category")
                .build();

        defaultRecurrence = TaskRecurrence.builder()
                .recurrenceStartDate(LocalDateTime.of(2026, Month.APRIL, 1, 0,0,0))
                .recurrenceEndDate(null)
                .maxOccurrences(null)
                .recurrencePattern(null)
                .build();
    }

    @Test
    void TaskDto_maps_all_fields_from_dto_to_entity(){
        TaskDto taskDto = defaultTaskDto.toBuilder()
                .taskId(1L)
                .endDate(LocalDateTime.of(2026, Month.APRIL, 2, 0,0,0))
                .completed(false)
                .overdue(false)
                .categoryId(2L)
                .taskRecurrenceId(3L)
                .build();

        Task result = taskMapper.toEntity(taskDto, defaultCategory, defaultRecurrence);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTaskId()).isEqualTo(1L);
        Assertions.assertThat(result.getTitle()).isEqualTo("Default Dto");
        Assertions.assertThat(result.getPriority()).isEqualTo(3);
        Assertions.assertThat(result.getStartDate()).isEqualTo(LocalDateTime.of(2026, Month.APRIL, 1, 0,0,0));
        Assertions.assertThat(result.getEndDate()).isEqualTo(LocalDateTime.of(2026, Month.APRIL, 2, 0,0,0));
        Assertions.assertThat(result.isCompleted()).isFalse();
        Assertions.assertThat(result.isOverdue()).isFalse();
        Assertions.assertThat(result.getCategory()).isEqualTo(defaultCategory);
        Assertions.assertThat(result.getTaskRecurrence()).isEqualTo(defaultRecurrence);

    }

    @Test
    void TaskDto_with_null_Category_and_TaskRecurrence_should_be_mapped_correctly(){

        Task result = taskMapper.toEntity(defaultTaskDto, null, null);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getCategory()).isNull();
        Assertions.assertThat(result.getTaskRecurrence()).isNull();

    }

    @Test
    void mapper_creates_new_instance_without_affecting_dto(){
        Task result = taskMapper.toEntity(defaultTaskDto, null, null);
        defaultTaskDto.setTitle("New Title");

        Assertions.assertThat(result.getTitle()).isEqualTo("Default Dto");
    }

    @Test
    void should_update_existing_task_from_dto_and_category(){
        TaskDto newTaskDto = defaultTaskDto.toBuilder()
                .title("new title")
                .priority(4)
                .categoryId(1L)
                .build();

        taskMapper.updateEntity(defaultTask, newTaskDto, defaultCategory);

        Assertions.assertThat(defaultTask.getTitle()).isEqualTo("new title");
        Assertions.assertThat(defaultTask.getPriority()).isEqualTo(4);
        Assertions.assertThat(defaultTask.getStartDate()).isEqualTo(newTaskDto.getStartDate());
        Assertions.assertThat(defaultTask.getEndDate()).isEqualTo(newTaskDto.getEndDate());
        Assertions.assertThat(defaultTask.isCompleted()).isEqualTo(newTaskDto.isCompleted());
        Assertions.assertThat(defaultTask.getCategory()).isEqualTo(defaultCategory);
    }

}
