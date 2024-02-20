package com.movienow.org.service;

import com.movienow.org.dto.MovieRequest;
import com.movienow.org.dto.MovieResponse;
import com.movienow.org.dto.TheatreMovieResponse;
import com.movienow.org.entity.Movie;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.repository.CityMovieRepository;
import com.movienow.org.repository.CityRepository;
import com.movienow.org.repository.MovieRepository;
import com.movienow.org.repository.TheatreRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<MovieResponse> getMovies(Long cityId) {
        cityRepository.findById(cityId).orElseThrow(() -> new NotFoundException("City not found for given cityId"));
        return cityMovieRepository.getMovies(cityId);
    }

    /**
     * Used to get all movies which are available in theatre
     *
     * @param theatreId
     * @return
     */
    public List<TheatreMovieResponse> getMoviesForTheatre(Long theatreId) {
        theatreRepository.findById(theatreId).orElseThrow(() -> new NotFoundException("Theatre not found for given Id."));
        return cityMovieRepository.getAllMovies(theatreId);
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
