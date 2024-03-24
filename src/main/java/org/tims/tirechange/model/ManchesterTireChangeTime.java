package org.tims.tirechange.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ManchesterTireChangeTime {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime bookingTime;

    @JsonProperty("available")
    private boolean available;
}
