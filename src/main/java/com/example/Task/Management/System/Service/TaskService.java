package com.example.Task.Management.System.Service;

import com.example.Task.Management.System.DTO.TaskDto;
import com.example.Task.Management.System.models.Category;
import com.example.Task.Management.System.repository.CategoryRepository;
import com.example.Task.Management.System.repository.TaskRepository;
import com.example.Task.Management.System.models.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository){
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<TaskDto> getAllTasks(){
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    public TaskDto getTaskById(Long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        return convertToTaskDto(task);
    }

    public List<TaskDto> getTaskContaining(String toSearch) {
        List<Task> tasks = taskRepository.findByTitleContaining(toSearch);
        return tasks.stream().map(this::convertToTaskDto).collect(Collectors.toList());
    }

    public TaskDto addTask(TaskDto taskDto) {
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

        return convertToTaskDto(savedTask);
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

        return convertToTaskDto(savedTask);
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
        return tasks.stream().map(this::convertToTaskDto).toList();
    }

    public void deleteTask(Long id){
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    private TaskDto convertToTaskDto(Task task){
        return TaskDto.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .completed(task.isCompleted())
                .categoryId((task.getCategory() != null) ? task.getCategory().getCategoryId() : null)
                .priority(task.getPriority())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .finishedDate(task.getFinishedDate())
                .overdue(task.isOverdue())
                .build();
    }

//    private Task convertToEntity(TaskDto taskDto){
//        return new Task(taskDto.getId(), taskDto.getTitle(), taskDto.isCompleted(), , task);
//    }
}
