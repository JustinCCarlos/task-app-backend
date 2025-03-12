package com.example.Task.Management.System.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;


// TODO:: add isActive

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    @Setter
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;

    @Setter
    @Builder.Default
    private boolean completed = false;

    @Setter
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @Setter
    @Column(nullable = true)
    @Builder.Default
    @Min(value = 1, message = "Priority must be at least 1") //lowest priority
    @Max(value = 5, message = "Priority must be at most 5") //highest priority
    private Integer priority = 3;

    @Setter
    @Column(nullable = false)
    @NotNull(message  = "Start date is required")
    private LocalDateTime startDate;

    @Setter
    @Column(nullable = true)
    private LocalDateTime endDate;

    @Setter
    @Column(nullable = true)
    private LocalDateTime finishedDate;

    @Setter
    @Builder.Default
    private boolean overdue = false;

}
