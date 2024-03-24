package org.tims.tirechange.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TimeslotFetchResult {
    private List<TireChangeBooking> availableTimeslots;
    private List<String> warnings;
}

