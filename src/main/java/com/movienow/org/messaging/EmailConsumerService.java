package com.movienow.org.messaging;

import com.movienow.org.dto.EmailDetails;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class EmailConsumerService {
    @Value(value = "${mail.username}")
    private String mailUserName;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private JavaMailSender javaMailSender;

    @RabbitListener(queues = "email_queue", concurrency = "5")
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(1000))
    public void sendEmail(EmailDetails emailDetails) throws IOException {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(mailUserName);
            simpleMailMessage.setTo(emailDetails.getEmail());
            simpleMailMessage.setText("Hi, your ticket have been booked Successfully for price:" + emailDetails.getPrice() + ".\n" + "Tickets : " + emailDetails.getSeatIds());
            simpleMailMessage.setSubject("Movie Tickets Booked");
            javaMailSender.send(simpleMailMessage);

        } catch (Exception e) {
            log.error("Email Consumer Service::" + e.getMessage());
            throw e;
        }
    }
}
