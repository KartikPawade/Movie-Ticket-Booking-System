package com.movienow.org.payment;

import lombok.Data;

@Data
public class PaymentResponse {
    private String sellerMessage;
    private String chargeId;
}
