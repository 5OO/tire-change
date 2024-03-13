package org.tims.tirechange.api;

import org.tims.tirechange.model.TireChangeBooking;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface TireShopApi {
    List<TireChangeBooking> getAvailableTimes(LocalDate from,
                                              LocalDate until) throws IOException;

    TireChangeBooking bookTimeSlot(String universalId,
                                   String contactInformation);
}
