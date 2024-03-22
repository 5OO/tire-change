package org.tims.tirechange.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tims.tirechange.configuration.TireShopConfig;
import org.tims.tirechange.configuration.TireShopConfigLoader;
import org.tims.tirechange.exception.NoAvailableTimeslotsException;
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

    private static final Logger logger = LoggerFactory.getLogger(TireShopService.class);
    @Autowired
    private LondonApi londonApi;
    @Autowired
    private ManchesterApi manchesterApi;
    @Autowired
    private TireShopConfigLoader configLoader;

    public TimeslotFetchResult findAvailableTimes(LocalDate from, LocalDate until,
                                                      List<String> tireShopName, List<String> vehicleTypes) throws IOException {
        List<TireChangeBooking> allResults = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<TireShopConfig> tireShops = configLoader.loadConfig("src/main/resources/tire_shops.json");

        logger.info("shops configurations loaded from JSON file...");

        for (TireShopConfig shop : tireShops) {
            if (tireShopName == null || tireShopName.isEmpty() || tireShopName.contains(shop.getName())) {
                String endpoint = shop.getApi().getEndpoint();
                try {
                    if (shop.getApi().getType().equals("xml")) {
                        logger.info("api type is XML...");
                        allResults.addAll(londonApi.getAvailableTimes(from, until, endpoint));
                    } else if (shop.getApi().getType().equals("json")) {
                        logger.info("api type is json...");

                        allResults.addAll(manchesterApi.getAvailableTimes(from, until, endpoint)
                                .stream()
                                // Filter out any timeslots where 'available' is false
                                .filter(ManchesterTireChangeTime::isAvailable)
                                // Map each ManchesterTireChangeTime object to a TireChangeBooking object
                                .map(manchesterTime -> mapManchesterToTireChangeBooking(manchesterTime, shop))
                                // Collect the results back into a List
                                .collect(Collectors.toList()));
                    }
                } catch (NoAvailableTimeslotsException e) {
                    logger.info("No available timeslots found for {} using {}: {}", shop.getName(), shop.getApi().getType(), e.getMessage());
                    warnings.add("No available timeslots found for " + shop.getName());
                }
            }
        }

        // Filter aggregated results based on vehicleType
        if (vehicleTypes != null && !vehicleTypes.isEmpty()) {
            allResults = allResults.stream()
                    .filter(booking -> vehicleTypes.contains(booking.getVehicleType()))
                    .collect(Collectors.toList());
        }



        return new TimeslotFetchResult(allResults, warnings);
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

    public void bookTimeslot(String universalId, String tireShopName, String contactInformation) throws IOException {
        List<TireShopConfig> tireShops = configLoader.loadConfig("src/main/resources/tire_shops.json");

        TireShopConfig shopConfig = tireShops.stream()
                .filter(shop -> shop.getName().equalsIgnoreCase(tireShopName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tire Shop not found"));

        if ("xml".equals(shopConfig.getApi().getType())) {
            londonApi.bookTimeSlot(universalId, contactInformation);

            logger.info("tireShopService - bookTimeslot initiated for xml endpoint ...");
        } else if ("json".equals(shopConfig.getApi().getType())) {
            manchesterApi.bookTimeSlot(universalId, contactInformation);

            logger.info("tireShopService - bookTimeslot initiated for json endpoint ...");
        } else {
            logger.info("tireShopService - bookTimeslot failed ...");
            throw new UnsupportedOperationException("Unsupported API type");

        }
    }
}
