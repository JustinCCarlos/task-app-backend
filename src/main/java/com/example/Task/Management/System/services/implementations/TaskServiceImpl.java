package com.example.Task.Management.System.services.implementations;

import com.example.Task.Management.System.domainservice.TaskFactory;
import com.example.Task.Management.System.domainservice.TaskRecurrenceFactory;
import com.example.Task.Management.System.dtos.Task.TaskDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import com.example.Task.Management.System.repository.TaskRecurrenceRepository;
import com.example.Task.Management.System.services.CategoryService;
import com.example.Task.Management.System.services.TaskService;
import com.example.Task.Management.System.models.Category;
import com.example.Task.Management.System.repository.CategoryRepository;
import com.example.Task.Management.System.repository.TaskRepository;
import com.example.Task.Management.System.mappers.TaskMapper;
import com.example.Task.Management.System.models.Task;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
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
    private final CategoryService categoryService;
    private final TaskRecurrenceRepository recurrenceRepository;
    private final TaskRecurrenceFactory recurrenceFactory;
    private final TaskMapper taskMapper;
    private final TaskFactory taskFactory;

    @Autowired
    public TaskServiceImpl(
            TaskRepository taskRepository,
            CategoryService categoryRepository,
            TaskRecurrenceRepository recurrenceRepository,
            TaskRecurrenceFactory recurrenceFactory,
            TaskMapper taskMapper,
            TaskFactory taskFactory)
    {
        this.taskRepository = taskRepository;
        this.categoryService = categoryRepository;
        this.recurrenceRepository = recurrenceRepository;
        this.recurrenceFactory = recurrenceFactory;
        this.taskMapper = taskMapper;
        this.taskFactory = taskFactory;
    }

    public List<TaskDto> getAllTasks(){
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public TaskDto getTaskById(Long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        return taskMapper.toDto(task);
    }

    public List<TaskDto> getTaskContaining(String toSearch) {
        List<Task> tasks = taskRepository.findByTitleContaining(toSearch);
        return tasks.stream().map(taskMapper::toDto).collect(Collectors.toList());
    }

    public TaskDto createTask(TaskDto taskDto) {
        Category category = (taskDto.getCategoryId() != null)
                ? categoryService.findById(taskDto.getCategoryId())
                : null;

        Task newTask = taskFactory.createTask(taskDto, category);
        Task savedTask = taskRepository.save(newTask);
        return taskMapper.toDto(savedTask);
    }

    @Transactional
    public TaskDto createTaskWithRecurrence(
            TaskDto taskDto,
            TaskRecurrenceDto recurrenceDto,
            RecurrencePatternDto patternDto)
    {
        Category category = (taskDto.getCategoryId() != null)
                ? categoryService.findById(taskDto.getCategoryId())
                : null;

        TaskRecurrence newRecurrence = recurrenceFactory.createRecurrence(recurrenceDto, patternDto);
        newRecurrence = recurrenceRepository.save(newRecurrence);

        Task newTask = taskFactory.createTask(taskDto, category, newRecurrence);
        newTask = taskRepository.save(newTask);

        newRecurrence.setGeneratedTasks(List.of(newTask));
        recurrenceRepository.save(newRecurrence);

        return taskMapper.toDto(newTask);

    }

    public TaskDto updateTask(Long id, TaskDto taskDto){
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        Category category = (taskDto.getCategoryId() != null)
                ? categoryService.findById(taskDto.getCategoryId())
                : null;

        taskMapper.updateEntity(existingTask, taskDto, category);

        try {
            taskFactory.validateUpdate(existingTask);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        Task savedTask = taskRepository.save(existingTask);

        return taskMapper.toDto(savedTask);
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
        return tasks.stream().map(taskMapper::toDto).toList();
    }

    public void deleteTask(Long id){
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

}
