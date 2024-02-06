package com.movienow.org.controller;

import com.movienow.org.service.ScreenTimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1")
public class TimeSlotController {
    @Autowired
    private ScreenTimeSlotService screenTimeSlotService;

    /**
     * get time slots for all available for selected screen
     */

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/cities/theatres/movies/screens/{screenId}/time-slots")
    public ResponseEntity<Object> getTimeSlots(@PathVariable("screenId") final Long screenId) {
        return ResponseEntity.ok().body(screenTimeSlotService.getTimeSlots(screenId));
    }
}
