package com.movienow.org.controller;

import com.movienow.org.service.BookingDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/")
public class BookingDetailsController {

    @Autowired
    private BookingDetailsService bookingDetailsService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "booking-details")
    public ResponseEntity<Object> getUpcomingBookings() {
        return ResponseEntity.ok().body(bookingDetailsService.getUpcomingBookings());
    }
}
