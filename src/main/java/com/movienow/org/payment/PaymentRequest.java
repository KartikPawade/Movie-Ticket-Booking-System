package com.movienow.org.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest<T> {
    private PaymentProviderType paymentProviderType;
    private Integer checkoutTotalPrice;
    private T t;
}
