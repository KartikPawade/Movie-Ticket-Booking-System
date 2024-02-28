package com.movienow.org.service;

import com.movienow.org.constants.CacheConstants;
import com.movienow.org.dto.MovieDetailsResponse;
import com.movienow.org.dto.MovieRequest;
import com.movienow.org.dto.MovieResponse;
import com.movienow.org.entity.Movie;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private CityMovieRepository cityMovieRepository;

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private MovieRepository movieRepository;

    /**
     * Used to get Movies for city
     *
     * @param cityId
     * @return
     */
    @Cacheable(cacheNames = CacheConstants.CACHE_MOVIES_FOR_CITY,key = "#cityId", sync = true)
    public List<MovieDetailsResponse> getMovies(Long cityId) {
        cityRepository.findById(cityId).orElseThrow(() -> new NotFoundException("City not found for given cityId"));
        List<MovieResponse> movieDetails = cityMovieRepository.getMovies(cityId);
        List<MovieDetailsResponse> movieDetailsResponses =  movieDetails.stream().map(this::getMovieDetailsResponse).toList();
        return movieDetailsResponses;
    }

    /**
     * Used to get Movie Details Response
     *
     * @param movieResponse
     * @return
     */
    private MovieDetailsResponse getMovieDetailsResponse(MovieResponse movieResponse) {
        MovieDetailsResponse movieDetailsResponse = new MovieDetailsResponse();
        movieDetailsResponse.setId(movieResponse.getId());
        movieDetailsResponse.setName(movieResponse.getName());
        return movieDetailsResponse;
    }

    /**
     * Used to add Movie
     *
     * @param movieRequest
     * @return
     */
    public String addMovie(MovieRequest movieRequest) {
        Movie movie = getMovie(movieRequest);
        movieRepository.save(movie);
        return "Movie Added Successfully.";
    }

    /**
     * Used to get MovieEntity
     *
     * @param movieRequest
     * @return
     */
    private Movie getMovie(MovieRequest movieRequest) {
        Movie movie = new Movie();
        movie.setName(movieRequest.getName());
        return movie;
    }
}
