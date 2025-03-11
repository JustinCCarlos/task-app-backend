package com.example.Task.Management.System.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDto {
    private Long task_id;

    @NotBlank(message = "Title cannot be empty")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;

    private boolean completed;
    private Long category_id;

    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 5, message = "Priority must be at most 5")
    @Builder.Default
    private Integer priority = 3;
    //use if needed to specify date format
    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") where Z represents UTC
    private LocalDateTime deadline;
    private boolean overdue;

    //removes null value to allow default to work
    public static class TaskDtoBuilder{
        public TaskDtoBuilder priority(Integer priority){
            this.priority$value = priority;
            this.priority$set = priority != null;
            return this;
        }
    }
}
