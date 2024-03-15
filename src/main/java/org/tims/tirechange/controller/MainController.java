package org.tims.tirechange.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tims.tirechange.api.LondonApi;
import org.tims.tirechange.model.TireChangeBooking;
import org.tims.tirechange.model.TireChangeTime;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
public class MainController {

    @Autowired
    private LondonApi londonApi;

    @GetMapping("/tire-changes/available")
    public List<TireChangeBooking> getAvailableTimes(
            @RequestParam LocalDate from,
            @RequestParam LocalDate until) throws IOException {
        return londonApi.getAvailableTimes(from, until);
    }

    @PutMapping("/tire-changes/{universalID}/booking2")
    public ResponseEntity<TireChangeTime> bookTimeSlot(@PathVariable String universalID, @RequestBody String clientContactInformation) throws IOException {
        try {
            TireChangeTime tireChangeTime = londonApi.bookTimeSlot(universalID, clientContactInformation);
            return ResponseEntity.ok(tireChangeTime); // Return an 'OK' status
        } catch (Exception e) {
            // Handle booking errors
            return ResponseEntity.internalServerError().build();
        }
    }
}
