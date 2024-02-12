package com.movienow.org.payment;

import com.movienow.org.exception.BadRequestException;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public interface PaymentProviderService<T> {


    T getProvider(PaymentProviderType paymentProviderType);
}

@Component
class PaymentProviderServiceServiceImpl implements PaymentProviderService<PaymentService<PaymentRequest, PaymentResponse>> {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    @SuppressWarnings(value = "rawtypes")
    public PaymentService getProvider(PaymentProviderType paymentProviderType) {
        switch (paymentProviderType) {
            case STRIPE -> {
                return applicationContext.getBean(StripePaymentService.class);
            }
            case PAYTM -> throw new BadRequestException("PAYTM is Yet to be configured for receiving Payments.");
            case PHONEPAY -> throw new BadRequestException("PhonePay is Yet to be configured for receiving Payments.");
            default -> throw new BadRequestException("InCompatible Payment Provider requested.");
        }
    }
}