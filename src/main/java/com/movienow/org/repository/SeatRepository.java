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
    @Query(value = "select s.id seatId, s.price from seat s " +
            "join time_slot_seat tss " +
            "on s.id = tss .seat_id " +
            "where tss.time_slot_id = :timeSlotId and tss.booked = 'N' "
            , nativeQuery = true)
    List<SeatResponse> getSeats(@Param(value = "timeSlotId") Long timeSlotId);

    List<Seat> findAllByScreenId(Long screenId);
}
