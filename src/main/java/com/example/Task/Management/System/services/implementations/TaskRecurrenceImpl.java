package com.example.Task.Management.System.services.implementations;

import com.example.Task.Management.System.dtos.Task.TaskDto;
import com.example.Task.Management.System.mappers.TaskMapper;
import com.example.Task.Management.System.dtos.TaskRecurrence.RecurrencePatternDto;
import com.example.Task.Management.System.mappers.RecurrencePatternMapper;
import com.example.Task.Management.System.dtos.TaskRecurrence.TaskRecurrenceDto;
import com.example.Task.Management.System.mappers.TaskRecurrenceMapper;
import com.example.Task.Management.System.services.TaskRecurrenceService;
import com.example.Task.Management.System.services.TaskService;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.recurrence.TaskDuration;
import com.example.Task.Management.System.models.recurrence.TaskRecurrence;
import com.example.Task.Management.System.models.Task;
import com.example.Task.Management.System.repository.TaskRecurrenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
public class TaskRecurrenceImpl implements TaskRecurrenceService {

    private static final Logger logger = LoggerFactory.getLogger(TaskRecurrenceImpl.class);

    private final TaskRecurrenceRepository taskRecurrenceRepository;
    private final TaskService taskService;

    @Autowired
    public TaskRecurrenceImpl(TaskRecurrenceRepository taskRecurrenceRepository, TaskService taskService){
        this.taskRecurrenceRepository = taskRecurrenceRepository;
        this.taskService = taskService;
    }

    public TaskRecurrenceDto createRecurrence(TaskRecurrenceDto recurrenceDto) {
        TaskRecurrence taskRecurrence = TaskRecurrenceMapper.toEntity(recurrenceDto);
        TaskRecurrence savedRecurrence = taskRecurrenceRepository.save(taskRecurrence);
        return TaskRecurrenceMapper.toDto(savedRecurrence);
    }

    public void generateNextTask(TaskRecurrenceDto recurrenceDto, RecurrencePatternDto patternDto) {
        try{
            logger.info("Generating next task...");
            TaskRecurrence taskRecurrence = TaskRecurrenceMapper.toEntity(recurrenceDto);
            RecurrencePattern pattern = RecurrencePatternMapper.toEntity(patternDto);
            Task currentTask = taskRecurrence.getGeneratedTasks().getLast();

            LocalDateTime nextStartDate = calculateNextStartDate(taskRecurrence.getRecurrenceStartDate(), taskRecurrence, pattern);
            LocalDateTime nextEndDate = calculateNextEndDate(currentTask.getStartDate(), taskRecurrence, nextStartDate, pattern);

            TaskDto nextTaskDto = TaskMapper.toDto(currentTask);
            nextTaskDto = nextTaskDto.toBuilder()
                    .completed(false)
                    .startDate(nextStartDate)
                    .endDate(nextEndDate)
                    .finishedDate(null)
                    .overdue(false)
                    .build();

            taskService.addTask(nextTaskDto);
            System.out.println(nextTaskDto);
            logger.info("Next task generated with dates: start={}, end{}", nextStartDate, nextEndDate);
        } catch (Exception e) {
            logger.error("Error generating next task: ", e);
        }


    }

    private LocalDateTime calculateNextStartDate(LocalDateTime recurrenceStartDate, TaskRecurrence taskRecurrence, RecurrencePattern pattern) {
        LocalDateTime nextStartDate = recurrenceStartDate;
        int interval = taskRecurrence.getRecurrencePattern().getInterval();
        nextStartDate = switch (pattern.getRecurrenceType()) {
            case DAILY -> nextStartDate.plusDays(interval);
            case WEEKLY -> nextStartDate
                    .plusWeeks(interval)
                    .plusDays(Optional.ofNullable(taskRecurrence.getRecurrencePattern())
                            .map(RecurrencePattern::getDaysOfWeek)
                            .filter(daysOfWeek -> !daysOfWeek.isEmpty())
                            .map(daysOfWeek -> calculateNextDayOfWeek(recurrenceStartDate.getDayOfWeek().getValue(), daysOfWeek))
                            .orElse(0L));
            case MONTHLY -> {
                String recurrencePattern  = taskRecurrence.getRecurrencePattern().getMonthDayRule();
                if (recurrencePattern != null && !recurrencePattern.isEmpty()){
                    nextStartDate = calculateNextMonthlyOccurrence(recurrenceStartDate, taskRecurrence.getRecurrencePattern().getMonthDayRule(), interval);
                } else {
                    nextStartDate = nextStartDate.plusMonths(interval);
                }
                yield nextStartDate;
            }
            case YEARLY -> nextStartDate.plusYears(interval);
        };
        return nextStartDate;
    }

    private LocalDateTime calculateNextMonthlyOccurrence(LocalDateTime recurrenceStartDate, String monthDayRule, int interval) {
        LocalDateTime nextMonth = recurrenceStartDate.plusMonths(interval);

        if (monthDayRule.startsWith("day_")) {
            // Extract numeric day, e.g., "day_15" → 15
            int dayOfMonth = Integer.parseInt(monthDayRule.split("_")[1]);
            return nextMonth.withDayOfMonth(Math.min(dayOfMonth, nextMonth.getMonth().maxLength()));
        }
        else if (monthDayRule.matches("^(first|second|third|fourth|last)_(monday|tuesday|wednesday|thursday|friday|saturday|sunday)$")) {
            return getNthWeekdayOfMonth(nextMonth, monthDayRule);
        }

        throw new IllegalArgumentException("Invalid monthDayRule: " + monthDayRule);
    }

    private LocalDateTime getNthWeekdayOfMonth(LocalDateTime nextMonth, String monthDayRule) {
        String[] parts = monthDayRule.split("_");
        String nth = parts[0]; // "first", "second", etc.
        DayOfWeek targetDay = DayOfWeek.valueOf(parts[1].toUpperCase()); // "wednesday" → WEDNESDAY

        int weekNumber = switch (nth) {
            case "first" -> 1;
            case "second" -> 2;
            case "third" -> 3;
            case "fourth" -> 4;
            case "last" -> -1;
            default -> throw new IllegalArgumentException("Invalid nth value in monthDayRule: " + nth);
        };

        return nextMonth.with(TemporalAdjusters.dayOfWeekInMonth(weekNumber, targetDay));
    }

    private long calculateNextDayOfWeek(int currentDayOfWeek, List<java.time.DayOfWeek> daysOfWeek){
        for (java.time.DayOfWeek ofWeek : daysOfWeek) {
            int dayOfWeek = ofWeek.getValue();
            if (dayOfWeek > currentDayOfWeek) {
                return dayOfWeek - currentDayOfWeek;
            }
        }

        int nextDayOfWeek  = daysOfWeek.getFirst().getValue();
        return (7 - currentDayOfWeek) + nextDayOfWeek;
    }

    private LocalDateTime calculateNextEndDate(LocalDateTime taskStartDate, TaskRecurrence taskRecurrence, LocalDateTime nextStartDate, RecurrencePattern pattern){
        LocalDateTime taskEndDate = taskStartDate;
        TaskDuration taskDuration = pattern.getTaskDuration();
        if (taskDuration == null || taskDuration.isEmpty()){ // Return default end date
            return calculateNextStartDate(nextStartDate, taskRecurrence, pattern).minusNanos(1);
        }

        if (taskDuration.getMinutes() != null) taskEndDate = taskEndDate.plusMinutes(taskDuration.getMinutes());
        if (taskDuration.getHours() != null) taskEndDate = taskEndDate.plusHours(taskDuration.getHours());
        if (taskDuration.getDays() != null) taskEndDate = taskEndDate.plusDays(taskDuration.getDays());
        if (taskDuration.getWeeks() != null) taskEndDate = taskEndDate.plusWeeks(taskDuration.getWeeks());
        if (taskDuration.getMonths() != null) taskEndDate = taskEndDate.plusMonths(taskDuration.getMonths());
        if (taskDuration.getYears() != null) taskEndDate = taskEndDate.plusYears(taskDuration.getYears());

        return taskEndDate;
    }

    public void deleteTaskRecurrence(Long id){
        if (!taskRecurrenceRepository.existsById(id)){
            throw new EntityNotFoundException("Recurrence not found with id: " + id);
        }
        taskRecurrenceRepository.deleteById(id);
    }

}
