package com.movienow.org.service;

import com.movienow.org.dto.AddTheatreRequest;
import com.movienow.org.dto.TheatreMovieResponse;
import com.movienow.org.dto.TheatreResponse;
import com.movienow.org.entity.Address;
import com.movienow.org.entity.City;
import com.movienow.org.entity.Theatre;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.repository.CityMovieRepository;
import com.movienow.org.repository.CityRepository;
import com.movienow.org.repository.TheatreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TheatreService {
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private TheatreRepository theatreRepository;
    @Autowired
    private CityMovieRepository cityMovieRepository;


    /**
     * Used to add new Theatre
     *
     * @param addTheatreRequest
     * @return
     */
    public String addTheatre(AddTheatreRequest addTheatreRequest) {
        Theatre theatre = getTheatre(addTheatreRequest);
        theatreRepository.save(theatre);
        return "Theatre details saved successfully";
    }

    /**
     * Used to get Theatre object
     *
     * @param addTheatreRequest
     * @return
     */
    private Theatre getTheatre(AddTheatreRequest addTheatreRequest) {
        Theatre theatre = new Theatre();
        theatre.setName(addTheatreRequest.getName());

        City city = cityRepository.findById(addTheatreRequest.getAddress().getCityId()).orElseThrow(() -> new NotFoundException("City with given Id not found."));

        Address address = new Address();
        address.setArea(addTheatreRequest.getAddress().getArea());
        address.setCity(city);
        theatre.setAddress(address);

        return theatre;
    }

    /**
     * Used to get Theatres for city
     *
     * @param cityId
     * @return
     */
    public List<TheatreResponse> getTheatres(Long cityId) {
        cityRepository.findById(cityId).orElseThrow(() -> new NotFoundException("City not found for given cityId"));
        return theatreRepository.getTheatres(cityId);
    }

    /**
     * Used to get Theatres for a movie in city
     *
     * @param cityMovieId
     * @return
     */
    public List<TheatreMovieResponse> getTheatresForMovie(Long cityMovieId) {
        cityMovieRepository.findById(cityMovieId).orElseThrow(() -> new NotFoundException("No Details found for Movies available in City for given Id."));
        return theatreRepository.getTheatresForMovieInCity(cityMovieId);
    }
}
