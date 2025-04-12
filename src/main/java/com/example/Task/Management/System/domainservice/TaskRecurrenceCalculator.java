package com.example.Task.Management.System.domainservice;

import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.recurrence.TaskDuration;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

    //TODO :: Create function to calculate duration for checking and setting

@Component
public class TaskRecurrenceCalculator {
    public LocalDateTime calculateNextStartDate(
            LocalDateTime taskStartDate,
            RecurrencePattern pattern)
    {
        LocalDateTime nextStartDate = taskStartDate;
        int interval = pattern.getInterval();

        nextStartDate = switch (pattern.getRecurrenceType()) {
            case DAILY -> nextStartDate.plusDays(interval);
            case WEEKLY -> nextStartDate
                    .plusDays(Optional.of(pattern)
                            .map(RecurrencePattern::getDaysOfWeek)
                            .filter(daysOfWeek -> !daysOfWeek.isEmpty())
                            .map(daysOfWeek -> calculateNextDayOfWeek(taskStartDate.getDayOfWeek().getValue(), interval, daysOfWeek))
                            .orElse(7L  * interval));
            case MONTHLY -> {
                String monthDayRule  = pattern.getMonthDayRule();
                if (monthDayRule != null && !monthDayRule.isEmpty()){
                    nextStartDate = calculateNextMonthlyOccurrence(taskStartDate, pattern.getMonthDayRule(), interval);
                } else {
                    nextStartDate = nextStartDate.plusMonths(interval);
                }
                yield nextStartDate;
            }
            case YEARLY -> nextStartDate.plusYears(interval);
        };
        return nextStartDate;
    }

    private LocalDateTime calculateNextMonthlyOccurrence(
            LocalDateTime taskStartDate,
            String monthDayRule,
            int interval)
    {
        LocalDateTime nextMonth = taskStartDate.plusMonths(interval);

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

    private long calculateNextDayOfWeek(int currentDayOfWeek, int interval, List<DayOfWeek> daysOfWeek){
        for (java.time.DayOfWeek ofWeek : daysOfWeek) {
            int dayOfWeek = ofWeek.getValue();
            if (dayOfWeek > currentDayOfWeek) {
                return dayOfWeek - currentDayOfWeek;
            }
        }

        int nextDayOfWeek  = daysOfWeek.getFirst().getValue();
        return (7L * (interval - 1)) + (7 - currentDayOfWeek) + nextDayOfWeek;
    }

    public LocalDateTime calculateNextEndDate(
            LocalDateTime nextStartDate,
            RecurrencePattern pattern)
    {
        LocalDateTime taskEndDate = nextStartDate;
        LocalDateTime defaultEndDate = calculateNextStartDate(nextStartDate, pattern).minusSeconds(1);
        TaskDuration taskDuration = pattern.getTaskDuration();

        if (taskDuration == null || taskDuration.isEmpty()){ // Return default end date
            return defaultEndDate;
        }

        if (taskDuration.getMinutes() != null) taskEndDate = taskEndDate.plusMinutes(taskDuration.getMinutes());
        if (taskDuration.getHours() != null) taskEndDate = taskEndDate.plusHours(taskDuration.getHours());
        if (taskDuration.getDays() != null) taskEndDate = taskEndDate.plusDays(taskDuration.getDays());
        if (taskDuration.getWeeks() != null) taskEndDate = taskEndDate.plusWeeks(taskDuration.getWeeks());
        if (taskDuration.getMonths() != null) taskEndDate = taskEndDate.plusMonths(taskDuration.getMonths());
        if (taskDuration.getYears() != null) taskEndDate = taskEndDate.plusYears(taskDuration.getYears());

        if (taskEndDate.isAfter(defaultEndDate))
            return defaultEndDate;
        else
            return taskEndDate;

    }
}
