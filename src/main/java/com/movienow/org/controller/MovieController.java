package com.movienow.org.controller;

import com.movienow.org.dto.MovieRequest;
import com.movienow.org.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1")
public class MovieController {
    @Autowired
    private MovieService movieService;


    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/cities/{cityId}/movies")
    public ResponseEntity<Object> getMovies(@PathVariable("cityId") final Long cityId) {
        return ResponseEntity.ok().body(movieService.getMovies(cityId));
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/cities/theatres/{theatreId}/movies")
    public ResponseEntity<Object> getMoviesForTheatre(@PathVariable("theatreId") final Long theatreId) {
        return ResponseEntity.ok().body(movieService.getMoviesForTheatre(theatreId));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/movies")
    public ResponseEntity<Object> addMovie(@RequestBody MovieRequest movieRequest) {
        return ResponseEntity.ok().body(movieService.addMovie(movieRequest));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/cities/theatres/movies/{movieId}")
    public ResponseEntity<Object> addMovieToTheatres(@PathVariable(value = "movieId") final Long movieId, @RequestParam("theatreIds") List<Long> theatreIds) {
        return ResponseEntity.ok().body(movieService.addMovieToTheatres(movieId, theatreIds));
    }
}
