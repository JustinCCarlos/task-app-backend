package com.example.Task.Management.System.models;

import com.example.Task.Management.System.models.Recurrence.TaskRecurrence;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;


// TODO:: add isActive

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;

    @Builder.Default
    private boolean completed = false;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @Column(nullable = true)
    @Builder.Default
    @Min(value = 1, message = "Priority must be at least 1") //lowest priority
    @Max(value = 5, message = "Priority must be at most 5") //highest priority
    private Integer priority = 3;

    @Column(nullable = false)
    @NotNull(message  = "Start date is required")
    private LocalDateTime startDate;

    @Column(nullable = true)
    private LocalDateTime endDate;

    @Column(nullable = true)
    private LocalDateTime finishedDate;

    @Builder.Default
    private boolean overdue = false;

    @ManyToOne
    @JoinColumn(name = "task_recurrence_id")
    private TaskRecurrence taskRecurrence;

}
