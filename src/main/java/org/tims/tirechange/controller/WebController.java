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
import org.tims.tirechange.api.TireShopService;
import org.tims.tirechange.configuration.TireShopConfigLoader;
import org.tims.tirechange.model.TimeslotFetchResult;

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

    @GetMapping("/tire-changes/view")
    public String displayAvailableTimes(Model model,
                                        @RequestParam(required = false) List<String> tireShop,
                                        @RequestParam(required = false) List<String> vehicleType,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate untilDate,
                                        RedirectAttributes redirectAttributes) throws IOException {

        // Default to today and two days ahead if dates are not provided
        LocalDate from = (fromDate != null) ? fromDate : LocalDate.now();
        LocalDate until = (untilDate != null) ? untilDate : LocalDate.now().plusDays(2);

        // Check if 'from' date is after 'until' date
        if (from.isAfter(until)) {
            // Redirect back to the form with an error message
            redirectAttributes.addFlashAttribute("errorMessage", "The start date must be before the end date.");
            return "redirect:/tire-changes/view";
        }
        // Proceed with fetching and displaying the available times
        var tireShops = configLoader.loadConfig("src/main/resources/tire_shops.json");
        model.addAttribute("tireShops", configLoader.loadConfig("src/main/resources/tire_shops.json"));

        // Extract and add distinct vehicle types
        Set<String> allVehicleTypes = tireShops.stream()
                .flatMap(shop -> Arrays.stream(shop.getVehicleTypes()))
                .collect(Collectors.toSet());
        model.addAttribute("vehicleTypes", allVehicleTypes);

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
