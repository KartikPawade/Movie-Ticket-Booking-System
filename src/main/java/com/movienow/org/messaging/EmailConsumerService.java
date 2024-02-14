package com.movienow.org.messaging;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
    @Value("${rabbitmq.email.queue}")
    private String emailQueueName;
    @Autowired
    private JavaMailSender javaMailSender;

    @RabbitListener(queues = "email_queue", concurrency = "4")
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(1000))
    public void sendEmail(String customerEmail, Channel channel,  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom("kartikpawade25@gmail.com");
            simpleMailMessage.setTo(customerEmail);
            simpleMailMessage.setText("Hi, your ticket has been booked Successfully.");
            simpleMailMessage.setSubject("Movie Tickets Booked");
            javaMailSender.send(simpleMailMessage);

            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            log.error("Email Consumer Service::" + e.getMessage());
            throw e;
        }
    }
}
