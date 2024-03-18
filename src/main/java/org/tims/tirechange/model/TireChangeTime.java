package org.tims.tirechange.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TireChangeTime {

    private String universalId;
    private LocalDate bookingTime;
}
