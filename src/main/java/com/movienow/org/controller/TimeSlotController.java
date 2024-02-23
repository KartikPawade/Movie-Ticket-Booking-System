package com.movienow.org.controller;

import com.movienow.org.dto.MovieTimeSlotRequest;
import com.movienow.org.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1")
public class TimeSlotController {
    @Autowired
    private TimeSlotService timeSlotService;


    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/cities/theatres/movies/screens/{screenId}/time-slots")
    public ResponseEntity<Object> getTimeSlots(@PathVariable("screenId") final Long screenId) {
        return ResponseEntity.ok().body(timeSlotService.getTimeSlots(screenId));
    }


    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping("/cities/theatres/movies/{movieId}/screens/{screenId}/time-slots")
    public ResponseEntity<Object> addMovieToScreenWithTimeSlots(@PathVariable("movieId") final Long movieId,
                                                                @PathVariable("screenId") final Long screenId,
                                                                @RequestBody MovieTimeSlotRequest movieTimeSlotRequest) {
        return ResponseEntity.ok().body(timeSlotService.addMovieToScreenWithTimeSlots(movieId,screenId,movieTimeSlotRequest));
    }
}
