package com.movienow.org.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movienow.org.exception.BadRequestException;
import com.movienow.org.payment.stripegateway.StripePaymentRequest;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public interface PaymentService<T, R> {
    R pay(T request, Double totalPrice);
}

@Component
@Slf4j
class StripePaymentService implements PaymentService<PaymentRequest<StripePaymentRequest>, PaymentResponse> {
    @Value("${api.stripe.key}")
    private String stripeSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecret;
    }

    /**
     * Used to make payment using Stripe Payment Gateway
     *
     * @param paymentRequest
     * @param totalPrice
     * @return
     */
    @Override
    public PaymentResponse pay(PaymentRequest<StripePaymentRequest> paymentRequest, Double totalPrice) {
        try {
            Token token = getToken(paymentRequest);
            if (token == null || token.getId() == null) {
                throw new BadRequestException("Invalid Card Details.");
            }
            return charge(paymentRequest, totalPrice, token);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Used to charge amount to client for the token generated from Stripe
     * Generates a Unique charge-Id
     *
     * @param paymentRequest
     * @param totalPrice
     * @param token
     * @return
     */
    private PaymentResponse charge(PaymentRequest<StripePaymentRequest> paymentRequest, Double totalPrice, Token token) {
        PaymentResponse paymentResponse = new PaymentResponse();
        try {
            StripePaymentRequest stripePaymentRequest = getStripePaymentRequest(paymentRequest);
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", totalPrice.intValue());
            chargeParams.put("currency", "INR");
            chargeParams.put("description", "Payment for id " + stripePaymentRequest.getAdditionalInfo().getOrDefault("ID_TAG", ""));
            chargeParams.put("source", token.getId());
            Map<String, Object> metaData = new HashMap<>();
            metaData.put("id", stripePaymentRequest.getChargeId());
            metaData.putAll(stripePaymentRequest.getAdditionalInfo());
            chargeParams.put("metadata", metaData);
            Charge charge = Charge.create(chargeParams);

            if (charge == null || charge.getId() == null) throw new BadRequestException("Payment failed.");
            paymentResponse.setSellerMessage(charge.getOutcome().getSellerMessage());
            paymentResponse.setChargeId(charge.getId());

        } catch (Exception e) {
            log.error("Stripe Charge");
            throw new BadRequestException(e.getMessage());
        }
        return paymentResponse;
    }


    /**
     * Used to get generated Token from Stripe Token using Card Details
     *
     * @param paymentRequest
     * @return
     */
    private Token getToken(PaymentRequest<StripePaymentRequest> paymentRequest) {
        Token token;
        try {
            StripePaymentRequest stripePaymentRequest = getStripePaymentRequest(paymentRequest);

            Map<String, Object> cardDetailsMap = new HashMap<>();
            cardDetailsMap.put("number", stripePaymentRequest.getCardNumber());
            cardDetailsMap.put("exp_month", Integer.parseInt(stripePaymentRequest.getExpMonth()));
            cardDetailsMap.put("exp_year", Integer.parseInt(stripePaymentRequest.getExpYear()));
            cardDetailsMap.put("cvc", Integer.parseInt(stripePaymentRequest.getCvc()));
            Map<String, Object> params = new HashMap<>();
            params.put("card", cardDetailsMap);
            token = Token.create(params);
        } catch (Exception e) {
            log.error("Stripe Payment Service::" + e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
        return token;
    }

    /**
     * Used to get StripePayment Object from JSON while serialization
     * this method needs to be explicitly used most of the time when we use GENERICS in java.
     *
     * @param paymentRequest
     * @return
     * @throws IOException
     */
    private static StripePaymentRequest getStripePaymentRequest(PaymentRequest<StripePaymentRequest> paymentRequest) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        byte[] stripeRequestBytes = mapper.writeValueAsBytes(paymentRequest.getT());
        return mapper.readValue(stripeRequestBytes, StripePaymentRequest.class);
    }
}
