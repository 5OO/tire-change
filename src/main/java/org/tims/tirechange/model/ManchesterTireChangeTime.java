package org.tims.tirechange.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ManchesterTireChangeTime {

    @JsonProperty("id") // Match JSON field exactly
    private Integer id;

    @JsonProperty("time")
    private LocalDateTime bookingTime;

    @JsonProperty("available")
    private boolean available;
}
