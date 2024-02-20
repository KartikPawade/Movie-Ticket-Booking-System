package com.movienow.org.repository;

import com.movienow.org.entity.TimeSlotSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SeatTimeSlotRepository extends JpaRepository<TimeSlotSeat, Long> {
    @Query(value = "select tss.* from time_slot_seat tss " +
            "where tss.time_slot_id = :timeSlotId " +
            "and tss.seat_id in :seatIds ", nativeQuery = true)
    List<TimeSlotSeat> findAllTimeSlotSeatRecords(@Param(value = "timeSlotId") Long timeSlotId,@Param(value = "seatIds") Set<Long> seatIds);
}
