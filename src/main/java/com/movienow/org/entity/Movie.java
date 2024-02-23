package com.movienow.org.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Short movieLengthInMinutes;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<CityMovie> cityMovieList = new ArrayList<>();
}
