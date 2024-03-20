package org.tims.tirechange.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.tims.tirechange.api.TireShopService;
import org.tims.tirechange.configuration.TireShopConfigLoader;
import org.tims.tirechange.model.TireChangeBooking;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);
    @Autowired
    private final TireShopConfigLoader configLoader;
    @Autowired
    private TireShopService tireShopService;

    @GetMapping("/tire-changes-available")
    public List<TireChangeBooking> findAvailableTimes(
            @RequestParam LocalDate from,
            @RequestParam LocalDate until,
            @RequestParam(required = false) List<String> tireShops, // Allow multiple values
            @RequestParam(required = false) List<String> vehicleTypes) throws IOException {
        // Parse shops into a comma-separated string if needed
        String tireShopFilter = tireShops != null ? String.join(",", tireShops) : null;

        List<TireChangeBooking> tireChangeBookings = tireShopService.findAvailableTimes(from, until, tireShopFilter, vehicleTypes);
        logger.info("Available Bookings: {}", tireChangeBookings);

        return tireChangeBookings;
    }

    @GetMapping("/tire-changes/view") // Or your relevant mapping
    public String displayAvailableTimes(Model model) throws IOException {
        model.addAttribute("tireShops", configLoader.loadConfig("src/main/resources/tire_shops.json"));
        List<TireChangeBooking> bookings = tireShopService.findAvailableTimes(LocalDate.now(), LocalDate.now().plusDays(2), null, null);
        model.addAttribute("bookings", bookings);  // Add the bookings list
        return "index";
    }
}
