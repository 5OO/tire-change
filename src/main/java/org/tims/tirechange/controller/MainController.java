package org.tims.tirechange.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tims.tirechange.api.LondonApi;
import org.tims.tirechange.api.TireShopService;
import org.tims.tirechange.model.LondonTireChangeTime;
import org.tims.tirechange.model.TireChangeBooking;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
public class MainController {

    @Autowired
    private LondonApi londonApi;

    @Autowired
    private TireShopService tireShopService;

//    @GetMapping("/tire-changes/available")
//    public List<TireChangeBooking> getAvailableTimes(
//            @RequestParam LocalDate from,
//            @RequestParam LocalDate until) throws IOException {
//        return londonApi.getAvailableTimes(from, until);
//    }

    @PutMapping("/tire-changes/{universalID}/booking")
    public ResponseEntity<LondonTireChangeTime> bookTimeSlot(@PathVariable String universalID,
                                                             @RequestBody String clientContactInformation) throws IOException {
        try {
            LondonTireChangeTime londonTireChangeTime = londonApi.bookTimeSlot(universalID, clientContactInformation);
            return ResponseEntity.ok(londonTireChangeTime); // Return an 'OK' status
        } catch (Exception e) {
            // Handle booking errors
            return ResponseEntity.internalServerError().build();
        }
    }

//    @GetMapping("/tire-changes/available/{tireShopName}/{vehicleType}")
//    public List<TireChangeBooking> findAvailableTimes(
//            @RequestParam LocalDate from,
//            @RequestParam LocalDate until,
//            @PathVariable(required = false) String tireShopName,
//            @PathVariable String vehicleType) throws IOException {
//        return tireShopService.findAvailableTimes(from, until, tireShopName, vehicleType);
//    }
}