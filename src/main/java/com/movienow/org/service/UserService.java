package com.movienow.org.service;

import com.movienow.org.dto.RegisterRequest;
import com.movienow.org.entity.AppUser;
import com.movienow.org.entity.Role;
import com.movienow.org.entity.UserRole;
import com.movienow.org.exception.BadRequestException;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.repository.RoleRepository;
import com.movienow.org.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;


    /**
     * Used to register new User
     *
     * @param registerRequest
     * @return
     */
    public String registerUser(RegisterRequest registerRequest) {
        // check if already present with email
        Optional<AppUser> optionalUser = userRepository.findByEmail(registerRequest.getEmail());
        if (optionalUser.isPresent()) {
            throw new BadRequestException("User already present with given email");
        }
        // validate role
        UserRole userRole = roleRepository.findByRole(Role.USER).orElseThrow(() -> new NotFoundException("Role not found"));

        AppUser appUser = new AppUser();
        appUser.setFirstName(registerRequest.getFirstName());
        appUser.setLastName(registerRequest.getLastName());
        appUser.setEmail(registerRequest.getEmail());
        appUser.setPhone(registerRequest.getPhone());
        appUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        appUser.setUserRole(userRole);
        userRepository.save(appUser);
        return "user saved successfully";
    }
}
