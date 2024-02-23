package com.movienow.org.controller;


import com.movienow.org.dto.AddScreenRequest;
import com.movienow.org.service.ScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1")
public class ScreenController {
    @Autowired
    private ScreenService screenService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/cities/theatres/{theatreId}/movies/{cityMovieId}/screens")
    public ResponseEntity<Object> getScreens(@PathVariable(value = "theatreId") final Long theatreId,
                                             @PathVariable(value = "cityMovieId") final Long cityMovieId) {
        return ResponseEntity.ok().body(screenService.getScreens(theatreId, cityMovieId));
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/cities/theatres/{theatreId}/screens")
    public ResponseEntity<Object> addScreens(@PathVariable(value = "theatreId") final Long theatreId,
                                             @RequestBody List<AddScreenRequest> screenRequests) {
        return ResponseEntity.ok().body(screenService.addScreens(theatreId, screenRequests));
    }
}
