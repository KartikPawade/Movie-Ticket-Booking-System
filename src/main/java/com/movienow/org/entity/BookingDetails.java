
package com.movienow.org.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BookingDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    AppUser user;
    private String chargeId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<TimeSlotSeat> seatTimeSlots = new ArrayList<>();

    private Double totalBookingPrice;
}