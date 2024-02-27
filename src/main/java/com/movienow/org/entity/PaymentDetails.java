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
        @UniqueConstraint(name = "uk_chargeId_user", columnNames = {"user_id","charge_id"})
})
public class PaymentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String chargeId;
    private Double totalBookingPrice;
    @ManyToOne
    @JoinColumn(name = "user_id")
    AppUser user;

    @OneToMany(mappedBy = "paymentDetails",cascade = CascadeType.ALL)
    private List<BookingDetails> bookingDetails = new ArrayList<>();
}
