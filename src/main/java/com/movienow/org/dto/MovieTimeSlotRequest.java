package com.movienow.org.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieTimeSlotRequest {
    private Short seatPrice;
    private Map<Date, List<Time>> timeSlots = new HashMap<>();
}
