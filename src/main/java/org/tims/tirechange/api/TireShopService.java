package org.tims.tirechange.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tims.tirechange.configuration.TireShopConfig;
import org.tims.tirechange.configuration.TireShopConfigLoader;
import org.tims.tirechange.model.ManchesterTireChangeTime;
import org.tims.tirechange.model.TireChangeBooking;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TireShopService {

    private static final Logger logger = LoggerFactory.getLogger(LondonApi.class);
    @Autowired
    private LondonApi londonApi;
    @Autowired
    private ManchesterApi manchesterApi;
    @Autowired
    private TireShopConfigLoader configLoader;

    public List<TireChangeBooking> findAvailableTimes(LocalDate from, LocalDate until,
                                                      String tireShopName, List<String> vehicleTypes) throws IOException {
        List<TireChangeBooking> allResults = new ArrayList<>();

        List<TireShopConfig> tireShops = configLoader.loadConfig("src/main/resources/tire_shops.json");

        logger.info("shops configurations loaded from JSON file...");

        for (TireShopConfig shop : tireShops) {
            if (tireShopName == null || shop.getName().equalsIgnoreCase(tireShopName)) {
                if (shop.getApi().getType().equals("xml")) {
                    logger.info("type is XML...");

                    allResults.addAll(londonApi.getAvailableTimes(from, until));
                } else if (shop.getApi().getType().equals("json")) {
                    logger.info("type is json...");

                    allResults.addAll(manchesterApi.getAvailableTimes(from, until)
                            .stream()
                            .map(manchesterTime -> mapManchesterToTireChangeBooking(manchesterTime, shop))
                            .collect(Collectors.toList()));
                }
            }
        }

        // Filter aggregated results based on vehicleType (add filtering logic here)
        return allResults;
    }

    private TireChangeBooking mapManchesterToTireChangeBooking(ManchesterTireChangeTime manchesterTime, TireShopConfig currentShopConfig) {
        TireChangeBooking booking = new TireChangeBooking();
        booking.setUniversalId(String.valueOf(manchesterTime.getId()));
        booking.setBookingTime(manchesterTime.getBookingTime());
        booking.setAvailable(manchesterTime.isAvailable());
        booking.setTireShopName(currentShopConfig.getName());
        booking.setTireShopAddress(currentShopConfig.getAddress());
        booking.setVehicleType(Arrays.toString(currentShopConfig.getVehicleTypes()));
        booking.setApiIdentifier("Manchester");
        return booking;
    }
}
