package com.movienow.org.service;

import com.movienow.org.dto.SeatResponse;
import com.movienow.org.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatService {
    @Autowired
    private SeatRepository seatRepository;
    public List<SeatResponse> getSeats(Long timeSlotId) {
        return seatRepository.getSeats(timeSlotId);
    }
}