package com.movienow.org.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserBookingDetails implements Serializable {
    Long seatId;
    Double price;
}
