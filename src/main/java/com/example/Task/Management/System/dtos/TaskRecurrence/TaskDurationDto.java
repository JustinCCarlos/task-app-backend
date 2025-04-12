package com.example.Task.Management.System.dtos.TaskRecurrence;

import lombok.Builder;

@Builder
public record TaskDurationDto (
     Integer minutes,
     Integer hours,
     Integer days,
     Integer weeks,
     Integer months,
     Integer years
) {


    // Optionally, you could add an "isEmpty" method if you want to keep that logic too.
    public boolean isEmpty() {
        return (minutes == null || minutes == 0) &&
                (hours == null || hours == 0) &&
                (days == null || days == 0) &&
                (weeks == null || weeks == 0) &&
                (months == null || months == 0) &&
                (years == null || years == 0);
    }
}
