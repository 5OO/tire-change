package org.tims.tirechange.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.tims.tirechange.api.TimeslotFetchResult;
import org.tims.tirechange.api.TireShopService;
import org.tims.tirechange.configuration.TireShopConfigLoader;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);
    @Autowired
    private final TireShopConfigLoader configLoader;
    @Autowired
    private TireShopService tireShopService;

    @GetMapping("/tire-changes-available")
    public String findAvailableTimes(
            Model model,
            @RequestParam LocalDate from,
            @RequestParam LocalDate until,
            @RequestParam(required = false) List<String> tireShops, // Allow multiple values
            @RequestParam(required = false) List<String> vehicleTypes) throws IOException {
        // Parse shops into a comma-separated string if needed
        logger.info("web-controller - tire-changes-available -> from {}", from);
        logger.info("tire-changes-available -> until {}", until);
        logger.info("tire shop filter {}", tireShops);
        logger.info("vehicle types {}", vehicleTypes);

        TimeslotFetchResult fetchResult = tireShopService.findAvailableTimes(from, until, tireShops, vehicleTypes);

        logger.info("Available Bookings: {}", fetchResult.getAvailableTimeslots());

        model.addAttribute("tireChangeBookings", fetchResult.getAvailableTimeslots()); // Add data to Model
        model.addAttribute("warnings", fetchResult.getWarnings()); // Add warnings to the model

        return "tire-changes-available"; // Return template name
    }

    @GetMapping("/tire-changes/view") // Or your relevant mapping
    public String displayAvailableTimes(Model model,
                                        @RequestParam(required = false) List<String> tireShop,
                                        @RequestParam(required = false) List<String> vehicleType,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate untilDate) throws IOException {
        var tireShops = configLoader.loadConfig("src/main/resources/tire_shops.json");
        model.addAttribute("tireShops", configLoader.loadConfig("src/main/resources/tire_shops.json"));

        // Extract and add distinct vehicle types
        Set<String> allVehicleTypes = tireShops.stream()
                .flatMap(shop -> Arrays.stream(shop.getVehicleTypes()))
                .collect(Collectors.toSet());
        model.addAttribute("vehicleTypes", allVehicleTypes);
        // Determine the date range for filtering
        LocalDate from = (fromDate != null) ? fromDate : LocalDate.now();
        LocalDate until = (untilDate != null) ? untilDate : LocalDate.now().plusDays(2);

        // Perform the filtering based on the provided parameters
        TimeslotFetchResult tireShopServiceAvailableTimes = tireShopService.findAvailableTimes(from, until, tireShop, vehicleType);

        model.addAttribute("bookings", tireShopServiceAvailableTimes.getAvailableTimeslots());  // Add the bookings list
        model.addAttribute("warnings", tireShopServiceAvailableTimes.getWarnings()); // Add warnings to the model
        return "index";
    }

    @PostMapping("/book-timeslot")
    public String bookTimeslot(@RequestParam String universalId,
                               @RequestParam String tireShopName,
                               @RequestParam String contactInformation,
                               RedirectAttributes redirectAttributes) {
        // Logic to call the appropriate booking API based on tireShopName
        try {
            tireShopService.bookTimeslot(universalId, tireShopName, contactInformation);
            redirectAttributes.addFlashAttribute("successMessage", "Booking successful!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking failed: " + e.getMessage());
        }

        return "redirect:/tire-changes/view";
    }
}
