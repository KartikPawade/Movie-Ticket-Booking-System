package com.movienow.org.service;

import com.movienow.org.dto.AddScreenRequest;
import com.movienow.org.dto.ScreenResponse;
import com.movienow.org.dto.TheatreResponse;
import com.movienow.org.entity.Screen;
import com.movienow.org.entity.Theatre;
import com.movienow.org.exception.BadRequestException;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.repository.MovieRepository;
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
    @Autowired
    private MovieRepository movieRepository;

    /**
     * Used to return available screens for given theatre with given movie
     *
     * @param theatreId
     * @param movieId
     * @return
     */
    public List<ScreenResponse> getScreens(Long theatreId, Long movieId) {
        theatreRepository.findById(theatreId).orElseThrow(()-> new NotFoundException("Theatre not found for given Id."));
        movieRepository.findById(movieId).orElseThrow(()-> new NotFoundException("Movie not found for given Id."));
        return screenRepository.getScreens(theatreId, movieId);
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
        List<String> screenNames = screenRequests.stream().map(AddScreenRequest::getName).toList();
        List<Screen> existScreens = screenRepository.findAllByTheatreAndNameIn(theatre, screenNames);
        if (!existScreens.isEmpty()) {
            throw new BadRequestException("Some of the requested Screens to be added already exist in the Theatre");
        }
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
