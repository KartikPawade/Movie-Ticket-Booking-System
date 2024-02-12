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
            "where screen_id = :screenId and date = current_date and start_time > current_time " +
            "union " +
            "select id as timeSlotId, date, start_time as slotTime  from screen_time_slot sts " +
            "where screen_id = :screenId and date > current_date " +
            "order by date , slotTime "
            , nativeQuery = true)
    List<ScreenTimeSlotDetails> getTimeSlots(@Param(value = "screenId") Long screenId);

    @Query(value = "select  tss.id as seatTimeSlotId,s.price " +
            "from time_slot_seat tss  " +
            "join screen_time_slot sts   " +
            "on sts.id = tss.time_slot_id  " +
            "join seat s " +
            "on s.id = tss.seat_id  " +
            "where sts.date > current_date " +
            "and tss.booked = 'N'  " +
            "and tss.time_slot_id = :timeSlotId " +
            "and tss.id in :seatTimeSlotIds " +
            "union " +
            "select  tss.id as seatTimeSlotId,s.price  " +
            "from time_slot_seat tss  " +
            "join screen_time_slot sts  " +
            "on sts.id = tss.time_slot_id  " +
            "join seat s  " +
            "on s.id = tss.seat_id  " +
            "where sts.date = current_date  " +
            "and sts.start_time >= current_time " +
            "and tss.booked = 'N'  " +
            "and tss.time_slot_id = :timeSlotId " +
            "and tss.id in :seatTimeSlotIds "
            , nativeQuery = true)
    List<BookingResponse> getSeats(@Param(value = "timeSlotId") Long timeSlotId, @Param(value = "seatTimeSlotIds") List<Long> seatTimeSlotIds);
}
