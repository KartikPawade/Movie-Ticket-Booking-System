package com.movienow.org.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public interface PaymentGatewayService<T> {
    PaymentResponse paymentGateway(T request, Double totalPrice);
}

@Component
class PaymentGatewayServiceImpl implements PaymentGatewayService<PaymentRequest> {
    @Autowired
    PaymentProviderService<PaymentService<PaymentRequest, PaymentResponse>> paymentProviderService;

    /**
     * Used to get the PaymentProvider based on the type select by the User, and re-route the flow to Payment-Execution method
     *
     * @param request
     * @param totalPrice
     * @return
     */
    @Override
    public PaymentResponse paymentGateway(PaymentRequest request, Double totalPrice) {
        return paymentProviderService.getProvider(request.getPaymentProviderType()).pay(request, totalPrice);
    }
}
