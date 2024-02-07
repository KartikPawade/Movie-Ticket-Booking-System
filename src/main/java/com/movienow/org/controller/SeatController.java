package com.movienow.org.controller;

import com.movienow.org.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1")
public class SeatController {
    @Autowired
    private SeatService seatService;

    /**
     * 1. Get all available seats for a timeslot selected for a day
     *
     * (we need to block seats if seats are selected, at that time we perform a check so that same seat cant be booked by 2 persons)
     */

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/cities/theatres/movies/screens/time-slots/{timeSlotId}/seats")
    public ResponseEntity<Object> getSeats(@PathVariable("timeSlotId") final Long timeSlotId) {
        return ResponseEntity.ok().body(seatService.getSeats(timeSlotId));
    }
}