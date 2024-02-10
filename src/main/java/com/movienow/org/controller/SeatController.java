package com.movienow.org.controller;

import com.movienow.org.dto.SeatBookingRequest;
import com.movienow.org.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v1")
public class SeatController {
    @Autowired
    private SeatService seatService;

    /**
     * 1. Get all available seats for a timeslot selected for a day
     *
     * 2. (we need to block seats if seats are selected, at that time we perform a check so that same seat cant be booked by 2 persons)
     */

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/cities/theatres/movies/screens/time-slots/{timeSlotId}/seats")
    public ResponseEntity<Object> getSeats(@PathVariable("timeSlotId") final Long timeSlotId) {
        return ResponseEntity.ok().body(seatService.getSeats(timeSlotId));
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/cities/theatres/movies/screens/time-slots/{timeSlotId}/seats")
    public ResponseEntity<Object> bookSeats(@PathVariable("timeSlotId") final Long timeSlotId, @RequestBody List<Long> seatTimeSlotIds) {
        return ResponseEntity.ok().body(seatService.bookSeats(timeSlotId,  seatTimeSlotIds));
    }
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/cities/theatres/movies/screens/time-slots/{timeSlotId}/seats/transactions")
    public ResponseEntity<Object> doPayment(@PathVariable("timeSlotId") final Long timeSlotId, @RequestBody List<Long> seatTimeSlotIds) {
        return ResponseEntity.ok().body(seatService.doPayment());
    }
}