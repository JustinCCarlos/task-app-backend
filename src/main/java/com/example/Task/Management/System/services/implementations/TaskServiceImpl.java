package com.example.Task.Management.System.services.implementations;

import com.example.Task.Management.System.dtos.Task.TaskDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskDurationDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.mappers.RecurrencePatternMapper;
import com.example.Task.Management.System.mappers.TaskDurationMapper;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.recurrence.TaskDuration;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import com.example.Task.Management.System.repository.TaskRecurrenceRepository;
import com.example.Task.Management.System.services.TaskService;
import com.example.Task.Management.System.models.Category;
import com.example.Task.Management.System.repository.CategoryRepository;
import com.example.Task.Management.System.repository.TaskRepository;
import com.example.Task.Management.System.mappers.TaskMapper;
import com.example.Task.Management.System.models.Task;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRecurrenceRepository recurrenceRepository;

    @Autowired
    public TaskServiceImpl(
            TaskRepository taskRepository,
            CategoryRepository categoryRepository,
            TaskRecurrenceRepository recurrenceRepository)
    {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.recurrenceRepository = recurrenceRepository;
    }

    public List<TaskDto> getAllTasks(){
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(TaskMapper::toDto)
                .collect(Collectors.toList());
    }

    public TaskDto getTaskById(Long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        return TaskMapper.toDto(task);
    }

    public List<TaskDto> getTaskContaining(String toSearch) {
        List<Task> tasks = taskRepository.findByTitleContaining(toSearch);
        return tasks.stream().map(TaskMapper::toDto).collect(Collectors.toList());
    }

    public TaskDto createTask(TaskDto taskDto) {
        Category category = null;

        if ((taskDto.getPriority() < 1 || taskDto.getPriority() > 5)){
            throw new IllegalArgumentException("Priority must be between 1 and 5");
        }

        if (taskDto.getStartDate() == null){
            taskDto.setStartDate(LocalDateTime.now());
        }

        if (taskDto.getCategoryId() != null){
            category = categoryRepository.findById(taskDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        }

        Task newTask = Task.builder()
                .title(taskDto.getTitle())
                .category(category)
                .priority(taskDto.getPriority())
                .startDate(taskDto.getStartDate())
                .endDate(taskDto.getEndDate())
                .build();

        Task savedTask = taskRepository.save(newTask);

        return TaskMapper.toDto(savedTask);
    }

    @Transactional
    public TaskDto createTaskWithRecurrence(
            TaskDto taskDto,
            TaskRecurrenceDto recurrenceDto,
            RecurrencePatternDto patternDto)
    {
        TaskDuration newDuration = TaskDurationMapper.toEntity(patternDto.taskDurationDto());

        RecurrencePattern pattern = RecurrencePattern.builder()
                .interval(patternDto.interval())
                .recurrenceType(patternDto.recurrenceType())
                .taskDuration(newDuration)
                .daysOfWeek(patternDto.daysOfWeek())
                .monthDayRule(patternDto.monthDayRule())
                .build();

        TaskRecurrence newRecurrence = TaskRecurrence.builder()
                .recurrenceStartDate(recurrenceDto.recurrenceStartDate())
                .recurrenceEndDate(recurrenceDto.recurrenceEndDate())
                .maxOccurrences(recurrenceDto.maxOccurrences())
                .active(true)
                .recurrencePattern(pattern)
                .build();

        newRecurrence = recurrenceRepository.save(newRecurrence);

        Task newTask = TaskMapper.toEntity(createTask(taskDto));
        newTask.setTaskRecurrence(newRecurrence);
        newTask = taskRepository.save(newTask);

        newRecurrence.setGeneratedTasks(List.of(newTask));
        recurrenceRepository.save(newRecurrence);

        return TaskMapper.toDto(newTask);

    }

    public TaskDto updateTask(Long id, TaskDto taskDto){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        task.setCompleted(taskDto.isCompleted());

        if(taskDto.getTitle() != null && !taskDto.getTitle().equals(task.getTitle())){
            task.setTitle(taskDto.getTitle());
        }

        if(taskDto.getCategoryId() != null){
            Category category = categoryRepository.findById(taskDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            task.setCategory(category);
        } else {
            task.setCategory(null);
        }

        if (taskDto.getPriority() != null && !taskDto.getPriority().equals(task.getPriority())){
            if (taskDto.getPriority() < 1 || taskDto.getPriority() > 5){
                throw new IllegalArgumentException("Priority must be between 1 and 5");
            }
            task.setPriority(taskDto.getPriority());
        }

        if (taskDto.getStartDate() != null && !taskDto.getStartDate().equals(task.getStartDate())){
            task.setStartDate(taskDto.getStartDate());
        }

        task.setFinishedDate(taskDto.getFinishedDate());
        task.setEndDate(taskDto.getEndDate());

        Task savedTask = taskRepository.save(task);

        return TaskMapper.toDto(savedTask);
    }

    public List<TaskDto> getFilteredTasks(Boolean isComplete, String title, String sortBy, String sortDirection, LocalDateTime startDate, LocalDateTime endDate){
        Specification<Task> spec = Specification.where(null);

        if (isComplete != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("completed"), isComplete));
        }

        if (title != null && !title.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }

        if (startDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startDate"), startDate));
        }

        if (endDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("endDate"), endDate));
        }

        Sort sort = Sort.unsorted();

        if (sortBy != null && !sortBy.isEmpty()){
            sort = sortDirection != null && sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        }

        List<Task> tasks = taskRepository.findAll(spec, sort);
        return tasks.stream().map(TaskMapper::toDto).toList();
    }

    public void deleteTask(Long id){
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

//    private TaskDto convertToTaskDto(Task task){
//        return TaskDto.builder()
//                .taskId(task.getTaskId())
//                .title(task.getTitle())
//                .completed(task.isCompleted())
//                .categoryId((task.getCategory() != null) ? task.getCategory().getCategoryId() : null)
//                .priority(task.getPriority())
//                .startDate(task.getStartDate())
//                .endDate(task.getEndDate())
//                .finishedDate(task.getFinishedDate())
//                .overdue(task.isOverdue())
//                .build();
//    }

//    private Task convertToEntity(TaskDto taskDto){
//        return new Task(taskDto.getId(), taskDto.getTitle(), taskDto.isCompleted(), , task);
//    }
}
