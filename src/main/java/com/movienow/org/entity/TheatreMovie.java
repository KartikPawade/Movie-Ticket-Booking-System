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
        @UniqueConstraint(name = "uk_theatre_movie",columnNames = {"cityMovie","theatre"})
})
public class TheatreMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "city_movie_id")
    private CityMovie cityMovie;

    @ManyToOne
    @JoinColumn(name = "theatre_id")
    private Theatre theatre;
}
