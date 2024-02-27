package com.movienow.org.dto;

import java.sql.Date;
import java.sql.Time;

public interface MovieShowTheatreDetails {
    Long getShowId();
    Date getDate();
    Time getShowTime();
    Double getSeatPrice();

    Long getScreenId();
    String getScreenName();

    Long getTheatreId();
    String getTheatreName();
    String getTheatreAddress();
}
