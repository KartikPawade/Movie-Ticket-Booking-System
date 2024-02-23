package com.movienow.org.service;

import com.movienow.org.dto.MovieTimeSlotRequest;
import com.movienow.org.dto.ScreenTimeSlotDetails;
import com.movienow.org.dto.ScreenTimeSlotResponse;
import com.movienow.org.entity.Movie;
import com.movienow.org.entity.Screen;
import com.movienow.org.entity.ScreenMovie;
import com.movienow.org.entity.ScreenTimeSlot;
import com.movienow.org.exception.BadRequestException;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.repository.MovieRepository;
import com.movienow.org.repository.ScreenMovieRepository;
import com.movienow.org.repository.ScreenRepository;
import com.movienow.org.repository.ScreenTimeSlotRepository;
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
public class TimeSlotService {
    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private ScreenTimeSlotRepository screenTimeSlotRepository;

    @Autowired
    private ScreenMovieRepository screenMovieRepository;
    @Autowired
    private MovieRepository movieRepository;

    /**
     * Used to get all Time Slots for available days for a screen
     *
     * @param screenId
     * @return
     */
    public Map<LocalDate, List<ScreenTimeSlotResponse>> getTimeSlots(Long screenId) {
        screenRepository.findById(screenId).orElseThrow(() -> new NotFoundException("Screen not found for given Id."));
        List<ScreenTimeSlotDetails> timeSlotDetails = screenTimeSlotRepository.getTimeSlots(screenId);

        return getTimeSlotResponseMap(timeSlotDetails);
    }

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
     * Used to add Movie to Screen and Add Time-SLots for that Movie
     *
     * @param movieId
     * @param screenId
     * @param movieTimeSlotRequest
     * @return
     */
    public String addMovieToScreenWithTimeSlots(@NotNull(message = "Invalid") Long movieId, Long screenId, @Valid MovieTimeSlotRequest movieTimeSlotRequest) {
        // validation
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new NotFoundException("Movie not found for given Id."));
        Screen screen = screenRepository.findById(screenId).orElseThrow(() -> new NotFoundException("Screen not found for given Id."));

        Optional<ScreenMovie> optionalScreenMovie = screenMovieRepository.findByMovieIdAndScreenId(movieId, screenId);
        if (optionalScreenMovie.isPresent()) throw new BadRequestException("Screen already has given Movie.");
        validateMovieTimeSLotRequest(movieTimeSlotRequest);

        ScreenMovie screenMovie = getScreenMovie(movieTimeSlotRequest, screen, movie);

        screenMovieRepository.save(screenMovie);
        return "Movie added to Screen with Time-Slots";
    }

    /**
     * Used to create Screen-Movie Link Record
     *
     * @param movieTimeSlotRequest
     * @param screen
     * @param movie
     * @return
     */
    private ScreenMovie getScreenMovie(MovieTimeSlotRequest movieTimeSlotRequest, Screen screen, Movie movie) {
        ScreenMovie screenMovie = new ScreenMovie();
        List<ScreenTimeSlot> screenTimeSlots = getScreenTimeSlots(screenMovie, movieTimeSlotRequest);

        screenMovie.setScreen(screen);
        screenMovie.setMovie(movie);
        screenMovie.setSeatPrice(movieTimeSlotRequest.getSeatPrice());
        screenMovie.setMovieLengthInMinutes(movieTimeSlotRequest.getMovieLengthInMinutes());
        screenMovie.getScreenTimeSlots().addAll(screenTimeSlots);
        return screenMovie;
    }

    /**
     * Used to Validate Time-Slots which are requested to be added
     *
     * @param movieTimeSlotRequest
     */
    private void validateMovieTimeSLotRequest(MovieTimeSlotRequest movieTimeSlotRequest) {
        Short movieLengthInMinutes = movieTimeSlotRequest.getMovieLengthInMinutes();
        Map<Date, List<Time>> timeSLotsMap = movieTimeSlotRequest.getTimeSlots();

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
     * Used to create TimeSlots for Screen and Movie
     *
     * @param screenMovie
     * @param movieTimeSlotRequest
     * @return
     */
    private List<ScreenTimeSlot> getScreenTimeSlots(ScreenMovie screenMovie, MovieTimeSlotRequest movieTimeSlotRequest) {
        List<ScreenTimeSlot> screenTimeSlots = new ArrayList<>();
        movieTimeSlotRequest.getTimeSlots().forEach((key, value) -> {
            value.forEach(timeSlot -> {
                ScreenTimeSlot screenTimeSlot = new ScreenTimeSlot();
                screenTimeSlot.setDate(key);
                screenTimeSlot.setStartTime(timeSlot);
                screenTimeSlot.setScreenMovie(screenMovie);
                screenTimeSlots.add(screenTimeSlot);
            });
        });
        if (screenTimeSlots.isEmpty())
            throw new BadRequestException("Invalid request to add movie to Screen without Time-Slots");
        return screenTimeSlots;
    }
}
