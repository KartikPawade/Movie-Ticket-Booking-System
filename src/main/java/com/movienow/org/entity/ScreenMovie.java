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
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_screen_movie", columnNames = {"screen_id","movie_id"})
})
public class ScreenMovie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "screen_id")
    private Screen screen;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    private Short seatPrice;

    @OneToMany(mappedBy = "screenMovie",cascade = CascadeType.ALL)
    List<ScreenTimeSlot> screenTimeSlots = new ArrayList<>();
}