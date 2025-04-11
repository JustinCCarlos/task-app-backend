package com.example.Task.Management.System.models.recurrence;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.DayOfWeek;
import java.util.List;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recurrence_pattern")
public class RecurrencePattern {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recurrence_pattern_id")
    private Long recurrencePatternId;

    @NotNull(message = "Interval is required")
    @Min(value = 1, message = "Interval must be at least 1")
    @Max(value = 99, message = "Interval cannot exceed 99")
    @Builder.Default
    private int interval = 1;

    @NotNull(message = "Recurrence type is required")
    @Column(name = "recurrence_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecurrenceType recurrenceType;

    @Embedded
    @Column(name = "task_duration")
    private TaskDuration taskDuration;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recurrence_days_of_week", joinColumns = @JoinColumn(name = "recurrence_pattern_id"))
    @Enumerated(EnumType.STRING)
    private List<DayOfWeek> daysOfWeek;

    @Column(name = "month_day_rule")
    private String monthDayRule; //Ex. "first_monday" "day_15"

}
//    public void setDaysOfWeek(List<String> daysOfWeek){
//        this.daysOfWeek = daysOfWeek.stream()
//                .map(day -> {
//                    try {
//                        return DayOfWeek.valueOf(day.toUpperCase());
//                    } catch (IllegalArgumentException e) {
//                        throw new IllegalArgumentException("Invalid day of week: " + day);
//                    }
//                })
//                .collect(Collectors.toList());
//
//        Collections.sort(this.daysOfWeek);
//    }

