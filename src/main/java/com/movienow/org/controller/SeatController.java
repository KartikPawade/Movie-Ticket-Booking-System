package com.movienow.org.controller;

import com.movienow.org.payment.PaymentRequest;
import com.movienow.org.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1")
public class SeatController {
    @Autowired
    private SeatService seatService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/cities/theatres/movies/screens/shows/{showId}/seats")
    public ResponseEntity<Object> getSeats(@PathVariable("showId") final Long showId) {
        return ResponseEntity.ok().body(seatService.getSeats(showId));
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/cities/theatres/{theatreId}/screens/{screenId}/seats")
    public ResponseEntity<Object> getAllSeats(@PathVariable("theatreId") final Long theatreId,
                                              @PathVariable("screenId") final Long screenId) {
        return ResponseEntity.ok().body(seatService.getSeats(theatreId, screenId));
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping("/cities/theatres/screens/{screenId}/seats")
    public ResponseEntity<Object> addSeats(@PathVariable("screenId") final Long screenId,
                                           @RequestParam(name = "startSeatNumber") Short startSeatNumber,
                                           @RequestParam(name = "endSeatNumber") Short endSeatNumber) {
        return ResponseEntity.ok().body(seatService.addSeats(screenId, startSeatNumber,endSeatNumber));
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/cities/theatres/movies/screens/shows/{showId}/seats")
    public ResponseEntity<Object> bookSeats(@PathVariable("showId") final Long showId, @RequestBody List<Long> seatTimeSlotIds) {
        return ResponseEntity.ok().body(seatService.bookSeats(showId, seatTimeSlotIds));
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/cities/theatres/movies/screens/shows/{showId}/seats/checkout")
    public ResponseEntity<Object> checkout(@PathVariable("showId") final Long showId, @RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok().body(seatService.checkout(showId, paymentRequest));
    }
}