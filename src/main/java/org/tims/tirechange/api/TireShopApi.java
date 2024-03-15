package org.tims.tirechange.api;

import org.tims.tirechange.model.TireChangeBooking;
import org.tims.tirechange.model.TireChangeTime;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface TireShopApi {
    List<TireChangeBooking> getAvailableTimes(LocalDate from,
                                              LocalDate until) throws IOException;

    TireChangeTime bookTimeSlot(String universalId,
                                String contactInformation) throws IOException;
}
