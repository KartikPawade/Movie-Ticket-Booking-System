package com.movienow.org.service;

import com.movienow.org.dto.*;
import com.movienow.org.entity.*;
import com.movienow.org.exception.BadRequestException;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Service
@Validated
public class MovieShowService {
    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private MovieShowRepository movieShowRepository;

    @Autowired
    private ScreenMovieRepository screenMovieRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private CityMovieRepository cityMovieRepository;
    @Autowired
    private TheatreRepository theatreRepository;
    @Autowired
    private CityRepository cityRepository;

    /**
     * Used to get Time Slot with respect to Date
     *
     * @param timeSlotDetails
     * @return
     */
    private static Map<LocalDate, List<ScreenTimeSlotResponse>> getTimeSlotResponseMap(List<ScreenTimeSlotDetails> timeSlotDetails) {
        Map<LocalDate, List<ScreenTimeSlotResponse>> timeSlotResponseMap = new LinkedHashMap<>();
        timeSlotDetails.forEach(screenTimeSlotDetails -> {
            var date = screenTimeSlotDetails.getDate().toLocalDate();
            List<ScreenTimeSlotResponse> list;
            ScreenTimeSlotResponse timeSlotResponse = new ScreenTimeSlotResponse();
            timeSlotResponse.setTimeSlotId(screenTimeSlotDetails.getTimeSlotId());
            timeSlotResponse.setSlotTime(screenTimeSlotDetails.getSlotTime());
            if (timeSlotResponseMap.containsKey(date)) {
                list = timeSlotResponseMap.get(date);
            } else {
                list = new ArrayList<>();
            }
            list.add(timeSlotResponse);
            timeSlotResponseMap.put(date, list);
        });
        return timeSlotResponseMap;
    }

    /**
     * Used to add Movie Show Details to Screen
     *
     * @param movieId
     * @param screenId
     * @param movieShowDetailsRequest
     * @return
     */
    @Transactional
    public String addMovieShowsToScreen(@NotNull(message = "Invalid") Long movieId, @NotNull(message = "Invalid") Long screenId, @Valid MovieShowDetailsRequest movieShowDetailsRequest) {
        // validation
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new NotFoundException("Movie not found for given Id."));
        Screen screen = screenRepository.findById(screenId).orElseThrow(() -> new NotFoundException("Screen not found for given Id."));

        Optional<Show> optionalScreenMovie = screenMovieRepository.findByMovieIdAndScreenId(movieId, screenId);
        if (optionalScreenMovie.isPresent()) throw new BadRequestException("Screen already has given Movie.");
        validateMovieShowRequest(movieShowDetailsRequest, movie.getMovieLengthInMinutes());

        List<Show> movieShows = getShowDetails(movieShowDetailsRequest, screen, movie);
        addMovieToCity(screen, movie);

        screenMovieRepository.saveAll(movieShows);
        return "Movie Show Details successfully added to Screen";
    }

    /**
     * Used to link Movie to City and Theatre
     *
     * @param screen
     * @param movie
     */
    private void addMovieToCity(Screen screen, Movie movie) {
        Theatre theatre = screen.getTheatre();
        City city = theatre.getCity();
        Long cityId = city.getId();

        Optional<CityMovie> optionalCityMovie = movie.getCityMovieList().stream().filter(cityMovie -> cityMovie.getCity().getId().equals(cityId)).findFirst();
        CityMovie cityMovie;
        if (optionalCityMovie.isEmpty()) {
            cityMovie = new CityMovie();
            cityMovie.setMovie(movie);
            cityMovie.setCity(city);

            cityMovieRepository.save(cityMovie);
        }
    }

    /**
     * Used to create link between Theatre and Movie
     *
     * @param theatre
     * @param cityMovie
     * @return
     */
    private TheatreMovie getTheatreMovie(Theatre theatre, CityMovie cityMovie) {
        TheatreMovie theatreMovie = new TheatreMovie();
        theatreMovie.setTheatre(theatre);
        theatreMovie.setCityMovie(cityMovie);
        return theatreMovie;
    }

    /**
     * Used to create Screen-Movie Link Record
     *
     * @param movieShowDetailsRequest
     * @param screen
     * @param movie
     * @return
     */
    private List<Show> getShowDetails(MovieShowDetailsRequest movieShowDetailsRequest, Screen screen, Movie movie) {

        List<Show> movieShows = new ArrayList<>();
        movieShowDetailsRequest.getTimeSlots().forEach((date, showTimeList) ->
                showTimeList.forEach(showTime -> {
                    Show showDetails = getShowDetails(movieShowDetailsRequest, screen, movie, date, showTime);
                    movieShows.add(showDetails);
                })
        );
        return movieShows;
    }

    /**
     * Used to get Show-Details
     *
     * @param movieShowDetailsRequest
     * @param screen
     * @param movie
     * @param key
     * @param showTime
     * @return
     */
    private static Show getShowDetails(MovieShowDetailsRequest movieShowDetailsRequest, Screen screen, Movie movie, Date key, Time showTime) {
        Show showDetails = new Show();
        showDetails.setScreen(screen);
        showDetails.setMovie(movie);
        showDetails.setSeatPrice(movieShowDetailsRequest.getSeatPrice());
        showDetails.setDate(key);
        showDetails.setShowTime(showTime);
        return showDetails;
    }

    /**
     * Used to Validate Time-Slots which are requested to be added
     *
     * @param movieShowDetailsRequest
     */
    private void validateMovieShowRequest(MovieShowDetailsRequest movieShowDetailsRequest, Short movieLengthInMinutes) {
        Map<Date, List<Time>> timeSLotsMap = movieShowDetailsRequest.getTimeSlots();

        timeSLotsMap.forEach((key, value) -> {
            if (value.isEmpty())
                throw new BadRequestException("Invalid request to add ZERO time-slots for given Date.");
            int noOfShowPerDay = value.size();
            Time prevShowTime = value.get(0);
            for (int i = 1; i < noOfShowPerDay; i++) {
                Time currShowTime = value.get(i);
                Duration duration = Duration.between(prevShowTime.toLocalTime(), currShowTime.toLocalTime());
                if (duration.toMinutes() < movieLengthInMinutes)
                    throw new BadRequestException("Invalid Time Slot Requested to be added to Screen as the Movie-times are overlapping fr Movie-length: " + movieLengthInMinutes + " minutes");
                prevShowTime = currShowTime;
            }
        });
    }

    /**
     * Used to get All Shows in City for a particular Movie
     *
     * @param cityId
     * @param movieId
     * @return
     */
    public Map<Long, TheatreDetailsDto> getAllShows(Long cityId, Long movieId) {
        cityRepository.findById(cityId).orElseThrow(() -> new NotFoundException("City not found for given Id."));
        movieRepository.findById(movieId).orElseThrow(() -> new NotFoundException("Movie not found for given Id."));
        List<MovieShowTheatreDetails> showDetails = movieShowRepository.getAllUpcomingShows(cityId, movieId);
        Map<Long, TheatreDetailsDto> showDetaislMap = new HashMap<>();
        showDetails.forEach(movieShowTheatreDetails -> {
            addTheatreShowDetails(movieShowTheatreDetails, showDetaislMap);
        });
        return showDetaislMap;
    }

    /**
     * Used to get All Shows in Theatre with available Movies
     *
     * @param theatreId
     * @return
     */
    public Map<Long, MovieDetailsDto> getAllShows(Long theatreId) {
        theatreRepository.findById(theatreId).orElseThrow(() -> new NotFoundException("Theatre not found for given Id."));
        List<MovieShowDetails> movieShowDetailsList = movieShowRepository.getAllUpcomingShows(theatreId);
        Map<Long, MovieDetailsDto> showDetailsMap = new HashMap<>();
        movieShowDetailsList.forEach(movieShowDetails -> {
            addMovieShowDetails(showDetailsMap, movieShowDetails);
        });
        return showDetailsMap;
    }

    /**
     * Used to add Movie Show Details
     *
     * @param showDetailsMap
     * @param movieShowDetails
     */
    private void addMovieShowDetails(Map<Long, MovieDetailsDto> showDetailsMap, MovieShowDetails movieShowDetails) {
        MovieDetailsDto movieDetails = showDetailsMap.getOrDefault(movieShowDetails.getMovieId(), getMovieDetails(movieShowDetails));
        ScreenDetails screenDetails = movieDetails.getScreens().getOrDefault(movieShowDetails.getScreenId(), getScreenDetails(movieShowDetails));
        List<ShowDetails> shows = screenDetails.getShows().getOrDefault(movieShowDetails.getDate().toLocalDate(), new ArrayList<>());
        shows.add(new ShowDetails(movieShowDetails.getShowId(), movieShowDetails.getShowTime()));
        screenDetails.getShows().put(movieShowDetails.getDate().toLocalDate(), shows);
        movieDetails.getScreens().put(movieShowDetails.getScreenId(), screenDetails);

        showDetailsMap.put(movieShowDetails.getMovieId(), movieDetails);
    }

    private MovieDetailsDto getMovieDetails(MovieShowDetails movieShowDetails) {
        MovieDetailsDto movieDetails = new MovieDetailsDto();
        movieDetails.setMovieName(movieShowDetails.getMovieName());
        return movieDetails;
    }

    /**
     * Used to add Theatre Show Details
     *
     * @param movieShowTheatreDetails
     * @param showDetailsMap
     */
    private void addTheatreShowDetails(MovieShowTheatreDetails movieShowTheatreDetails, Map<Long, TheatreDetailsDto> showDetailsMap) {
        TheatreDetailsDto theatreDetails = showDetailsMap.getOrDefault(movieShowTheatreDetails.getTheatreId(), getTheatreDetails(movieShowTheatreDetails));
        ScreenDetails screenDetails = theatreDetails.getScreens().getOrDefault(movieShowTheatreDetails.getScreenId(), getScreenDetails(movieShowTheatreDetails));
        List<ShowDetails> shows = screenDetails.getShows().getOrDefault(movieShowTheatreDetails.getDate().toLocalDate(), new ArrayList<>());
        shows.add(new ShowDetails(movieShowTheatreDetails.getShowId(), movieShowTheatreDetails.getShowTime()));
        screenDetails.getShows().put(movieShowTheatreDetails.getDate().toLocalDate(), shows);
        theatreDetails.getScreens().put(movieShowTheatreDetails.getScreenId(), screenDetails);
        showDetailsMap.put(movieShowTheatreDetails.getTheatreId(), theatreDetails);
    }

    /**
     * Used to get ScreenDetails with Shows
     *
     * @param movieShowTheatreDetails
     * @return
     */
    private ScreenDetails getScreenDetails(MovieShowTheatreDetails movieShowTheatreDetails) {
        ScreenDetails screenDetails = new ScreenDetails();
        screenDetails.setName(movieShowTheatreDetails.getScreenName());
        return screenDetails;
    }

    /**
     * Used to get ScreenDetails with Shows
     *
     * @param movieShowDetails
     * @return
     */
    private ScreenDetails getScreenDetails(MovieShowDetails movieShowDetails) {
        ScreenDetails screenDetails = new ScreenDetails();
        screenDetails.setName(movieShowDetails.getScreenName());
        return screenDetails;
    }

    /**
     * Used to get Theatre Details
     *
     * @param showDetaislResponse
     * @return
     */
    private TheatreDetailsDto getTheatreDetails(MovieShowTheatreDetails showDetaislResponse) {
        TheatreDetailsDto theatreDetailsDto = new TheatreDetailsDto();
        theatreDetailsDto.setName(showDetaislResponse.getTheatreName());
        theatreDetailsDto.setAddress(showDetaislResponse.getTheatreAddress());
        return theatreDetailsDto;
    }

//    /**
//     * Used to create TimeSlots for Screen and Movie
//     *
//     * @param show
//     * @param movieShowDetailsRequest
//     * @return
//     */
//    private List<Show> getScreenTimeSlots(Show show, MovieShowDetailsRequest movieShowDetailsRequest) {
//        List<Show> screenTimeSlots = new ArrayList<>();
//        movieShowDetailsRequest.getTimeSlots().forEach((key, value) ->
//                value.forEach(timeSlot -> {
//                    Show screenTimeSlot = new Show();
//                    screenTimeSlot.setDate(key);
//                    screenTimeSlot.setShowTime(timeSlot);
//                    screenTimeSlot.setShow(show);
//                    screenTimeSlots.add(screenTimeSlot);
//                })
//        );
//        if (screenTimeSlots.isEmpty())
//            throw new BadRequestException("Invalid request to add movie to Screen without Time-Slots");
//        return screenTimeSlots;
//    }
}
