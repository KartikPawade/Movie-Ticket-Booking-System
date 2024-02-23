package com.movienow.org.service;

import com.movienow.org.dto.AddTheatreRequest;
import com.movienow.org.dto.TheatreDetails;
import com.movienow.org.dto.TheatreResponse;
import com.movienow.org.entity.City;
import com.movienow.org.entity.Theatre;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.repository.CityRepository;
import com.movienow.org.repository.MovieRepository;
import com.movienow.org.repository.TheatreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TheatreService {
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private TheatreRepository theatreRepository;
    @Autowired
    private MovieRepository movieRepository;


    /**
     * Used to add new Theatre
     *
     * @param cityId
     * @param addTheatreRequest
     * @return
     */
    public String addTheatre(Long cityId, AddTheatreRequest addTheatreRequest) {
        Theatre theatre = getTheatre(cityId, addTheatreRequest);
        theatreRepository.save(theatre);
        return "Theatre details saved successfully";
    }

    /**
     * Used to get Theatre object
     *
     * @param cityId
     * @param addTheatreRequest
     * @return
     */
    private Theatre getTheatre(Long cityId, AddTheatreRequest addTheatreRequest) {
        Theatre theatre = new Theatre();
        theatre.setName(addTheatreRequest.getName());
        theatre.setAddress(addTheatreRequest.getAddress());
        City city = cityRepository.findById(cityId).orElseThrow(() -> new NotFoundException("City with given Id not found."));

        theatre.setCity(city);
        return theatre;
    }

    /**
     * Used to get Theatres for city
     *
     * @param cityId
     * @return
     */
    public List<TheatreDetails> getTheatres(Long cityId) {
        cityRepository.findById(cityId).orElseThrow(() -> new NotFoundException("City not found for given cityId"));
//        return theatreRepository.getTheatres(cityId);
        return null;
    }

    /**
     * Used to get Theatres for a movie in city
     *
     * @param cityId
     * @param movieId
     * @return
     */
    public List<TheatreResponse> getTheatresForMovie(Long cityId, Long movieId) {
        cityRepository.findById(cityId).orElseThrow(() -> new NotFoundException("City not found with given Id."));
        movieRepository.findById(movieId).orElseThrow(() -> new NotFoundException("Movie not found with given Id."));
        List<TheatreDetails> theatreDetailsList = theatreRepository.getTheatresForMovieInCity(movieId, cityId);
        List<TheatreResponse> theatreResponses = new ArrayList<>();
        theatreDetailsList.forEach(theatreDetails -> theatreResponses.add(getTheatreResponse(theatreDetails)));
        return theatreResponses;
    }

    /**
     * Used to get Theatre Response
     *
     * @param theatreDetails
     * @return
     */
    private TheatreResponse getTheatreResponse(TheatreDetails theatreDetails) {
        TheatreResponse theatreResponse = new TheatreResponse();
        theatreResponse.setId(theatreDetails.getId());
        theatreResponse.setName(theatreDetails.getName());
        return theatreResponse;
    }
}
