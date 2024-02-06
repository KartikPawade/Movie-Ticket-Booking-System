package com.movienow.org.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenDetails {
    private Long screenId;
    private String screenName;
    List<TimeSlotDetails> timeSlots = new ArrayList<>();
}
