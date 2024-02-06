package com.movienow.org.service;

import com.movienow.org.dto.ScreenTimeSlotDetails;
import com.movienow.org.dto.ScreenTimeSlotResponse;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.repository.ScreenRepository;
import com.movienow.org.repository.ScreenTimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScreenTimeSlotService {
    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private ScreenTimeSlotRepository screenTimeSlotRepository;

    /**
     * Used to get all Time Slots for available days for a screen
     *
     * @param screenId
     * @return
     */
    public Map<LocalDate, List<ScreenTimeSlotResponse>> getTimeSlots(Long screenId) {
        screenRepository.findById(screenId).orElseThrow(() -> new NotFoundException("Screen not found for given Id."));
        List<ScreenTimeSlotDetails> timeSlotDetails = screenTimeSlotRepository.getTimeSlots(screenId);

        return getTimeSlotResponseMap(timeSlotDetails);
    }

    /**
     * Used to get Time Slot with respect to Date
     * @param timeSlotDetails
     * @return
     */
    private static Map<LocalDate, List<ScreenTimeSlotResponse>> getTimeSlotResponseMap(List<ScreenTimeSlotDetails> timeSlotDetails) {
        Map<LocalDate, List<ScreenTimeSlotResponse>> timeSlotResponseMap = new LinkedHashMap<>();
        timeSlotDetails.forEach(screenTimeSlotDetails -> {
            var date = screenTimeSlotDetails.getDate().toLocalDate();
            List<ScreenTimeSlotResponse> list;
            ScreenTimeSlotResponse timeSlotResponse = new ScreenTimeSlotResponse();
            timeSlotResponse.setTimeSlotId(screenTimeSlotDetails.getTimeSlotId());
            timeSlotResponse.setSlotTime(screenTimeSlotDetails.getSlotTime());
            if (timeSlotResponseMap.containsKey(date)) {
                list = timeSlotResponseMap.get(date);
            } else {
                list = new ArrayList<>();
            }
            list.add(timeSlotResponse);
            timeSlotResponseMap.put(date, list);
        });
        return timeSlotResponseMap;
    }
}
