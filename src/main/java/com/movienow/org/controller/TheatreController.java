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

    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping("/cities/{cityId}/theatres/add-theatre")
    public ResponseEntity<Object> addTheatre(@PathVariable(name = "cityId") final Long cityId, @RequestBody AddTheatreRequest addTheatreRequest) {
        return ResponseEntity.ok().body(theatreService.addTheatre(cityId, addTheatreRequest));
    }

    @PreAuthorize("hasAuthority('USER') or hasAuthority('MANAGER')")
    @GetMapping("/cities/{cityId}/theatres")
    public ResponseEntity<Object> getTheatres(@PathVariable("cityId") final Long cityId) {
        return ResponseEntity.ok().body(theatreService.getTheatres(cityId));
    }
}