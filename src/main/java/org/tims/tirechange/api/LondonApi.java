package org.tims.tirechange.api;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.tims.tirechange.configuration.TireShopConfig;
import org.tims.tirechange.configuration.TireShopConfigLoader;
import org.tims.tirechange.model.TireChangeBooking;
import org.tims.tirechange.model.TireChangeTime;
import org.tims.tirechange.model.TireChangeTimesResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component // Mark this as a Spring-managed bean
public class LondonApi implements TireShopApi {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TireShopConfigLoader configLoader;

    public List<TireChangeBooking> getAvailableTimes(LocalDateTime from, LocalDateTime until) throws IOException {
        // TODO: Implement logic to:
        // 1. Build the API request URL (use the configuration data)
        TireShopConfig config = configLoader.loadConfig("tire_shops.json").get(0); // Load and retrieve config
        String endpoint = config.getApi().getEndpoint();

        // Construct the request URL
        String url = endpoint + "?from=" + from + "&until=" + until;

        // 2. Make an HTTP GET request to the London API
        String xmlResponse = restTemplate.getForObject(url, String.class);

        XmlMapper xmlMapper = new XmlMapper();
        TireChangeTimesResponse response = xmlMapper.readValue(xmlResponse, TireChangeTimesResponse.class);

// Access available times:
        List<TireChangeTime> availableTimes = response.getAvailableTimes();
        for (TireChangeTime time : availableTimes) {
            System.out.println("UUID: " + time.getUuid());
            System.out.println("Time: " + time.getTime());



// TODO: Parse the XML response into TireChangeBooking objects

// ... return the list of parsed bookings
//        return bookings;
            // 3. Parse the XML response
            // 4. Convert the XML data into a list of TireChangeBooking objects

        }
        return null;
    }
    @Override
    public TireChangeBooking bookTimeSlot(String universalId, String contactInformation){
        // TODO: Similar logic as above, but for the booking PUT request
        return null;
    }
}
