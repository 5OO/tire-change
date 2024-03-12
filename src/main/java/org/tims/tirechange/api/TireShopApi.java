package org.tims.tirechange.api;

import org.tims.tirechange.model.TireChangeBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface TireShopApi {
    List<TireChangeBooking> getAvailableTimes(LocalDateTime from,
                                              LocalDateTime until);

    TireChangeBooking bookTimeSlot(String universalId,
                                   String contactInformation);
}
