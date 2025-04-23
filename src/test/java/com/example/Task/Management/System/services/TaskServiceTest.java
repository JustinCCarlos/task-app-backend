package com.example.Task.Management.System.services;

import com.example.Task.Management.System.domainservice.TaskFactory;
import com.example.Task.Management.System.domainservice.TaskRecurrenceFactory;
import com.example.Task.Management.System.dtos.Task.TaskDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.mappers.TaskMapper;
import com.example.Task.Management.System.models.recurrence.RecurrenceType;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import com.example.Task.Management.System.repository.TaskRecurrenceRepository;
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
    private CategoryService categoryService;
    @Mock
    private TaskRecurrenceRepository recurrenceRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private TaskRecurrenceFactory recurrenceFactory;
    @Mock
    private TaskFactory taskFactory;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    private Category defaultCategory;
    private Task defaultTask;
    private TaskDto defaultTaskDto;

    @BeforeEach
    public void init() {
        taskCaptor = ArgumentCaptor.forClass(Task.class);

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
                .title("Default Task")
                .completed(false)
                .categoryId(1L)
                .priority(3)
                .startDate(defaultStartDate)
                .endDate(null)
                .finishedDate(null)
                .overdue(false)
                .taskRecurrenceId(null)
                .build();
    }

    @Test
    public void retrieve_all_task_from_database(){
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
    public void valid_taskId_returns_task() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.ofNullable(defaultTask));
        when(taskMapper.toDto(defaultTask)).thenReturn(defaultTaskDto.toBuilder().taskId(1L).build());

        TaskDto foundTaskDto = taskService.getTaskById(1L);

        verify(taskRepository).findById(anyLong());
        Assertions.assertThat(foundTaskDto).isNotNull();
        Assertions.assertThat(foundTaskDto.getTitle()).hasToString("Default Task");
    }

    @Test
    public void invalid_taskId_throws_exception() {
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
                .title("to be found 2")
                .build();

        List<Task> tasks = List.of(task1, task2);
        when(taskRepository.findByTitleContaining("found")).thenReturn(tasks);
        when(taskMapper.toDto(any(Task.class))).thenAnswer(inv -> {
             Task task = inv.getArgument(0);
             return TaskDto.builder()
                     .title(task.getTitle())
                     .build();
        });

        List<TaskDto> foundTask = taskService.getTaskContaining("found");

        Assertions.assertThat(foundTask)
                .isNotNull()
                .hasSize(2)
                .extracting(TaskDto::getTitle)
                .contains("to be found 1", "to be found 2");
        verify(taskRepository).findByTitleContaining("found");
    }
// commented because test is moved over to TaskFactory
//    @Test
//    public void created_task_with_null_priority_defaults_to_3(){
//        TaskDto taskDto = defaultTaskDto.toBuilder()
//                .title("title")
//                .priority(null)
//                .categoryId(null)
//                .build();
//
//        Task task = defaultTask.toBuilder()
//                .title(taskDto.getTitle())
//                .priority(3)
//                .category(null)
//                .build();
//
//        when(taskRepository.save(any(Task.class))).thenReturn(task);
//
//        TaskDto addedTask = taskService.createTask(taskDto);
//
//        verify(taskRepository).save(any(Task.class));
//        Assertions.assertThat(addedTask)
//                .extracting(TaskDto::getTitle, TaskDto::getPriority)
//                .contains("title",3);
//    }

    @Test
    public void task_with_category_is_created(){
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

        TaskDto returnedDto = taskDto.toBuilder()
                .categoryId(1L)
                .build();

        when(categoryService.findById(anyLong())).thenReturn(defaultCategory);
        when(taskFactory.createTask(any(TaskDto.class),any(Category.class))).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDto(any(Task.class))).thenReturn(returnedDto);

        TaskDto newTask = taskService.createTask(taskDto);

        verify(categoryService).findById(anyLong());
        verify(taskFactory).createTask(taskDto, defaultCategory);
        verify(taskRepository).save(any(Task.class));
        verify(taskMapper).toDto(task);

        Assertions.assertThat(newTask)
                .isNotNull()
                .extracting(TaskDto::getTitle, TaskDto::getCategoryId)
                .contains("test task", 1L);
    }

    @Test
    public void Task_with_correct_endDate_is_created_successfully(){
        TaskDto taskDto = defaultTaskDto.toBuilder()
                .title("test task")
                .completed(false)
                .categoryId(null)
                .endDate(LocalDateTime.of(2025, Month.FEBRUARY, 28, 6, 0, 0))
                .build();

        Task newTask = defaultTask.toBuilder()
                .title(taskDto.getTitle())
                .category(null)
                .endDate(taskDto.getEndDate())
                .build();

        TaskDto newTaskDto = taskDto.toBuilder()
                .taskId(1L)
                .build();

        when(taskFactory.createTask(any(TaskDto.class), isNull())).thenReturn(newTask);
        when(taskRepository.save(any(Task.class))).thenReturn(newTask);
        when(taskMapper.toDto(any(Task.class))).thenReturn(newTaskDto);

        TaskDto savedTask = taskService.createTask(taskDto);

        verify(taskRepository).save(newTask);
        Assertions.assertThat(savedTask.getTaskId()).isEqualTo(1L);
        Assertions.assertThat(savedTask.getEndDate())
                .hasMonth(Month.FEBRUARY)
                .hasDayOfMonth(28)
                .hasHour(6)
                .hasMinute(0)
                .hasSecond(0);
        Assertions.assertThat(savedTask.getTitle()).isEqualTo("test task");
    }

    @Test
    void create_task_repeating_every_day(){
        TaskRecurrenceDto recurrenceDto = TaskRecurrenceDto.builder()
                .recurrenceStartDate(defaultTask.getStartDate())
                .recurrenceEndDate(null)
                .maxOccurrences(5)
                .active(true)
                .build();

        RecurrencePatternDto patternDto = RecurrencePatternDto.builder()
                .interval(1)
                .recurrenceType(RecurrenceType.DAILY)
                .taskDurationDto(null)
                .build();

        TaskRecurrence newRecurrence = TaskRecurrence.builder()
                .taskRecurrenceId(1L)
                .recurrenceStartDate(defaultTask.getStartDate())
                .maxOccurrences(5)
                .active(true)
                .build();

        Task taskWithRecurrence = defaultTask.toBuilder()
                .taskRecurrence(newRecurrence)
                .build();

        TaskDto taskDtoWithRecurrence = defaultTaskDto.toBuilder()
                .taskRecurrenceId(newRecurrence.getTaskRecurrenceId())
                .build();

        when(categoryService.findById(anyLong())).thenReturn(defaultCategory);
        when(recurrenceFactory.createRecurrence(any(TaskRecurrenceDto.class), any(RecurrencePatternDto.class))).thenReturn(newRecurrence);
        when(recurrenceRepository.save(any(TaskRecurrence.class))).thenReturn(newRecurrence);
        when(taskFactory.createTask(any(TaskDto.class), any(Category.class), any(TaskRecurrence.class))).thenReturn(taskWithRecurrence);
        when(taskRepository.save(any(Task.class))).thenReturn(taskWithRecurrence);
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDtoWithRecurrence);

        TaskDto result = taskService.createTaskWithRecurrence(defaultTaskDto, recurrenceDto, patternDto);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getTitle()).isEqualTo("Default Task");
        verify(recurrenceRepository, times(2)).save(any(TaskRecurrence.class));
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
    public void Task_updated_as_completed(){
        Task existingTask = defaultTask.toBuilder()
                .title("Test Task")
                .completed(false)
                .build();
        TaskDto updatedTaskDto = defaultTaskDto.toBuilder()
                .completed(true)
                .build();
        Task updatedtask = existingTask.toBuilder()
                .completed(true)
                .build();

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(existingTask));
        when(categoryService.findById(anyLong())).thenReturn(defaultCategory);
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);
        when(taskMapper.toDto(any(Task.class))).thenReturn(updatedTaskDto);

        TaskDto updatedTask = taskService.updateTask(1L, updatedTaskDto);

        verify(taskRepository).save(existingTask);
        Assertions.assertThat(updatedTask.isCompleted()).isEqualTo(true);
    }

    @Test
    public void Task_updated_with_new_title() {
        TaskDto updatedTaskDto = defaultTaskDto.toBuilder()
                .title("New Title")
                .build();

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(defaultTask));
        when(categoryService.findById(anyLong())).thenReturn(defaultCategory);
        when(taskRepository.save(any(Task.class))).thenReturn(defaultTask);
        when(taskMapper.toDto(any(Task.class))).thenReturn(updatedTaskDto);

        TaskDto updatedTask = taskService.updateTask(1L, updatedTaskDto);

        verify(taskRepository).save(defaultTask);
        Assertions.assertThat(updatedTask.getTitle()).isEqualTo("New Title");
    }

    @Test
    public void TaskService_UpdateTask_Priority_ReturnUpdatedTask(){
        TaskDto updatedTaskDto = defaultTaskDto.toBuilder()
                .priority(4)
                .build();

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(defaultTask));
        when(categoryService.findById(anyLong())).thenReturn(defaultCategory);
        when(taskRepository.save(any(Task.class))).thenReturn(defaultTask);
        when(taskMapper.toDto(any(Task.class))).thenReturn(updatedTaskDto);

        TaskDto updatedTask = taskService.updateTask(1L, updatedTaskDto);

        verify(taskRepository).save(defaultTask);
        Assertions.assertThat(updatedTask.getPriority()).isEqualTo(4);

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
