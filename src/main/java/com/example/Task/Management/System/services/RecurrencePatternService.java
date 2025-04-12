package com.example.Task.Management.System.services;


import com.example.Task.Management.System.models.recurrence.RecurrencePattern;

public interface RecurrencePatternService {
    RecurrencePattern findById(Long id);
}
