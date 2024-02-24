package com.movienow.org.service;

import com.movienow.org.dto.MovieRequest;
import com.movienow.org.dto.MovieResponse;
import com.movienow.org.dto.TheatreMovieResponse;
import com.movienow.org.entity.Movie;
import com.movienow.org.entity.Theatre;
import com.movienow.org.entity.TheatreMovie;
import com.movienow.org.exception.BadRequestException;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Autowired
    private TheatreMovieRepository theatreMovieRepository;

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

    /**
     * Used to link Movie with multiple Theatres
     *
     * @param movieId    Movie id
     * @param theatreIds Theatre id List
     * @return Success message
     */
    public String addMovieToTheatres(Long movieId, List<Long> theatreIds) {
        //validation
//        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new NotFoundException("Movie not found for given Id"));
//        var theatres = theatreRepository.findAllById(theatreIds);
//        if (theatreIds.size() != theatres.size()) throw new BadRequestException("Invalid Theatre Ids");
//        List<TheatreMovie> records = theatreMovieRepository.findByMovieAndTheatresIn(movie, theatres);
//        if (!records.isEmpty())
//            throw new BadRequestException("Invalid Request, given Movie is already added to some of the given Theatres.");


        /**
         *
         * pagination -todo
         * caching - todo
         * messaging queue - async email service - done
         * temporary-record with expiry using redis - to handle concurrency - done
         * payment Gateway - stipe Gateway - done
         *
         *
         *
         *
         *
         * AUTHORITIES
         * ROLE : ADMIN authorities
         * 1.add theatre-manager
         * 2.add city to state of country
         * 3.add MOVIE
         *
         * ROLE : MANAGER(theatre-manager) authorities
         * 1.add movie with available timeSlot to the screens of a theatre of city
         * 2.add seats to the screens of a theatre, it will have price
         * 3.add screens to a theatre
         * 4.add theatre for a city: done
         *
         * ROLE : USER
         * FLOW-1:-
         * 1.get All movies for a city A
         * 2.get All Theatres of city A selected movie B
         *
         * FLOW-2:-
         * 1.get All Theatres for a city A
         * 2.get All Movies for the selected theatre B
         *
         * // common-flow
         * 3.get all screens of Theatre A with Movie B
         * 4.get all available Time-slots for selected Screen A
         * 5.get all available-only(non-booked) seats for a selected Time-slot
         * 6.book-seats(temporarily for 6-minutes) till checkout/payment.
         * 7.checkout/payment API,to process payments, persist booking details, and sending confirmation mail.
         */


        List<TheatreMovie> theatreMovies = new ArrayList<>();
//        theatres.forEach(theatre -> theatreMovies.add(getTheatreMovieLink(movie, theatre)));
        theatreMovieRepository.saveAll(theatreMovies);
        return "Movie added the Theatres successfully";
    }

    /**
     * Used to get TheatreMovie Link
     *
     * @param movie   Movie
     * @param theatre Theatre
     * @return
     */
//    private TheatreMovie getTheatreMovieLink(Movie movie, Theatre theatre) {
//        TheatreMovie theatreMovie = new TheatreMovie();
//        theatreMovie.setCityMovie(theatre.g);
//
//    }
}
