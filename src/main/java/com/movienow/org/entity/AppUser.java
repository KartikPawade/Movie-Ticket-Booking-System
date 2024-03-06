package com.movienow.org.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "phone"})
})
public class AppUser {

    private String firstName;
    private String lastName;
    @Id
    private String email;
    @NonNull
    private String password;
    private Long phone;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    private UserRole userRole;

}
