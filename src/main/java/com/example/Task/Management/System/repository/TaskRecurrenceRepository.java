package com.example.Task.Management.System.repository;

import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRecurrenceRepository extends JpaRepository<TaskRecurrence, Long> {
}
