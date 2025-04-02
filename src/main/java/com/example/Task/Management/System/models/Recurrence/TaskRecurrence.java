package com.example.Task.Management.System.models.Recurrence;

import com.example.Task.Management.System.models.Task;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Table(name ="task_recurrence")
public class TaskRecurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_recurrence_id")
    private Long taskRecurrenceId;

    @OneToMany(mappedBy = "taskRecurrence", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> generatedTasks;

    @NotNull(message = "Recurrence type is required")
    @Column(name = "recurrence_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecurrenceType recurrenceType;

    @Min(value = 1, message = "Interval must be at least 1")
    @Max(value = 99, message = "Interval cannot exceed 99")
    @Column(name = "interval", nullable = false)
    private int interval;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Min(value = 1, message = "Max occurrences must be at least 1")
    @Column(name = "max_occurrences")
    private Integer maxOccurrences;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Convert(converter = RecurrencePatternConverter.class)
    @Column(name = "recurrence_pattern", columnDefinition = "JSON")
    private RecurrencePattern recurrencePattern; //For storing custom recurrence patterns

    @AssertTrue(message = "Either endDate or maxOccurrences should be set, but not both")
    private boolean isEndDateOrMaxOccurrencesValid() {
        return (endDate != null) ^ (maxOccurrences != null); // XOR ensures only one is set
    }

}
