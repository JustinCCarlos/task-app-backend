package com.example.Task.Management.System.services;

import com.example.Task.Management.System.domainservice.TaskRecurrenceCalculator;
import com.example.Task.Management.System.models.recurrence.RecurrencePattern;
import com.example.Task.Management.System.models.recurrence.RecurrenceType;
import com.example.Task.Management.System.models.recurrence.TaskDuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class TaskRecurrenceCalculatorTest {
    @InjectMocks
    private TaskRecurrenceCalculator calculator;

//    private TaskRecurrence defaultRecurrence;
    private RecurrencePattern defaultPattern;
    private LocalDateTime defaultStartingDate;

    @BeforeEach
    public void init() {
        defaultStartingDate = LocalDateTime.of(2026, Month.APRIL, 15, 0, 0);

        defaultPattern = RecurrencePattern.builder()
                .build();
    }

    @Test
    void repeat_task_every_day(){
        RecurrencePattern nextPattern = defaultPattern.toBuilder()
                .recurrenceType(RecurrenceType.DAILY)
                .build();

        LocalDateTime nextStartDate = calculator.calculateNextStartDate(defaultStartingDate, nextPattern);
        LocalDateTime nextEndDate = calculator.calculateNextEndDate(nextStartDate, nextPattern);

        assertThat(nextStartDate).isEqualTo(LocalDateTime.of(2026, Month.APRIL, 16, 0, 0));
        assertThat(nextEndDate).isEqualTo(LocalDateTime.of(2026, Month.APRIL, 16, 23, 59,59));
    }

    @Test
    void repeat_task_every_day_with_custom_duration(){
        TaskDuration customDuration = TaskDuration.builder()
                .hours(1)
                .minutes(30)
                .build();

        RecurrencePattern nextPattern = defaultPattern.toBuilder()
                .recurrenceType(RecurrenceType.DAILY)
                .taskDuration(customDuration)
                .build();

        LocalDateTime nextStartDate = calculator.calculateNextStartDate(defaultStartingDate, nextPattern);
        LocalDateTime nextEndDate = calculator.calculateNextEndDate(nextStartDate, nextPattern);

        assertThat(nextEndDate).isEqualTo(LocalDateTime.of(2026, Month.APRIL, 16, 1, 30));
    }

    @Test
    void custom_duration_exceeding_defaultEndDate_returns_defaultEndDate(){
        TaskDuration customDuration = TaskDuration.builder()
                .days(2)
                .build();

        RecurrencePattern nextPattern = defaultPattern.toBuilder()
                .recurrenceType(RecurrenceType.DAILY)
                .taskDuration(customDuration)
                .build();

        LocalDateTime nextStartDate = calculator.calculateNextStartDate(defaultStartingDate, nextPattern);
        LocalDateTime nextEndDate = calculator.calculateNextEndDate(nextStartDate, nextPattern);

        assertThat(nextEndDate).isEqualTo(LocalDateTime.of(2026, Month.APRIL, 16, 23, 59, 59));
    }

    @Test
    void repeat_task_every_week(){
        RecurrencePattern nextPattern = defaultPattern.toBuilder()
                .recurrenceType(RecurrenceType.WEEKLY)
                .build();

        LocalDateTime nextStartDate = calculator.calculateNextStartDate(defaultStartingDate, nextPattern);
        LocalDateTime nextEndDate = calculator.calculateNextEndDate(nextStartDate, nextPattern);

        assertThat(nextStartDate).isEqualTo(LocalDateTime.of(2026, Month.APRIL, 22, 0, 0));
        assertThat(nextEndDate).isEqualTo(LocalDateTime.of(2026, Month.APRIL, 28, 23, 59, 59));
    }

    @Test
    void with_recurrence_mon_wed_fri_starting_wednesday_calculate_next_friday(){
        RecurrencePattern nextPattern = defaultPattern.toBuilder()
                .recurrenceType(RecurrenceType.WEEKLY)
                .daysOfWeek(List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))
                .build();

        LocalDateTime nextStartDate = calculator.calculateNextStartDate(defaultStartingDate, nextPattern);
        LocalDateTime nextEndDate = calculator.calculateNextEndDate(nextStartDate, nextPattern);

        assertThat(nextStartDate).isEqualTo(LocalDateTime.of(2026, Month.APRIL, 17, 0, 0));
        assertThat(nextStartDate.getDayOfWeek()).isEqualTo(DayOfWeek.FRIDAY);
        assertThat(nextEndDate).isEqualTo(LocalDateTime.of(2026, Month.APRIL, 19, 23, 59, 59));
    }

    @Test
    void with_recurrence_mon_wed_fri_starting_friday_calculate_next_monday(){
        RecurrencePattern nextPattern = defaultPattern.toBuilder()
                .recurrenceType(RecurrenceType.WEEKLY)
                .daysOfWeek(List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))
                .build();

        LocalDateTime startingDate = defaultStartingDate.withDayOfMonth(17);

        LocalDateTime nextStartDate = calculator.calculateNextStartDate(startingDate, nextPattern);
        LocalDateTime nextEndDate = calculator.calculateNextEndDate(nextStartDate, nextPattern);

        assertThat(nextStartDate).isEqualTo(LocalDateTime.of(2026, Month.APRIL, 20, 0, 0));
        assertThat(nextStartDate.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(nextEndDate).isEqualTo(LocalDateTime.of(2026, Month.APRIL, 21, 23, 59, 59));
    }

    @Test
    void repeat_task_every_month(){
        RecurrencePattern nextPattern = defaultPattern.toBuilder()
                .recurrenceType(RecurrenceType.MONTHLY)
                .build();

        LocalDateTime nextStartDate = calculator.calculateNextStartDate(defaultStartingDate, nextPattern);
        LocalDateTime nextEndDate = calculator.calculateNextEndDate(nextStartDate, nextPattern);

        assertThat(nextStartDate).isEqualTo(LocalDateTime.of(2026, Month.MAY, 15, 0, 0));
        assertThat(nextEndDate).isEqualTo(LocalDateTime.of(2026, Month.JUNE, 14, 23, 59, 59));
    }

    @Test
    void repeat_task_every_12th_day_of_month(){
        RecurrencePattern nextPattern = defaultPattern.toBuilder()
                .recurrenceType(RecurrenceType.MONTHLY)
                .monthDayRule("day_12")
                .build();

        LocalDateTime startingDate = LocalDateTime.of(2026, Month.APRIL, 12, 0, 0);

        LocalDateTime nextStartDate = calculator.calculateNextStartDate(startingDate, nextPattern);
        LocalDateTime nextEndDate = calculator.calculateNextEndDate(nextStartDate, nextPattern);


        assertThat(nextStartDate).isEqualTo(LocalDateTime.of(2026, Month.MAY, 12, 0, 0));
        assertThat(nextEndDate).isEqualTo(LocalDateTime.of(2026, Month.JUNE, 11, 23, 59, 59));
    }

    @Test
    void repeat_task_every_2nd_wednesday_of_month(){
        RecurrencePattern nextPattern = defaultPattern.toBuilder()
                .recurrenceType(RecurrenceType.MONTHLY)
                .monthDayRule("second_wednesday")
                .build();

        LocalDateTime nextStartDate = calculator.calculateNextStartDate(defaultStartingDate, nextPattern);
        LocalDateTime nextEndDate = calculator.calculateNextEndDate(nextStartDate, nextPattern);

        assertThat(nextStartDate).isEqualTo(LocalDateTime.of(2026, Month.MAY, 13, 0, 0));
        assertThat(nextStartDate.getDayOfWeek()).isEqualTo(DayOfWeek.WEDNESDAY);
        assertThat(nextEndDate).isEqualTo(LocalDateTime.of(2026, Month.JUNE, 9, 23, 59, 59));
    }

    @Test
    void repeat_task_every_year(){
        RecurrencePattern nextPattern = defaultPattern.toBuilder()
                .recurrenceType(RecurrenceType.YEARLY)
                .build();

        LocalDateTime nextStartDate = calculator.calculateNextStartDate(defaultStartingDate, nextPattern);
        LocalDateTime nextEndDate = calculator.calculateNextEndDate(nextStartDate, nextPattern);

        assertThat(nextStartDate).isEqualTo(LocalDateTime.of(2027, Month.APRIL, 15, 0, 0));
        assertThat(nextEndDate).isEqualTo(LocalDateTime.of(2028, Month.APRIL, 14, 23, 59, 59));

    }

    @Test
    void repeat_task_every_other_year(){
        RecurrencePattern nextPattern = defaultPattern.toBuilder()
                .recurrenceType(RecurrenceType.YEARLY)
                .interval(2)
                .build();

        LocalDateTime nextStartDate = calculator.calculateNextStartDate(defaultStartingDate, nextPattern);
        LocalDateTime nextEndDate = calculator.calculateNextEndDate(nextStartDate, nextPattern);

        assertThat(nextStartDate).isEqualTo(LocalDateTime.of(2028, Month.APRIL, 15, 0, 0));
        assertThat(nextEndDate).isEqualTo(LocalDateTime.of(2030, Month.APRIL, 14, 23, 59, 59));

    }


}
