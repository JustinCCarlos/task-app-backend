package com.example.Task.Management.System.domainservice;

import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.exceptions.DomainValidationException;
import com.example.Task.Management.System.mappers.RecurrencePatternMapper;
import com.example.Task.Management.System.mappers.TaskDurationMapper;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.recurrence.TaskDuration;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.validation.ValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecurrencePatternFactory {
    private static final Logger logger = LoggerFactory.getLogger(RecurrencePatternFactory.class);

    private final RecurrencePatternMapper patternMapper;
    private final TaskDurationMapper durationMapper;

    public RecurrencePattern createPattern(RecurrencePatternDto patternDto){
        TaskDuration duration = null;
        if (patternDto.taskDurationDto() != null){
            duration = durationMapper.toEntity(patternDto.taskDurationDto());
        }

        RecurrencePattern pattern = patternMapper.toEntity(patternDto, duration);

        try {
            validatePattern(pattern);
        } catch (ValidationException e) {
            logger.error("RecurrencePattern validation failed while creating recurrence", e);
            throw new RuntimeException(e);
        }

        return pattern;
    }


    private void validatePattern(RecurrencePattern pattern) throws ValidationException {
        if (pattern.getInterval() < 1 || pattern.getInterval() > 99)
            throw new ValidationException("Interval must be between 1 and 99");
        if (pattern.getRecurrenceType() == null)
            throw new ValidationException("Recurrence Type is required");

        String monthDayRule = pattern.getMonthDayRule();
        if (monthDayRule != null) {
            String[] parts = monthDayRule.split("_");
            if (parts.length != 2) {
                throw new ValidationException("Invalid monthDayRule format: " + monthDayRule);
            }
            if (parts[0].equals("day")) {
                try {
                    int day = Integer.parseInt(parts[1]);
                    if (day < 1 || day > 31) {
                        throw new ValidationException("Invalid day value in monthDayRule: '" + monthDayRule + "'. Day must be between 1 and 31.");
                    }
                } catch (NumberFormatException e) {
                    throw new ValidationException("Invalid day format in monthDayRule: '" + monthDayRule + "'. Day must be a number between 1 and 31.");
                }
            } else {
                String[] allowedNths = {"first", "second", "third", "fourth", "last"};
                String[] allowedDays = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

                List<String> errors = new ArrayList<>();

                if (!Arrays.asList(allowedNths).contains(parts[0])) {
                    errors.add("Invalid nth value: '" + parts[0] + "'");
                }
                if (!Arrays.asList(allowedDays).contains(parts[1])) {
                    errors.add("Invalid day value: '" + parts[1] + "'");
                }

                if (!errors.isEmpty()) {
                    throw new DomainValidationException(errors);
                }
            }
        }

    }
}
