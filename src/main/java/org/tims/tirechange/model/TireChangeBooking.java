package org.tims.tirechange.model;

import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class TireChangeBooking {
    private String universalId; // booking identifier for the  in London API (UUID) or available tire change time ID in Manchester
    private LocalDateTime bookingTime; // selected booking Date and time of the tire change
    private boolean available; // Indicates if the time slot is available for booking
    private String vehicleType; // Type of vehicle that is serviced
    private String apiIdentifier; // to indicate which API a specific booking is associated with
    private String tireShopName; // Name of the tire workshop
    private String tireShopAddress; // Address of the tire workshop
    private String clientContactInformation; // Contact information of the client booking for the time slot
    private LocalDateTime dateFrom; // API1 London time slots from
    private LocalDateTime dateUntil; // API1 London time slots until
}
