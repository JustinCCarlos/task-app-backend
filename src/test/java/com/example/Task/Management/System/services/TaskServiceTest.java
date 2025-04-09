package com.example.Task.Management.System.services;

import com.example.Task.Management.System.dtos.Task.TaskDto;
import com.example.Task.Management.System.services.implementations.TaskServiceImpl;
import com.example.Task.Management.System.models.Category;
import com.example.Task.Management.System.models.Task;
import com.example.Task.Management.System.repository.CategoryRepository;
import com.example.Task.Management.System.repository.TaskRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.within;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


// TODO:: implement use of ArgumentCaptor

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    @Captor
    private ArgumentCaptor<TaskDto> taskDtoCaptor;

    private Category defaultCategory;
    private Task defaultTask;
    private TaskDto defaultTaskDto;

    @BeforeEach
    public void init() {
//        taskService = new TaskServiceImpl(taskRepository, categoryRepository);
        taskCaptor = ArgumentCaptor.forClass(Task.class);
        taskDtoCaptor = ArgumentCaptor.forClass(TaskDto.class);

        LocalDateTime defaultStartDate = LocalDateTime.of(2026, Month.FEBRUARY, 27,0,0);
        LocalDateTime defaultEndDate = LocalDateTime.of(2026, Month.MARCH, 30,0,0);
        LocalDateTime defaultFinished = LocalDateTime.of(2026, Month.MARCH, 15,0,0);

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
                .taskRecurrence(null)
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
                .taskRecurrenceId(defaultTask.getTaskRecurrence() != null ? defaultTask.getTaskRecurrence().getTaskRecurrenceId() : null)
                .build();
    }

    @Test
    public void TaskService_GetAllTask_ReturnAllTaskDto(){
        Task task1 = defaultTask.toBuilder()
                .title("task1").build();
        Task task2 = defaultTask.toBuilder()
                .title("task2").build();
        Task task3 = defaultTask.toBuilder()
                .title("task3").build();

        List<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);

        when(taskRepository.findAll()).thenReturn(tasks);

        List<TaskDto> taskDtos = taskService.getAllTasks();

        verify(taskRepository).findAll();
        Assertions.assertThat(taskDtos)
                .isNotNull()
                .hasSize(3);
    }

    @Test
    public void TaskService_GetTaskById_ReturnTask() {
        Task task = defaultTask.toBuilder()
                .title("test task")
                .build();

        when(taskRepository.findById(anyLong())).thenReturn(Optional.ofNullable(task));

        TaskDto foundTaskDto = taskService.getTaskById(1L);

        verify(taskRepository).findById(anyLong());
        Assertions.assertThat(foundTaskDto).isNotNull();
        Assertions.assertThat(foundTaskDto.getTitle()).hasToString("test task");
    }

    @Test
    public void TaskService_GetTaskById_ThrowException() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> taskService.getTaskById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task not found with id: 1");
    }

    @Test
    public void TaskService_GetTaskContaining_ReturnListOfTaskDto() {
        Task task1 = defaultTask.toBuilder()
                .title("to be found 1")
                .build();
        Task task2 = defaultTask.toBuilder()
                .title("to be founded 2")
                .build();

        List<Task> tasks = List.of(task1, task2);
        when(taskRepository.findByTitleContaining("found")).thenReturn(tasks);

        List<TaskDto> foundTask = taskService.getTaskContaining("found");

        Assertions.assertThat(foundTask)
                .isNotNull()
                .hasSize(2)
                .extracting(TaskDto::getTitle)
                .contains("to be found 1", "to be founded 2");
        verify(taskRepository).findByTitleContaining("found");
    }

    @Test
    public void TaskService_AddTask_NullPriority_ReturnTaskDto(){
        TaskDto taskDto = defaultTaskDto.toBuilder()
                .title("title")
                .categoryId(null)
                .build();

        Task task = defaultTask.toBuilder()
                .title(taskDto.getTitle())
                .category(null)
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto addedTask = taskService.addTask(taskDto);

        verify(taskRepository).save(any(Task.class));
        Assertions.assertThat(addedTask)
                .extracting(TaskDto::getTitle, TaskDto::getPriority)
                .contains("title",3);
    }

    @Test
    public void TaskService_AddTask_TitleCategory_ReturnTaskDto(){
        TaskDto taskDto = defaultTaskDto.toBuilder()
                .title("test task")
                .priority(null)
                .completed(false)
                .categoryId(1L)
                .build();

        Task task = defaultTask.toBuilder()
                .title(taskDto.getTitle())
                .category(defaultCategory)
                .build();

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.ofNullable(defaultCategory));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto newTask = taskService.addTask(taskDto);

        verify(taskRepository).save(any(Task.class));
        verify(categoryRepository).findById(anyLong());
        Assertions.assertThat(newTask)
                .isNotNull()
                .extracting(TaskDto::getTitle, TaskDto::getCategoryId)
                .contains("test task", 1L);
    }

    @Test
    public void TaskService_AddTask_TitleDeadline_ReturnTaskDto(){
        TaskDto taskDto = defaultTaskDto.toBuilder()
                .title("test task")
                .completed(false)
                .categoryId(null)
                .endDate(LocalDateTime.of(2025, Month.FEBRUARY, 28, 6, 0, 0))
                .build();

        Task task = defaultTask.toBuilder()
                .title(taskDto.getTitle())
                .category(null)
                .endDate(taskDto.getEndDate())
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto newTask = taskService.addTask(taskDto);

        verify(taskRepository).save(any(Task.class));
        Assertions.assertThat(newTask.getEndDate())
                .hasMonth(Month.FEBRUARY)
                .hasDayOfMonth(28)
                .hasHour(6)
                .hasMinute(0)
                .hasSecond(0);
        Assertions.assertThat(newTask.getTitle()).isEqualTo("test task");
    }

    @Test
    public void TaskService_AddTask_NullStartDate_ReturnTaskDto(){
        TaskDto taskDto = defaultTaskDto.toBuilder()
                .startDate(null)
                .categoryId(null)
                .build();

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            return invocation.<Task>getArgument(0);
        });

        TaskDto newTask = taskService.addTask(taskDto);

        verify(taskRepository).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();

        Assertions.assertThat(savedTask.getStartDate()).isNotNull();
        Assertions.assertThat(savedTask.getStartDate()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
        Assertions.assertThat(newTask.getStartDate()).isEqualTo(savedTask.getStartDate());

    }

//    @Test
//    public void TaskService_UpdateTaskCompleted_ReturnToggledCompleted() {
//        Task task = Task.builder()
//                .title("Test Task")
//                .completed(false)
//                .build();
//        boolean originalStatus = task.isCompleted();
//
//        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
//
//        TaskDto updatedTask = taskService.updateTaskCompleted(1L);
//
//        verify(taskRepository).save(taskCaptor.capture());
//        Assertions.assertThat(updatedTask.isCompleted()).isEqualTo(!originalStatus);
//
//    }

    @Test
    public void TaskService_UpdateTask_Completed_ReturnUpdatedTask(){
        Task task = defaultTask.toBuilder()
                .title("Test Task")
                .completed(false)
                .build();
        TaskDto taskDto = defaultTaskDto.toBuilder()
                .completed(true)
                .build();
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(defaultCategory));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto updatedTask = taskService.updateTask(1L, taskDto);

        verify(taskRepository).save(task);
        Assertions.assertThat(updatedTask.isCompleted()).isEqualTo(true);
    }

    @Test
    public void TaskService_UpdateTask_Title_ReturnUpdatedTask() {
        TaskDto newTaskDto = defaultTaskDto.toBuilder()
                .title("New Title")
                .priority(null)
                .completed(false)
                .categoryId(null)
                .build();

        Task task = defaultTask.toBuilder()
                .title(newTaskDto.getTitle())
                .build();

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto updatedTask = taskService.updateTask(1L, newTaskDto);

        verify(taskRepository).save(task);
        Assertions.assertThat(updatedTask.getTitle()).isEqualTo("New Title");
    }

    @Test
    public void TaskService_UpdateTask_Priority_ReturnUpdatedTask(){
        TaskDto newTaskDto = defaultTaskDto.toBuilder()
                .title("task title")
                .priority(4)
                .build();

        Task task = defaultTask.toBuilder()
                .title("task title")
                .priority(4)
                .build();

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(defaultCategory));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto updatedTask = taskService.updateTask(1L, newTaskDto);

        Assertions.assertThat(updatedTask.getPriority()).isEqualTo(4);
        verify(taskRepository).save(any(Task.class));
    }

//    @Test
//    public void TaskService_UpdateTaskTitle_ThrowException_TooShort() {
//        Task task = Task.builder()
//                .title("Old Title")
//                .build();
//
//        TaskDto newTaskDto = TaskDto.builder()
//                .title("Ni")
//                .build();
//
//        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
//
//        Assertions.assertThatThrownBy(() -> taskService.updateTaskTitle(1L, newTaskDto))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("Title must be between 3 and 50 characters");
//        verify(taskRepository, never()).save(any());
//    }
//
//    @Test
//    public void TaskService_UpdateTaskTitle_ThrowException_TooLong(){
//        Task task = Task.builder()
//                .title("Old title")
//                .build();
//        TaskDto taskDto = TaskDto.builder()
//                .title("A".repeat(51))
//                .build();
//
//        when(taskRepository.findById(anyLong())).thenReturn(Optional.ofNullable(task));
//
//        Assertions.assertThatThrownBy(() -> taskService.updateTaskTitle(1L, taskDto))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("Title must be between 3 and 50 characters");
//    }

    @Test
    public void TaskService_DeleteTask_Success() {
        when(taskRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    public void TaskService_DeleteTask_ThrowException() {
        when(taskRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertThatThrownBy(() -> taskService.deleteTask(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task not found with id: 1");
    }

}
