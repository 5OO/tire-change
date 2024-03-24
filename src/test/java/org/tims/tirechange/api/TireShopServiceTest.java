package org.tims.tirechange.api;

import org.junit.jupiter.api.Test;
import org.tims.tirechange.configuration.TireShopConfig;
import org.tims.tirechange.model.ManchesterTireChangeTime;
import org.tims.tirechange.model.TireChangeBooking;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TireShopServiceTest {

    private final TireShopService tireShopService = new TireShopService();

    @Test
    void mapManchesterToTireChangeBooking_correctlyMapsFields() {
        // Arrange
        ManchesterTireChangeTime manchesterTime = new ManchesterTireChangeTime();
        manchesterTime.setId(1);
        manchesterTime.setBookingTime(LocalDateTime.of(2024, 3, 20, 9, 0));
        manchesterTime.setAvailable(true);

        TireShopConfig shopConfig = new TireShopConfig();
        shopConfig.setName("Manchester Tire Shop");
        shopConfig.setAddress("123 Manchester Road");
        shopConfig.setVehicleTypes(new String[]{"Car", "Truck"});

        // Act
        TireChangeBooking booking = tireShopService.mapManchesterToTireChangeBooking(manchesterTime, shopConfig);

        // Assert
        assertThat(booking.getUniversalId()).isEqualTo("1");
        assertThat(booking.getBookingTime()).isEqualTo(LocalDateTime.of(2024, 3, 20, 9, 0));
        assertThat(booking.isAvailable()).isTrue();
        assertThat(booking.getTireShopName()).isEqualTo("Manchester Tire Shop");
        assertThat(booking.getTireShopAddress()).isEqualTo("123 Manchester Road");
        assertThat(booking.getVehicleType()).isEqualTo("Car, Truck");
        assertThat(booking.getApiIdentifier()).isEqualTo("Manchester");
    }
}
