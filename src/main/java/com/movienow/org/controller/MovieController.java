package com.movienow.org.controller;

import com.movienow.org.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;


    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/{cityId}")
    public ResponseEntity<Object> getMovies(@PathVariable("cityId") final Long cityId) {
        return ResponseEntity.ok().body(movieService.getMovies(cityId));
    }
}
