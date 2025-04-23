package com.example.Task.Management.System.domainservice;

import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.mappers.RecurrencePatternMapper;
import com.example.Task.Management.System.mappers.TaskDurationMapper;
import com.example.Task.Management.System.mappers.TaskRecurrenceMapper;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.recurrence.TaskDuration;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TaskRecurrenceFactory {

    private static final Logger logger = LoggerFactory.getLogger(TaskRecurrenceFactory.class);

    private final TaskRecurrenceMapper recurrenceMapper;
    private final RecurrencePatternFactory patternFactory;

    public TaskRecurrence createRecurrence(TaskRecurrenceDto recurrenceDto, RecurrencePatternDto patternDto){

        RecurrencePattern pattern = patternFactory.createPattern(patternDto);

        TaskRecurrence recurrence = recurrenceMapper.toEntity(recurrenceDto, pattern);

        try{
            validateRecurrence(recurrence);
        } catch (ValidationException e) {
            logger.error("TaskRecurrence validation failed while creating recurrence", e);
            throw new RuntimeException(e);
        }
        return recurrence;
    }


    private void validateRecurrence(TaskRecurrence recurrence) throws ValidationException {
        if (recurrence.getRecurrenceStartDate() == null) {
            throw new ValidationException("Recurrence start date is required");
        }
        if (recurrence.getRecurrenceEndDate() != null && recurrence.getMaxOccurrences() != null) {
            throw new ValidationException("Either end date or max occurrences can be set, but not both");
        }
    }

}
