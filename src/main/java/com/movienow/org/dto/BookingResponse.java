package com.movienow.org.dto;

import java.io.Serializable;

public interface BookingResponse extends Serializable {
    Long getSeatId();
    Double getPrice();
}
