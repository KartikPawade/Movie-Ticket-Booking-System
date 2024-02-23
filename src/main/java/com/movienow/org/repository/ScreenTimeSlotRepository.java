package com.movienow.org.repository;

import com.movienow.org.dto.BookingResponse;
import com.movienow.org.dto.ScreenTimeSlotDetails;
import com.movienow.org.entity.ScreenTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenTimeSlotRepository extends JpaRepository<ScreenTimeSlot, Long> {
    @Query(value = "select id as timeSlotId, date, start_time as slotTime from screen_time_slot sts " +
            "where screen_movie_id = :screenMovieId and date = current_date and start_time > current_time " +
            "union " +
            "select id as timeSlotId, date, start_time as slotTime  from screen_time_slot sts " +
            "where screen_movie_id = :screenMovieId and date > current_date " +
            "order by date , slotTime "
            , nativeQuery = true)
    List<ScreenTimeSlotDetails> getTimeSlots(@Param(value = "screenMovieId") Long screenMovieId);

    @Query(value = "select  s.id as seatId, s.price " +
            "from seat s " +
            "join time_slot_seat tss " +
            "on s.id = tss.seat_id  " +
            "join screen_time_slot sts " +
            "on sts.id = tss.time_slot_id " +
            "and tss.booked = 'N'  " +
            "and tss.time_slot_id = :timeSlotId " +
            "and s.id in :seatTimeSlotIds "
            , nativeQuery = true)
    List<BookingResponse> getSeats(@Param(value = "timeSlotId") Long timeSlotId, @Param(value = "seatTimeSlotIds") List<Long> seatTimeSlotIds);
}
