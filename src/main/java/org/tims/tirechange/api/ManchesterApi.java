package org.tims.tirechange.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.tims.tirechange.configuration.TireShopConfig;
import org.tims.tirechange.configuration.TireShopConfigLoader;
import org.tims.tirechange.model.ManchesterTireChangeTime;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ManchesterApi {

    private static final Logger logger = LoggerFactory.getLogger(ManchesterApi.class);
    private final RestTemplate restTemplate;
    private final TireShopConfigLoader configLoader;

    public List<ManchesterTireChangeTime> getAvailableTimes(LocalDate from, LocalDate until, String endpoint) throws IOException {
        // 1. Build request URL
        TireShopConfig config = configLoader.loadConfig("src/main/resources/tire_shops.json").get(1); // Assuming Manchester is at index 1
        // fill in needed range of timeslots depending on time span
        Integer amountOfNeededTimeSlots;
        if (from != null || until != null) {
            amountOfNeededTimeSlots = Period.between(from, until).getDays() * 9;
        } else {
            throw new IllegalArgumentException("The dates 'from' and 'until' cannot be null");
        }

        // Construct the request URL with parameters
        String url = endpoint + "?amount=" + amountOfNeededTimeSlots + "&page=0&from=" + from.format(DateTimeFormatter.ISO_DATE);
        logger.info("{} url for 'get' created ...", url);

        // 2. Make an HTTP GET request to the Manchester API
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        logger.info("GET request completed ...");

        // 3. Parse JSON using Jackson's ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Registers support for Java 8 Time API
        List<ManchesterTireChangeTime> tireChangeTimes = Arrays.asList(mapper.readValue(response.getBody(), ManchesterTireChangeTime[].class));
        logger.info("JSON parsing completed ...");

        return tireChangeTimes;
    }

    public ManchesterTireChangeTime bookTimeSlot(String universalId, String contactInformation) throws IOException {
        // 1. Build the API request URL
        TireShopConfig config = configLoader.loadConfig("src/main/resources/tire_shops.json").get(1);
        String endpoint = config.getApi().getEndpoint();
        String bookingUrl = endpoint + universalId + "/booking";

        logger.info("1 - final booking URL: {}", bookingUrl);

        // 2. Prepare JSON request body
        String requestBody = createBookingRequestJSON(contactInformation);

        // 3. Execute PUT request with RestTemplate
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(bookingUrl, HttpMethod.POST, entity, String.class);
        logger.info(" Manchester JSON - PUT execution done ... ");

        // 4. Handle Response
        if (response.getStatusCode() == HttpStatus.OK) {
            logger.info(" status code is 200 OK, preparing for parsing ");
            // Parse response JSON into ManchesterTireChangeTime
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // enable Java 8 time support
            return mapper.readValue(response.getBody(), ManchesterTireChangeTime.class);
        } else {
            logger.info("PUT failed, throwing an error message ");
            // Throw appropriate exception based on error code
            throw new RuntimeException("Booking failed - Manchester API error");
        }

    }

    private String createBookingRequestJSON(String clientContactInformation) {
        return String.format("{\"contactInformation\": \"%s\"}", clientContactInformation);
    }
}
