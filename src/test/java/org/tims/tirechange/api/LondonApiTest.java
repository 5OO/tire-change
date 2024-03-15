package org.tims.tirechange.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.tims.tirechange.configuration.ApiConfig;
import org.tims.tirechange.configuration.TireShopConfig;
import org.tims.tirechange.configuration.TireShopConfigLoader;
import org.tims.tirechange.model.TireChangeBooking;
import org.tims.tirechange.model.TireChangeTimesResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LondonApiTest {
    @Test
    void testGetAvailableTimes_noTimeSlots() throws IOException {

        // Arrange ***

        // Create a mock TireShopConfigLoader instance
        TireShopConfigLoader mockConfigLoader = mock(TireShopConfigLoader.class);

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
        RestTemplate mockRestTemplate = mock(RestTemplate.class);

        // Instantiate LondonApi
        LondonApi londonApi = new LondonApi(mockRestTemplate, mockConfigLoader);


        when(mockRestTemplate.getForObject(Mockito.anyString(), Mockito.eq(TireChangeTimesResponse.class))).thenReturn(new TireChangeTimesResponse());

        // Act ***
        List<TireChangeBooking> result = londonApi.getAvailableTimes(LocalDate.now(), LocalDate.now().plusDays(1));

        verify(mockRestTemplate, times(1)).getForObject(argThat(url -> url.equals("localhost:9003/api/v1/tire-change-times/available")), eq(TireChangeTimesResponse.class));

        // Assert ***
        assertThat(result).isEmpty();
    }
}

