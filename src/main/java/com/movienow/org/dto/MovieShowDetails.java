package com.movienow.org.dto;

import java.sql.Date;
import java.sql.Time;

public interface MovieShowDetails {

    Long getScreenId();

    String getScreenName();

    Long getMovieId();

    String getMovieName();

    Date getDate();

    Time getShowTime();

    Long getShowId();

    Double getSeatPrice();
}
