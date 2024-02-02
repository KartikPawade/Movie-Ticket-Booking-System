package com.movienow.org.controller;

import com.movienow.org.dto.LoginRequest;
import com.movienow.org.dto.RegisterUserDto;
import com.movienow.org.security.JwtUtils;
import com.movienow.org.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 1. register user
     * 2. login user
     * 3. logout user
     */


    /**
     * Used to register user
     *
     * @param registerUserDto
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody RegisterUserDto registerUserDto) {
        return ResponseEntity.ok().body(userService.registerUser(registerUserDto));
    }

    /**
     * Used to log in User
     *
     * @param loginRequest
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtUtils.generateTokenFromUsername(userDetails.getUsername());

        return ResponseEntity.ok().body(jwt);
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logoutUser() {//to be implemented
        return null;
    }
}
