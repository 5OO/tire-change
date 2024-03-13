package org.tims.tirechange.model;

import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class TireChangeBooking {
    private String universalId; // booking identifier for the  in London API 1 (UUID) or available tire change time (ID) in Manchester API 2
    private LocalDateTime bookingTime; // selected booking Date and time of the tire change
    private boolean available; // Indicates if the time slot is available for booking API 2 Manchester ws
    private String vehicleType; // Type of vehicle that is serviced - need to display in UI
    private String apiIdentifier; // to indicate which API a specific booking is associated with
    private String tireShopName; // Name of the tire workshop - need to display in UI
    private String tireShopAddress; // Address of the tire workshop - need to display in UI
    private String clientContactInformation; // optional contact information of the client booking for the time slot (incl. within the Request body)
}
