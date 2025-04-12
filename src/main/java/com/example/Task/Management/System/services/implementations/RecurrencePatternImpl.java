package com.example.Task.Management.System.services.implementations;

import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.repository.RecurrencePatternRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;

public class RecurrencePatternImpl {
    private final RecurrencePatternRepository patternRepository;

    @Autowired
    public RecurrencePatternImpl(RecurrencePatternRepository patternRepository){
        this.patternRepository = patternRepository;
    }

    public RecurrencePattern findById(Long id){
        return patternRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pattern not found with id: " + id));

    }
}
