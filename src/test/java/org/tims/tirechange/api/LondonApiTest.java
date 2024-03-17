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
import org.tims.tirechange.model.LondonTireChangeTime;
import org.tims.tirechange.model.TireChangeTimesResponse;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LondonApiTest {

    //    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void testGetAvailableTimes_noTimeSlots() throws IOException {

        // Arrange ***

        // Create a mock TireShopConfigLoader instance
        TireShopConfigLoader mockConfigLoader = mock(TireShopConfigLoader.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        BookingResponseParser mockResponseParser = mock(BookingResponseParser.class); // Mocking BookingResponseParser


        // Create a test TireShopConfig instance
        TireShopConfig testConfig = new TireShopConfig();

        // Configure mock instance
        testConfig.setName("Test Shop");
        testConfig.setAddress("Test Address");
        testConfig.setVehicleTypes(new String[]{"Car"});

        // Create a test ApiConfig instance
        ApiConfig apiConfig = new ApiConfig();

        // Configure  mock instance
        apiConfig.setEndpoint("http://localhost:9003/api/v1/tire-change-times/available"); // Or any URL you like for testing
        apiConfig.setType("xml");

        testConfig.setApi(apiConfig);

        // Configure the mock behavior of mockConfigLoader to return the testConfig when loadConfig is called
        when(mockConfigLoader.loadConfig("src/main/resources/tire_shops.json")).thenReturn(List.of(testConfig));

        // Instantiate and mock RestTemplate
//        RestTemplate mockRestTemplate = mock(RestTemplate.class);

        // Instantiate LondonApi
//        LondonApi londonApi = new LondonApi(mockRestTemplate, mockConfigLoader);


        when(mockRestTemplate.getForObject(Mockito.anyString(), Mockito.eq(TireChangeTimesResponse.class))).thenReturn(new TireChangeTimesResponse());

        // Act ***
//        List<TireChangeBooking> result = londonApi.getAvailableTimes(LocalDate.now(), LocalDate.now().plusDays(1));

        verify(mockRestTemplate, times(1)).getForObject(argThat(url -> url.equals("localhost:9003/api/v1/tire-change-times/available")), eq(TireChangeTimesResponse.class));

        // Assert ***
//        assertThat(result).isEmpty();
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

