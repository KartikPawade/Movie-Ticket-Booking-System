package com.movienow.org.service;

import com.movienow.org.dto.AddScreenRequest;
import com.movienow.org.dto.ScreenResponse;
import com.movienow.org.dto.TheatreResponse;
import com.movienow.org.entity.Screen;
import com.movienow.org.entity.Theatre;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.repository.ScreenRepository;
import com.movienow.org.repository.TheatreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScreenService {
    @Autowired
    private ScreenRepository screenRepository;
    @Autowired
    private TheatreRepository theatreRepository;

    /**
     * Used to return available screens for given theatre with given movie for a city
     *
     * @param theatreId
     * @param cityMovieId
     * @return
     */
    public List<ScreenResponse> getScreens(Long theatreId, Long cityMovieId) {
        return screenRepository.getScreens(theatreId, cityMovieId);
    }

    /**
     * Used to add Screens to a particular Theatre
     *
     * @param theatreId
     * @param screenRequests
     * @return
     */
    public String addScreens(Long theatreId, List<AddScreenRequest> screenRequests) {
        Theatre theatre = theatreRepository.findById(theatreId).orElseThrow(() -> new NotFoundException("Theatre not found with given Id."));
        List<Screen> screens = new ArrayList<>();
        screenRequests.forEach(addScreenRequest -> {
            Screen screen = new Screen();
            screen.setName(addScreenRequest.getName());
            screen.setTheatre(theatre);
            screens.add(screen);
        });
        screenRepository.saveAll(screens);
        return "Screens Added successfully.";
    }
}
