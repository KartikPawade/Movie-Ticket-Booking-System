package com.movienow.org.service;

import com.movienow.org.constants.CacheConstants;
import com.movienow.org.dto.*;
import com.movienow.org.entity.City;
import com.movienow.org.entity.Theatre;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.repository.CityRepository;
import com.movienow.org.repository.MovieRepository;
import com.movienow.org.repository.TheatreRepository;
import com.movienow.org.uitls.CacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = CacheConstants.CACHE_THEATRE_FOR_CITY_V_1)
public class TheatreService {

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private TheatreRepository theatreRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private CacheUtils cacheUtils;

    /**
     * Used to add new Theatre
     *
     * @param cityId
     * @param addTheatreRequest
     * @return
     */
    public String addTheatre(Long cityId, AddTheatreRequest addTheatreRequest) {
        Theatre theatre = getTheatre(cityId, addTheatreRequest);
        Theatre savedTheatre = theatreRepository.save(theatre);

        updateCache(cityId, savedTheatre);
        return "Theatre details saved successfully";
    }

    /**
     * Used to update Cache
     *
     * @param cityId
     * @param savedTheatre
     */
    private void updateCache(Long cityId, Theatre savedTheatre) {
        TheatreDetailsResponse theatreDetailsResponse = new TheatreDetailsResponse();
        theatreDetailsResponse.setId(savedTheatre.getId());
        theatreDetailsResponse.setName(savedTheatre.getName());

        cacheUtils.updateCacheList(CacheConstants.CACHE_THEATRE_FOR_CITY_V_1, cityId, TheatreDetailsResponse.class, theatreDetailsResponse);
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
    @Cacheable(key = "#cityId", sync = true)
    public List<TheatreDetailsResponse> getTheatres(Long cityId) {
        cityRepository.findById(cityId).orElseThrow(() -> new NotFoundException("City not found for given cityId"));
        List<TheatreDetails> theatreDetailsList = theatreRepository.getTheatres(cityId);
        List<TheatreDetailsResponse> theatreDetailsResponses = new ArrayList<>();
        theatreDetailsList.forEach(theatreDetails -> theatreDetailsResponses.add(getTheatreDetailsResponse(theatreDetails)));
        return theatreDetailsResponses;
    }

    /**
     * Used to get Theatre Details Response
     *
     * @param theatreDetails
     * @return
     */
    private TheatreDetailsResponse getTheatreDetailsResponse(TheatreDetails theatreDetails) {
        TheatreDetailsResponse theatreDetailsResponse = new TheatreDetailsResponse();
        theatreDetailsResponse.setId(theatreDetails.getId());
        theatreDetailsResponse.setName(theatreDetails.getName());
        return theatreDetailsResponse;
    }
}
