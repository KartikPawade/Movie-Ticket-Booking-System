package com.movienow.org.service;

import com.movienow.org.dto.ScreenResponse;
import com.movienow.org.repository.ScreenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScreenService {
    @Autowired
    private ScreenRepository screenRepository;

    /**
     * Used to return available screens for given theatre with given movie for a city
     *
     * @param theatreId
     * @param movieId
     * @return
     */
    public List<ScreenResponse> getScreens(Long theatreId, Long cityMovieId) {
        return screenRepository.getScreens(theatreId, cityMovieId);
    }
}
