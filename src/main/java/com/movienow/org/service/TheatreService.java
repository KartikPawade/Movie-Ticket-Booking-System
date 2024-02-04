package com.movienow.org.service;

import com.movienow.org.dto.AddTheatreRequest;
import com.movienow.org.entity.Address;
import com.movienow.org.entity.City;
import com.movienow.org.entity.Theatre;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.repository.CityRepository;
import com.movienow.org.repository.TheatreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TheatreService {
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private TheatreRepository theatreRepository;


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
}
