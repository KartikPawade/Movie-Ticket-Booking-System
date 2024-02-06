package com.movienow.org.service;

import com.movienow.org.dto.ScreenData;
import com.movienow.org.dto.ScreenDetails;
import com.movienow.org.dto.ScreenResponse;
import com.movienow.org.dto.TimeSlotDetails;
import com.movienow.org.repository.ScreenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScreenService {
    @Autowired
    private ScreenRepository screenRepository;

    /**
     * Used to return available screens for given theatre with given movie for a city
     *
     * @param theatreId
     * @param cityMovieId
     * @return
     */
    public ScreenResponse getScreens(Long theatreId, Long cityMovieId) {
        List<ScreenData> screenDataList = screenRepository.getScreens(theatreId, cityMovieId);

        ScreenResponse screenResponse = getScreenDetails(screenDataList);
        return screenResponse;
    }

    /**
     * Use to get ScreenDetails
     *
     * @param screenDataList
     * @return
     */
    private ScreenResponse getScreenDetails(List<ScreenData> screenDataList) {
        ScreenResponse screenResponse = new ScreenResponse();
        if (screenDataList.isEmpty()) return screenResponse;
        ScreenData screenData = screenDataList.get(0);
        screenResponse.setTheatreId(screenData.getTheatreId());
        screenResponse.setTheatreName(screenData.getTheatreName());
        screenResponse.setMovieId(screenData.getMovieId());
        screenResponse.setMovieName(screenData.getMovieName());

        Map<Long, ScreenDetails> screenDetailsMap = new HashMap<>();
        screenDataList.forEach(details -> {
            ScreenDetails screenDetails;
            if (screenDetailsMap.containsKey(details.getScreenId())) {
                screenDetails = screenDetailsMap.get(details.getScreenId());
            } else {
                screenDetails = new ScreenDetails();
                screenDetails.setScreenId(details.getScreenId());
                screenDetails.setScreenName(details.getScreenName());
            }
            TimeSlotDetails timeSlotDetails = new TimeSlotDetails();
            timeSlotDetails.setTimeSlotId(details.getTimeSlotId());
            timeSlotDetails.setSlotTime(details.getStartTime());
            screenDetails.getTimeSlots().add(timeSlotDetails);
            screenDetailsMap.put(details.getScreenId(), screenDetails);
        });
        screenResponse.setScreens(screenDetailsMap);
        return screenResponse;
    }
}
