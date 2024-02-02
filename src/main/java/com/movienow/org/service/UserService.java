package com.movienow.org.service;

import com.movienow.org.dto.RegisterUserDto;
import com.movienow.org.entity.AppUser;
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


    /**
     * Used to register new User
     *
     * @param registerUserDto
     * @return
     */
    public String registerUser(RegisterUserDto registerUserDto) {
        // check if already present with email
        Optional<AppUser> optionalUser = userRepository.findByEmail(registerUserDto.getEmail());
        if(optionalUser.isPresent()){
            throw new RuntimeException();
        }

        AppUser appUser = new AppUser();
        appUser.setFirstName(registerUserDto.getFirstName());
        appUser.setLastName(registerUserDto.getLastName());
        appUser.setEmail(registerUserDto.getEmail());
        appUser.setPhone(registerUserDto.getPhone());
        appUser.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        userRepository.save(appUser);
        return "user saved successfully";
    }
}
