package com.movienow.org.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_screen_theatre", columnNames = {"name", "theatre_id"})
})
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "theatre_id")
    private Theatre theatre;
}
