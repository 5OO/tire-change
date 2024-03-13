package org.tims.tirechange.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tims.tirechange.api.LondonApi;
import org.tims.tirechange.model.TireChangeBooking;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
public class MainController {

    @Autowired
    private LondonApi londonApi;

    @GetMapping("/tire-changes/available")
    public List<TireChangeBooking> getAvailableTimes(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate until) throws IOException {
        return londonApi.getAvailableTimes(from, until);
    }
}
