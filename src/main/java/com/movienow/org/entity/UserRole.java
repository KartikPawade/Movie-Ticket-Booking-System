package com.movienow.org.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class UserRole {
    @Id
    private Long id;
    private Role role;
}
