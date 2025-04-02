package com.example.Task.Management.System.models.Recurrence;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecurrencePattern {
    private List<java.time.DayOfWeek> daysOfWeek; //
    private String monthDayRule; //Ex. "first_monday" "day_15"

}
