package com.example.Task.Management.System.repository;

import com.example.Task.Management.System.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // TODO:: add find tasks that contains specific string (match case and whole words)

    List<Task> findByCompleted(boolean Completed);

    List<Task> findByTitleContaining(String toSearch);

}
