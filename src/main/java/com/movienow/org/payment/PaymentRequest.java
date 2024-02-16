package com.movienow.org.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest<T> {
    Set<Long> seatIds = new HashSet<>();
    private PaymentProviderType paymentProviderType;
    private Integer checkoutTotalPrice;
    private T t;
}
