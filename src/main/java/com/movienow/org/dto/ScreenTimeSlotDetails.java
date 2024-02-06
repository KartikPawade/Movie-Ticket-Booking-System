package com.movienow.org.dto;

import java.sql.Date;
import java.sql.Time;

public interface ScreenTimeSlotDetails {
    Long getTimeSlotId();

    Date getDate();

    Time getSlotTime();
}
