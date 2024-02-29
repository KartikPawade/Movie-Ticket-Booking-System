package com.movienow.org.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDetailsResponse {
    Double bookingPrice;
    List<Long> seatIds = new ArrayList<>();
    LocalDate date;
    Time showTime;
    String movieName;
    Short movieLengthInMinutes;
    String screen;
}
