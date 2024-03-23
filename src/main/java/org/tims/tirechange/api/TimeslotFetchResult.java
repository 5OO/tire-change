package org.tims.tirechange.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.tims.tirechange.model.TireChangeBooking;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TimeslotFetchResult {
    private List<TireChangeBooking> availableTimeslots;
    private List<String> warnings;
}

