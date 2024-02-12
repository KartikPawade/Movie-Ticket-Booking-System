package com.movienow.org.repository;

import com.movienow.org.dto.SeatResponse;
import com.movienow.org.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query(value = "select tss.id as seatTimeSlotId, tss.booked, tss.seat_id as seatId, s.price  from time_slot_seat tss " +
            "join seat s  " +
            "on s.id = tss .seat_id " +
            "where tss.booked = 'N' and tss.time_slot_id = :timeSlotId "
            , nativeQuery = true)
    List<SeatResponse> getSeats(@Param(value = "timeSlotId") Long timeSlotId);
}
