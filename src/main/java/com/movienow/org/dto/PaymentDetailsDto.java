package com.movienow.org.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailsDto implements Serializable {
    private String chargeId;
    List<Long> seatIds = new ArrayList<>();
    Double totalPrice;
    Long showId;
    String userEmail;
}
