package org.tims.tirechange.api;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.tims.tirechange.configuration.TireShopConfig;
import org.tims.tirechange.configuration.TireShopConfigLoader;
import org.tims.tirechange.exception.NoAvailableTimeslotsException;
import org.tims.tirechange.model.TireChangeBooking;
import org.tims.tirechange.model.TireChangeTime;
import org.tims.tirechange.model.TireChangeTimesResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
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
        String url = endpoint + "available?from=" + from + "&until=" + until;

        // 2. Make an HTTP GET request to the London API
        String xmlResponse = restTemplate.getForObject(url, String.class);

        // Parse XML
        XmlMapper xmlMapper = new XmlMapper();
        TireChangeTimesResponse response = xmlMapper.readValue(xmlResponse, TireChangeTimesResponse.class);

        List<TireChangeBooking> listAvailableTimes =  new ArrayList<>();

        // Map to TireChangeBooking
        List<TireChangeTime> availableTimes = response.getAvailableTimes();
        if (availableTimes == null) {
            String errorMessage = String.format("No available timeslots found for the given date range between %s and %s.", from.toString(), until.toString());
            throw new NoAvailableTimeslotsException(errorMessage);
        } else {
            listAvailableTimes = response.getAvailableTimes().stream()
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
            }
        return listAvailableTimes;
    }

    @Override
    public TireChangeTime bookTimeSlot(String universalId, String contactInformation) throws IOException {
        // 1. Construct PUT request URL
        TireShopConfig config = configLoader.loadConfig("src/main/resources/tire_shops.json").get(0);
        String bookingEndpoint = config.getApi().getEndpoint() + universalId + "/booking";

        // 2. Prepare XML request body
        String requestBody = createBookingRequestXML(contactInformation);

        // 3. Execute PUT request with RestTemplate (headers for XML)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(bookingEndpoint, HttpMethod.PUT, entity, String.class);

        // 4. Handle Response
        if (response.getStatusCode() == HttpStatus.OK) {
            // Parse response XML into TireChangeBooking
            TireChangeTime bookingResponse = parseBookingResponseXML(response.getBody());
            return bookingResponse;
        } else {
            // Throw appropriate exception based on error code
            throw new RuntimeException("Booking failed - London API error");
        }
    }

    private String createBookingRequestXML(String clientContactInformation) {
        return String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<london.tireChangeBookingRequest>\n" +
                "   <contactInformation> - %s</contactInformation>\n" +
                "</london.tireChangeBookingRequest>", clientContactInformation);
    }

    private TireChangeTime parseBookingResponseXML(String xmlResponse) {
        try {
            XmlMapper xmlMapper = new XmlMapper(); // From com.fasterxml.jackson.dataformat.xml
            TireChangeTime responseObject = xmlMapper.readValue(xmlResponse, TireChangeTime.class);
            return responseObject;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing booking response XML", e);
        }
    }


}
