package com.movienow.org.controller;

import com.movienow.org.dto.AddTheatreRequest;
import com.movienow.org.service.TheatreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/theatres")
public class TheatreController {
    @Autowired
    private TheatreService  theatreService;
    /**
     * 1. add theatre - ADMIN role
     * 2. get all theatres by city
     */

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-theatre")
    public ResponseEntity<Object> addTheatre(@RequestBody AddTheatreRequest addTheatreRequest){
        return ResponseEntity.ok().body(theatreService.addTheatre(addTheatreRequest));
    }
}