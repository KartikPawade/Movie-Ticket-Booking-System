package com.movienow.org.service;

import com.movienow.org.EmailDetailsDto;
import com.movienow.org.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailConsumerService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(EmailDetailsDto emailDetails) {

        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom("pwdkartik@gmail.com");
            simpleMailMessage.setTo(emailDetails.getEmail());
            simpleMailMessage.setText("Hi, your tickets " + emailDetails.getSeatIds() + " has been booked Successfully for price: " + emailDetails.getPrice());
            simpleMailMessage.setSubject("Movie Tickets Booked");
            javaMailSender.send(simpleMailMessage);

        } catch (Exception e) {
            log.error("Email Consumer Service::" + e.getMessage());
            throw new BadRequestException("Could not send email.");
        }

    }
}