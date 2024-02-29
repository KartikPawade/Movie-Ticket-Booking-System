package com.movienow.org.dto;

import java.sql.Date;
import java.sql.Time;

public interface BookingDetailsDto {
    Double getBookingPrice();
    Long getSeatId();

    Date getDate();
    Time getShowTime();
    String getMovieName();
    Short getMovieLengthInMinutes();

    String getScreen();
    String getChargeId();
}
