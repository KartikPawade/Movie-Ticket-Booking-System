package com.movienow.org.controller;

import com.movienow.org.dto.MovieShowDetailsRequest;
import com.movienow.org.service.MovieShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1")
public class MovieShowController {
    @Autowired
    private MovieShowService movieShowService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/cities/{cityId}/theatres/movies/{movieId}/shows")
    public ResponseEntity<Object> getAllShows(@PathVariable("cityId") final Long cityId,
                                              @PathVariable("movieId") final Long movieId) {
        return ResponseEntity.ok().body(movieShowService.getAllShows(cityId, movieId));
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/cities/theatres/{theatreId}/movies/shows")
    public ResponseEntity<Object> getAllShows(@PathVariable("theatreId") final Long theatreId){
        return ResponseEntity.ok().body(movieShowService.getAllShows(theatreId));
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping("/cities/theatres/movies/{movieId}/screens/{screenId}/shows")
    public ResponseEntity<Object> addMovieShowsToScreen(@PathVariable("movieId") final Long movieId,
                                                        @PathVariable("screenId") final Long screenId,
                                                        @RequestBody MovieShowDetailsRequest movieShowDetailsRequest) {
        return ResponseEntity.ok().body(movieShowService.addMovieShowsToScreen(movieId, screenId, movieShowDetailsRequest));
    }
}
