package com.movienow.org.dto;

import com.movienow.org.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Long phone;

    private Role role;
}
