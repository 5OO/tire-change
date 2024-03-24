package org.tims.tirechange.api;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.tims.tirechange.configuration.ApiConfig;
import org.tims.tirechange.configuration.TireShopConfig;
import org.tims.tirechange.configuration.TireShopConfigLoader;
import org.tims.tirechange.model.ManchesterTireChangeTime;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ManchesterApiTest {

    @Test
    void testGetAvailableTimes_availableTimeSlots() throws IOException {

        // Mock dependencies
        TireShopConfigLoader mockConfigLoader = mock(TireShopConfigLoader.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);

        // Setup mock behavior for configLoader
        TireShopConfig testConfig = new TireShopConfig();
        testConfig.setName("Manchester");
        testConfig.setAddress("14 Bury New Rd, Manchester");
        testConfig.setVehicleTypes(new String[]{"Car", "Truck"});

        // Create a test ApiConfig instance
        ApiConfig apiConfig = new ApiConfig();

        apiConfig.setEndpoint("http://localhost:9004/api/v2/tire-change-times/");
        apiConfig.setType("json");

        testConfig.setApi(apiConfig);

        TireShopConfig dummyConfig = new TireShopConfig(); // Create a dummy config for the first entry
        dummyConfig.setName("Dummy");
        dummyConfig.setAddress("Dummy Address");
        dummyConfig.setVehicleTypes(new String[]{"DummyType"});
        dummyConfig.setApi(new ApiConfig());

        when(mockConfigLoader.loadConfig("src/main/resources/tire_shops.json")).thenReturn(List.of(dummyConfig, testConfig));

        // Define test data
        LocalDate from = LocalDate.of(2024, 3, 20);
        LocalDate until = LocalDate.of(2024, 3, 22);
        String endpoint = "http://localhost:9004/api/v2/tire-change-times";
        String jsonResponse = "[{\"id\":1,\"time\":\"2024-03-20T09:00:00Z\",\"available\":true}, {\"id\":2,\"time\":\"2024-03-20T11:00:00Z\",\"available\":true}]";
        LocalDateTime expectedDateTime1 = LocalDateTime.parse("2024-03-20T09:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime expectedDateTime2 = LocalDateTime.parse("2024-03-20T11:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Setup mock behavior for restTemplate
        when(mockRestTemplate.getForEntity(Mockito.anyString(), eq(String.class))).thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));

        // Instantiate the ManchesterApi with mocked dependencies
        ManchesterApi manchesterApi = new ManchesterApi(mockRestTemplate, mockConfigLoader);

        // Execute the test method
        List<ManchesterTireChangeTime> result = manchesterApi.getAvailableTimes(from, until, endpoint);

        // Verify the results
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getBookingTime()).isEqualTo(expectedDateTime1);
        assertThat(result.get(0).isAvailable()).isTrue();
        assertThat(result.get(1).getId()).isEqualTo(2);
        assertThat(result.get(1).getBookingTime()).isEqualTo(expectedDateTime2);
        assertThat(result.get(1).isAvailable()).isTrue();
    }

    @Test
    void testGetAvailableTimes_noAvailableTimeSlots() throws IOException {

        // Mock dependencies
        TireShopConfigLoader mockConfigLoader = mock(TireShopConfigLoader.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);

        // Setup mock behavior for configLoader
        TireShopConfig testConfig = new TireShopConfig();
        testConfig.setName("Manchester");
        testConfig.setAddress("14 Bury New Rd, Manchester");
        testConfig.setVehicleTypes(new String[]{"Car", "Truck"});

        // Create a test ApiConfig instance
        ApiConfig apiConfig = new ApiConfig();

        apiConfig.setEndpoint("http://localhost:9004/api/v2/tire-change-times/");
        apiConfig.setType("json");

        testConfig.setApi(apiConfig);

        TireShopConfig dummyConfig = new TireShopConfig(); // Create a dummy config for the first entry
        dummyConfig.setName("Dummy");
        dummyConfig.setAddress("Dummy Address");
        dummyConfig.setVehicleTypes(new String[]{"DummyType"});
        dummyConfig.setApi(new ApiConfig());

        when(mockConfigLoader.loadConfig("src/main/resources/tire_shops.json")).thenReturn(List.of(dummyConfig, testConfig));

        // Define test data
        LocalDate from = LocalDate.of(2024, 3, 20);
        LocalDate until = LocalDate.of(2024, 3, 22);
        String endpoint = "http://localhost:9004/api/v2/tire-change-times";
        String jsonResponse = "[]";
        LocalDateTime expectedDateTime1 = LocalDateTime.parse("2024-03-20T09:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime expectedDateTime2 = LocalDateTime.parse("2024-03-20T11:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Setup mock behavior for restTemplate
        when(mockRestTemplate.getForEntity(Mockito.anyString(), eq(String.class))).thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));

        // Instantiate the ManchesterApi with mocked dependencies
        ManchesterApi manchesterApi = new ManchesterApi(mockRestTemplate, mockConfigLoader);

        // Execute the test method
        List<ManchesterTireChangeTime> result = manchesterApi.getAvailableTimes(from, until, endpoint);

        // Verify the results
        assertThat(result).isEmpty();
    }

    @Test
    void testBookTimeSlot_Successful() throws IOException {

        // Mock dependencies
        TireShopConfigLoader mockConfigLoader = mock(TireShopConfigLoader.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);

        // Setup mock behavior for configLoader
        TireShopConfig testConfig = new TireShopConfig();
        testConfig.setName("Manchester");
        testConfig.setAddress("14 Bury New Rd, Manchester");
        testConfig.setVehicleTypes(new String[]{"Car", "Truck"});

        // Create a test ApiConfig instance
        ApiConfig apiConfig = new ApiConfig();

        apiConfig.setEndpoint("http://localhost:9004/api/v2/tire-change-times/");
        apiConfig.setType("json");

        testConfig.setApi(apiConfig);

        TireShopConfig dummyConfig = new TireShopConfig(); // Create a dummy config for the first entry
        dummyConfig.setName("Dummy");
        dummyConfig.setAddress("Dummy Address");
        dummyConfig.setVehicleTypes(new String[]{"DummyType"});
        dummyConfig.setApi(new ApiConfig());

        when(mockConfigLoader.loadConfig("src/main/resources/tire_shops.json")).thenReturn(List.of(dummyConfig, testConfig));

        // Define test data for the request and response
        String universalId = "60";
        String contactInformation = "test@example.com";
        String bookingUrl = testConfig.getApi().getEndpoint() + universalId + "/booking";

        String jsonResponse = "{\n" + "  \"id\": 60,\n" + "  \"time\": \"2024-03-20T09:00:00Z\",\n" + "  \"available\": false\n" + "}";

        // Setup mock behavior for restTemplate
        when(mockRestTemplate.exchange(eq(bookingUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class))).thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));

        // Instantiate ManchesterApi with mocked dependencies
        ManchesterApi manchesterApi = new ManchesterApi(mockRestTemplate, mockConfigLoader);

        // Execute the test method
        ManchesterTireChangeTime result = manchesterApi.bookTimeSlot(universalId, contactInformation);

        // Verify the results
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(60);
        assertThat(result.getBookingTime()).isEqualTo(LocalDateTime.parse("2024-03-20T09:00:00", DateTimeFormatter.ISO_DATE_TIME));
        assertThat(result.isAvailable()).isFalse();
    }

    @Test
    void testBookTimeSlot_Failure() throws IOException {

        // Mock dependencies
        TireShopConfigLoader mockConfigLoader = mock(TireShopConfigLoader.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);

        // Setup mock behavior for configLoader
        TireShopConfig testConfig = new TireShopConfig();
        testConfig.setName("Manchester");
        testConfig.setAddress("14 Bury New Rd, Manchester");
        testConfig.setVehicleTypes(new String[]{"Car", "Truck"});

        // Create a test ApiConfig instance
        ApiConfig apiConfig = new ApiConfig();

        apiConfig.setEndpoint("http://localhost:9004/api/v2/tire-change-times/");
        apiConfig.setType("json");

        testConfig.setApi(apiConfig);

        TireShopConfig dummyConfig = new TireShopConfig(); // Create a dummy config for the first entry
        dummyConfig.setName("Dummy");
        dummyConfig.setAddress("Dummy Address");
        dummyConfig.setVehicleTypes(new String[]{"DummyType"});
        dummyConfig.setApi(new ApiConfig());

        when(mockConfigLoader.loadConfig("src/main/resources/tire_shops.json")).thenReturn(List.of(dummyConfig, testConfig));

        // Define test data for the request and response
        String universalId = "60";
        String contactInformation = "test@example.com"; // This variable should be recognized within your test method.
        String bookingUrl = testConfig.getApi().getEndpoint() + universalId + "/booking";

        String jsonResponse = "{\n" + "  \"id\": 60,\n" + "  \"time\": \"2024-03-20T09:00:00Z\",\n" + "  \"available\": false\n" + "}";


        // Simulate an error response from the API
        when(mockRestTemplate.exchange(eq(bookingUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class))).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        // Instantiate ManchesterApi with mocked dependencies
        ManchesterApi manchesterApi = new ManchesterApi(mockRestTemplate, mockConfigLoader);

        // Execute the test method and expect an exception
        assertThrows(RuntimeException.class, () -> {
            manchesterApi.bookTimeSlot(universalId, contactInformation);
        }, "Expected a RuntimeException to be thrown due to API error");
    }

}
