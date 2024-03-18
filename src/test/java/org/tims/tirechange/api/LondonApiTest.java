package org.tims.tirechange.api;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.tims.tirechange.configuration.ApiConfig;
import org.tims.tirechange.configuration.BookingResponseParser;
import org.tims.tirechange.configuration.TireShopConfig;
import org.tims.tirechange.configuration.TireShopConfigLoader;
import org.tims.tirechange.exception.NoAvailableTimeslotsException;
import org.tims.tirechange.model.LondonTireChangeTime;
import org.tims.tirechange.model.TireChangeBooking;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LondonApiTest {
    @Test
    void testGetAvailableTimes_availableTimeSlots() throws IOException {

        // Arrange
        TireShopConfigLoader mockConfigLoader = mock(TireShopConfigLoader.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        BookingResponseParser mockResponseParser = mock(BookingResponseParser.class);

        // Create a test TireShopConfig instance
        TireShopConfig testConfig = new TireShopConfig();

        // Configure mock instance
        testConfig.setName("London");
        testConfig.setAddress("1A Gunton Rd, London");
        testConfig.setVehicleTypes(new String[]{"Car"});

        // Create a test ApiConfig instance
        ApiConfig apiConfig = new ApiConfig();

        apiConfig.setEndpoint("http://localhost:9003/api/v1/tire-change-times/available");
        apiConfig.setType("xml");

        testConfig.setApi(apiConfig);

        // Configure the mock behavior of mockConfigLoader to return the testConfig when loadConfig is called
        when(mockConfigLoader.loadConfig("src/main/resources/tire_shops.json")).thenReturn(List.of(testConfig));

        // Injecting mock into LondonApi
        LondonApi londonApi = new LondonApi(mockResponseParser, mockRestTemplate, mockConfigLoader);

        String testXMLresponse = "<tireChangeTimesResponse>\n" +
                "  <availableTime>\n" +
                "    <uuid>607e3dfb-daac-4a1c-a1d3-32104911b24a</uuid>\n" +
                "    <time>2024-04-15T05:00:00Z</time>\n" +
                "  </availableTime>\n" +
                "  <availableTime>\n" +
                "    <uuid>a189688e-3863-4e73-8408-fcc96b5477fe</uuid>\n" +
                "    <time>2024-04-15T06:00:00Z</time>\n" +
                "  </availableTime>\n" +
                "  <availableTime>\n" +
                "    <uuid>465362c8-6427-4c2b-8939-38900e42e5e2</uuid>\n" +
                "    <time>2024-04-15T13:00:00Z</time>\n" +
                "  </availableTime>\n" +
                "</tireChangeTimesResponse>";
        when(mockRestTemplate.getForObject(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(testXMLresponse);

        LocalDate fromDate = LocalDate.now();
        LocalDate untilDate = LocalDate.now().plusDays(1);

        // Act
        List<TireChangeBooking> result = londonApi.getAvailableTimes(fromDate, untilDate);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(3); // Expecting 3 time slots based on the provided XML
        assertThat(result.get(0).getUniversalId()).isEqualTo("607e3dfb-daac-4a1c-a1d3-32104911b24a");
        assertThat(result.get(0).getBookingTime()).isEqualTo(java.time.OffsetDateTime.parse("2024-04-15T05:00:00Z").toLocalDateTime());
        assertThat(result.get(0).getUniversalId()).isEqualTo("607e3dfb-daac-4a1c-a1d3-32104911b24a");
        assertThat(result.get(1).getUniversalId()).isEqualTo("a189688e-3863-4e73-8408-fcc96b5477fe");
        assertThat(result.get(1).getBookingTime()).isEqualTo(java.time.OffsetDateTime.parse("2024-04-15T06:00:00Z").toLocalDateTime());
    }

    @Test
    void testGetAvailableTimes_noTimeSlots() throws IOException {

        // Arrange
        TireShopConfigLoader mockConfigLoader = mock(TireShopConfigLoader.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        BookingResponseParser mockResponseParser = mock(BookingResponseParser.class);

        // Create a test TireShopConfig instance
        TireShopConfig testConfig = new TireShopConfig();

        // Configure mock instance
        testConfig.setName("London");
        testConfig.setAddress("1A Gunton Rd, London");
        testConfig.setVehicleTypes(new String[]{"Car"});

        // Create a test ApiConfig instance
        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setEndpoint("http://localhost:9003/api/v1/tire-change-times/available");
        apiConfig.setType("xml");
        testConfig.setApi(apiConfig);

        // Configure the mock behavior of mockConfigLoader to return the testConfig when loadConfig is called
        when(mockConfigLoader.loadConfig("src/main/resources/tire_shops.json")).thenReturn(List.of(testConfig));

        // Simulate an empty response scenario
        String emptyXmlResponse = "<tireChangeTimesResponse></tireChangeTimesResponse>";
        when(mockRestTemplate.getForObject(Mockito.anyString(), Mockito.eq(String.class))).thenReturn(emptyXmlResponse);

        // Injecting mock into LondonApi
        LondonApi londonApi = new LondonApi(mockResponseParser, mockRestTemplate, mockConfigLoader);

        // Act & Assert
        NoAvailableTimeslotsException thrown = assertThrows(
                NoAvailableTimeslotsException.class,
                () -> londonApi.getAvailableTimes(LocalDate.now(), LocalDate.now().plusDays(1)),
                "Expected getAvailableTimes to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("No available timeslots found for the given date range"));
    }

    @Test
    void testBookTimeSlot_Successful() throws Exception {

        //Arrange
        BookingResponseParser mockResponseParser = mock(BookingResponseParser.class);
        TireShopConfigLoader mockConfigLoader = mock(TireShopConfigLoader.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);

        TireShopConfig testConfig = new TireShopConfig();
        testConfig.setName("London");
        testConfig.setAddress("1A Gunton Rd, London");
        testConfig.setVehicleTypes(new String[]{"Car"});

        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setEndpoint("http://localhost:9003/api/v1/tire-change-times/");
        apiConfig.setType("xml");
        testConfig.setApi(apiConfig);

        when(mockConfigLoader.loadConfig("src/main/resources/tire_shops.json")).thenReturn(List.of(testConfig));

        String bookingResponseXml = "<tireChangeBookingResponse>\n" +
                "  <uuid>950b1c72-e319-4cfa-88ad-23b74b5851be</uuid>\n" +
                "  <time>2024-03-11T13:00:00Z</time>\n" +
                "</tireChangeBookingResponse>";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(bookingResponseXml, HttpStatus.OK);

        when(mockRestTemplate.exchange(
                eq("http://localhost:9003/api/v1/tire-change-times/950b1c72-e319-4cfa-88ad-23b74b5851be/booking"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        LondonTireChangeTime expectedREsponse = new LondonTireChangeTime();

        // Mocking the parser behavior

        when(mockResponseParser.parseBookingResponseXML(bookingResponseXml)).thenReturn(expectedREsponse);

        LondonApi londonApi = new LondonApi(mockResponseParser, mockRestTemplate, mockConfigLoader);

        //Act

        LondonTireChangeTime bookingResponse = londonApi.bookTimeSlot("950b1c72-e319-4cfa-88ad-23b74b5851be", "contact-info");

        assertNotNull(bookingResponse);
    }

    @Test
    void testBookTimeSlot_Failure() throws Exception {
        //Arrange
        TireShopConfigLoader mockConfigLoader = mock(TireShopConfigLoader.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        BookingResponseParser mockResponseParser = mock(BookingResponseParser.class);

        TireShopConfig testConfig = new TireShopConfig();

        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setEndpoint("http://localhost:9003/api/v1/tire-change-times/");
        apiConfig.setType("xml");
        testConfig.setApi(apiConfig);

        // Simulate a failure scenario with HttpStatus.INTERNAL_SERVER_ERROR

        ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
        when(mockConfigLoader.loadConfig("src/main/resources/tire_shops.json")).thenReturn(List.of(testConfig));

        when(mockRestTemplate.exchange(
                eq("http://localhost:9003/api/v1/tire-change-times/950b1c72-e319-4cfa-88ad-23b74b5851be/booking"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        LondonApi londonApi = new LondonApi(mockResponseParser, mockRestTemplate, mockConfigLoader);

        // Act & Assert
        Exception exception = assertThrows(
                RuntimeException.class,
                () -> londonApi.bookTimeSlot("950b1c72-e319-4cfa-88ad-23b74b5851be", "contact-info")
        );

        // Verify the correct failure message
        assertEquals("Booking failed - London API error", exception.getMessage());
    }
}

