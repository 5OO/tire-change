package org.tims.tirechange.configuration;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;
import org.tims.tirechange.model.LondonTireChangeTime;

@Component
public class BookingResponseParserImpl implements BookingResponseParser {

    @Override
    public LondonTireChangeTime parseBookingResponseXML(String xmlResponse) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.readValue(xmlResponse, LondonTireChangeTime.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing booking response XML", e);
        }
    }
}
