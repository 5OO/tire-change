package org.tims.tirechange.api;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.tims.tirechange.configuration.TireShopConfig;
import org.tims.tirechange.configuration.TireShopConfigLoader;
import org.tims.tirechange.model.TireChangeBooking;
import org.tims.tirechange.model.TireChangeTimesResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component // Mark this as a Spring-managed bean
public class LondonApi implements TireShopApi {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TireShopConfigLoader configLoader;

    public List<TireChangeBooking> getAvailableTimes(LocalDate from, LocalDate until) throws IOException {
        // 1. Build the API request URL (use the configuration data)
        TireShopConfig config = configLoader.loadConfig("src/main/resources/tire_shops.json").get(0); // Load and retrieve config
        String endpoint = config.getApi().getEndpoint();

        // Construct the request URL
        String url = endpoint + "?from=" + from + "&until=" + until;

        // 2. Make an HTTP GET request to the London API
        String xmlResponse = restTemplate.getForObject(url, String.class);

        // Parse XML
        XmlMapper xmlMapper = new XmlMapper();
        TireChangeTimesResponse response = xmlMapper.readValue(xmlResponse, TireChangeTimesResponse.class);


        // Map to TireChangeBooking
        List<TireChangeBooking> availableTimes = response.getAvailableTimes().stream()
                .map(time -> {
                    TireChangeBooking availableTime = new TireChangeBooking();
                    availableTime.setUniversalId(time.getUuid());
                    availableTime.setBookingTime(java.time.OffsetDateTime.parse(time.getTime()).toLocalDateTime());
                    availableTime.setAvailable(true);
                    availableTime.setVehicleType(Arrays.toString(config.getVehicleTypes()));
                    availableTime.setTireShopName(config.getName());
                    availableTime.setTireShopAddress(config.getAddress());
                    return availableTime;
                })
                .collect(Collectors.toList());

        return availableTimes;
    }

    @Override
    public TireChangeBooking bookTimeSlot(String universalId, String contactInformation) {
        // TODO: Similar logic as above, but for the booking PUT request
        return null;
    }
}
