package org.tims.tirechange.configuration;

import org.tims.tirechange.model.LondonTireChangeTime;

public interface BookingResponseParser {
    LondonTireChangeTime parseBookingResponseXML(String xmlResponse);

}
