package com.example.Task.Management.System.repository;

import com.example.Task.Management.System.models.Task;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void TaskRepository_FindById_ReturnTask(){
        Task task = Task.builder()
                .title("Test Title")
                .build();

        taskRepository.save(task);

        Task retrievedTask = taskRepository.findById(task.getTask_id()).get();

        Assertions.assertThat(retrievedTask).isNotNull();
        Assertions.assertThat(retrievedTask.getTitle())
                .isEqualTo("Test Title");
    }

    @Test
    public void TaskRepository_Save_ReturnSavedTask(){
        Task task = Task.builder()
                .title("Test Add Title")
                .build();

        Task savedTask = taskRepository.save(task);

        Assertions.assertThat(savedTask).isNotNull();
        Assertions.assertThat(savedTask.getTask_id()).isGreaterThan(0L);

    }

    @Test
    public void TaskRepository_Delete_ReturnNull(){
        Task task = Task.builder()
                .title("Test Delete Title")
                .build();

        taskRepository.save(task);
        taskRepository.deleteById(task.getTask_id());

        Task returnTask = taskRepository.findById(task.getTask_id()).orElse(null);

        Assertions.assertThat(returnTask).isNull();

    }

    @Test
    public void TaskRepository_FindAll_ReturnAllTask(){
        Task task = Task.builder()
                .title("Title")
                .build();
        Task task2 = Task.builder()
                .title("Title2")
                .build();

        taskRepository.save(task);
        taskRepository.save(task2);

        List<Task> allTask = taskRepository.findAll();

        Assertions.assertThat(allTask).isNotNull();
        Assertions.assertThat(allTask.size()).isEqualTo(2);
        Assertions.assertThat(allTask)
                .extracting(Task::getTitle)
                .containsExactlyInAnyOrder("Title", "Title2");
    }

    @Test
    public void TaskRepository_FindByIsCompleted_ReturnCompleteTask(){
        Task task = Task.builder()
                .title("Completed Task")
                .completed(true)
                .build();

        Task task2 = Task.builder()
                .title("Completed Task2")
                .completed(true)
                .build();

        Task task3 = Task.builder()
                .title("Complete Task")
                .completed(false)
                .build();

        taskRepository.save(task);
        taskRepository.save(task2);
        taskRepository.save(task3);

        List<Task> completedTaskList = taskRepository.findByCompleted(true);

        Assertions.assertThat(completedTaskList)
                .isNotNull();
        Assertions.assertThat(completedTaskList.size())
                .isEqualTo(2);

    }

    @Test
    public void TaskRepository_FindByTitleContainingString_ReturnListOfTask(){
        Task task = Task.builder()
                .title("Task 1")
                .build();
        Task task2 = Task.builder()
                .title("Task 2")
                .build();
        Task task3 = Task.builder()
                .title("Something")
                .build();
        Task task4 = Task.builder()
                .title("Tasks 4")
                .build();

        taskRepository.save(task);
        taskRepository.save(task2);
        taskRepository.save(task3);
        taskRepository.save(task4);

        List<Task> foundTask = taskRepository.findByTitleContaining("Task");

        Assertions.assertThat(foundTask).isNotNull()
                .hasSize(3)
                .contains(task)
                .contains(task2)
                .contains(task4);
    }

    @Test
    public void TaskRepository_UpdateTaskTitle_ReturnUpdatedTask(){
        Task task = Task.builder()
                .title("Initial Task")
                .build();

        Task updatedTask = taskRepository.save(task);

        updatedTask.setTitle("Updated Task");
        taskRepository.save(updatedTask);

        Assertions.assertThat(updatedTask.getTitle())
                .isEqualTo("Updated Task");
    }

    @Test
    public void TaskRepository_UpdateTaskCompleted_ReturnUpdatedCompleted(){
        Task task = Task.builder()
                .title("New Task")
                .build();

        boolean initialCompleted = task.isCompleted();

        taskRepository.save(task);

        Task updatedTask = taskRepository.findById(task.getTask_id()).orElseThrow();
        updatedTask.setCompleted(!task.isCompleted());
        updatedTask = taskRepository.save(updatedTask);

        Assertions.assertThat(updatedTask.isCompleted())
                .isNotEqualTo(initialCompleted);
    }
}
