package com.example.Task.Management.System.Service;

import com.example.Task.Management.System.DTO.TaskDto;
import com.example.Task.Management.System.models.Category;
import com.example.Task.Management.System.repository.CategoryRepository;
import com.example.Task.Management.System.repository.TaskRepository;
import com.example.Task.Management.System.models.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
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
//        return taskRepository.findAll();
    }

    public TaskDto getTaskById(Long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        return convertToTaskDto(task);
//        return taskRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
    }

    public List<TaskDto> getTaskContaining(String toSearch) {
        List<Task> tasks = taskRepository.findByTitleContaining(toSearch);
        return tasks.stream().map(this::convertToTaskDto).collect(Collectors.toList());
    }

    public TaskDto addTask(TaskDto taskDto) {
        if ((taskDto.getPriority() < 1 || taskDto.getPriority() > 5)){
            throw new IllegalArgumentException("Priority must be between 1 and 5");
        }

        if(taskDto.getCategoryId() != null){
            Category category = categoryRepository.findById(taskDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        }

        Task newTask = Task.builder()
                .title(taskDto.getTitle())
                .priority(taskDto.getPriority())
                .endDate(taskDto.getEndDate())
                .build();

        Task savedTask = taskRepository.save(newTask);

        return convertToTaskDto(savedTask);
    }

//    public TaskDto updateTaskCompleted(Long id){
//        Task task = taskRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
//        task.setCompleted(!task.isCompleted());
//        taskRepository.save(task);
//
//        return convertToTaskDto(task);
//    }

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

        task.setEndDate(taskDto.getEndDate());

        Task savedTask = taskRepository.save(task);

        return convertToTaskDto(savedTask);
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
                .endDate(task.getEndDate())
                .overdue(task.isOverdue())
                .build();
    }

//    private Task convertToEntity(TaskDto taskDto){
//        return new Task(taskDto.getId(), taskDto.getTitle(), taskDto.isCompleted(), , task);
//    }
}
