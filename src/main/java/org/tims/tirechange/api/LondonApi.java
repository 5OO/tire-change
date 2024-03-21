package org.tims.tirechange.api;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.tims.tirechange.configuration.BookingResponseParser;
import org.tims.tirechange.configuration.TireShopConfig;
import org.tims.tirechange.configuration.TireShopConfigLoader;
import org.tims.tirechange.exception.NoAvailableTimeslotsException;
import org.tims.tirechange.model.LondonTireChangeTime;
import org.tims.tirechange.model.TireChangeBooking;
import org.tims.tirechange.model.TireChangeTimesResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component // Mark this as a Spring-managed bean
public class LondonApi implements TireShopApi {

    private static final Logger logger = LoggerFactory.getLogger(LondonApi.class);
    final private BookingResponseParser responseParser;
    final private RestTemplate restTemplate;
    final private TireShopConfigLoader configLoader;

    public List<TireChangeBooking> getAvailableTimes(LocalDate from, LocalDate until, String endpoint) throws IOException {
        // 1. Build the API request URL (use the configuration data)
        TireShopConfig config = configLoader.loadConfig("src/main/resources/tire_shops.json").get(0); // Load and retrieve config

        // Construct the request URL
        String url = endpoint + "available?from=" + from + "&until=" + until;
        logger.info("url for 'get' created ...");

        // 2. Make an HTTP GET request to the London API
        String xmlResponse = restTemplate.getForObject(url, String.class);
        logger.info("GET request completed ...");

        // Parse XML
        XmlMapper xmlMapper = new XmlMapper();
        TireChangeTimesResponse response = xmlMapper.readValue(xmlResponse, TireChangeTimesResponse.class);
        logger.info("XML parsing completed ...");

        // Map to TireChangeBooking
        List<LondonTireChangeTime> availableTimes = response.getAvailableTimes();

        if (availableTimes == null || availableTimes.isEmpty()) {
            String errorMessage = String.format("No available timeslots found for the given date range between %s and %s.", from.toString(), until.toString());
            throw new NoAvailableTimeslotsException(errorMessage);
        } else {
            return response.getAvailableTimes().stream()
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
    }

    @Override
    public LondonTireChangeTime bookTimeSlot(String universalId, String contactInformation) throws IOException {
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

        logger.info("London API XML PUT execution done ... ");

        // 4. Handle Response
        if (response.getStatusCode() == HttpStatus.OK) {
            // Parse response XML into TireChangeBooking
            LondonTireChangeTime bookingResponse = responseParser.parseBookingResponseXML(response.getBody());
            logger.info(" status code is 200 OK, parsing XML done ");
            return bookingResponse;
        } else {
            logger.info("PUT failed, throwing an error message ");
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
}
