package com.movienow.org.dto;

import java.sql.Time;

public interface ScreenData {
    Long getScreenId();

    String getScreenName();

    Long getTheatreId();

    String getTheatreName();

    Long getMovieId();

    String getMovieName();

    Long getTimeSlotId();

    Time getStartTime();
}
