package com.example.Task.Management.System.repository;

import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurrencePatternRepository extends JpaRepository<RecurrencePattern, Long> {
}
