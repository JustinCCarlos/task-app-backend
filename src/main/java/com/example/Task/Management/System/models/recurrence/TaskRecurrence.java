package com.example.Task.Management.System.models.recurrence;

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

    @OneToMany(mappedBy = "taskRecurrence", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<Task> generatedTasks;

    @NotNull
    @Column(name = "recurrence_start_date", nullable = false)
    private LocalDateTime recurrenceStartDate;

    @Column(name = "end_recurrence_date")
    private LocalDateTime recurrenceEndDate;

    @Min(value = 1, message = "Max occurrences must be at least 1")
    @Column(name = "max_occurrences")
    private Integer maxOccurrences;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "recurrence_pattern_id")
    private RecurrencePattern recurrencePattern; //For storing recurrence patterns used for creation of next task

    @AssertTrue(message = "Either endDate or maxOccurrences should be set, but not both")
    private boolean isEndDateOrMaxOccurrencesValid() {
        return (recurrenceEndDate != null) ^ (maxOccurrences != null); // XOR ensures only one is set
    }

    // data moved to a separate entity "RecurrencePattern"

    //    @NotNull(message = "Recurrence type is required")
    //    @Column(name = "recurrence_type", nullable = false)
    //    @Enumerated(EnumType.STRING)
    //    private RecurrenceType recurrenceType;

    //    @Min(value = 1, message = "Interval must be at least 1")
    //    @Max(value = 99, message = "Interval cannot exceed 99")
    //    @Column(name = "interval", nullable = false)
    //    private int interval;
}
