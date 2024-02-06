package com.movienow.org.controller;


import com.movienow.org.service.ScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1")
public class ScreenController {
    @Autowired
    private ScreenService screenService;
    /**
     * 1. add screen wit movie
     * 2. delete screen with movie
     * <p>
     * APP-FLOW
     * after select city user will get list of (theatres from that city[theatre + city] /movies in the city[movie + city])
     * <p>
     * 1. user will select theatre, then he will select movie                                  , {P1}then he will select screen, then time slot..
     * 2. user will select movie, then he will get a theatre list ,then theatre                , {P1}then he will select screen, then time slot..
     * <p>
     * so at point {P1} we will have theatre, movie , so it common API
     * <p>
     * <p>
     * Here screen API is common with no data frequent change, so this can be cached later.
     */

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/theatres/{theatreId}/movies/{movieId}/screens")
    public ResponseEntity<Object> getScreens(@PathVariable(value = "theatreId") final Long theatreId,
                                             @PathVariable(value = "movieId") final Long movieId) {
        return ResponseEntity.ok().body(screenService.getScreens(theatreId,movieId));
    }
}
