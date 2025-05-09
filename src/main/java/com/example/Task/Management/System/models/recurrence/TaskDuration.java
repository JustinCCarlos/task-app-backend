package com.example.Task.Management.System.models.recurrence;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskDuration {
    private Integer minutes;
    private Integer hours;
    private Integer days;
    private Integer weeks;
    private Integer months;
    private Integer years;

    public boolean isEmpty() {
        return (minutes == null || minutes == 0) &&
                (hours == null || hours == 0) &&
                (days == null || days == 0) &&
                (weeks == null || weeks == 0) &&
                (months == null || months == 0) &&
                (years == null || years == 0);
    }
}
