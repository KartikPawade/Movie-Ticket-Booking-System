package com.movienow.org.controller;

import com.movienow.org.dto.AddTheatreRequest;
import com.movienow.org.service.TheatreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class TheatreController {
    @Autowired
    private TheatreService theatreService;

    /**
     * 1. add theatre - ADMIN role
     * 2. get all theatres by city
     */

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/cities/theatres/add-theatre")
    public ResponseEntity<Object> addTheatre(@RequestBody AddTheatreRequest addTheatreRequest) {
        return ResponseEntity.ok().body(theatreService.addTheatre(addTheatreRequest));
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/cities/{cityId}/theatres")
    public ResponseEntity<Object> getTheatres(@PathVariable("cityId") final Long cityId) {
        return ResponseEntity.ok().body(theatreService.getTheatres(cityId));
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/cities/movies/{cityMovieId}/theatres")
    public ResponseEntity<Object> getTheatresForMovieInCity(@PathVariable("cityMovieId") final Long cityMovieId) {
        return ResponseEntity.ok().body(theatreService.getTheatresForMovie(cityMovieId));
    }
}