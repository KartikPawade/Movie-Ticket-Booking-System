package com.movienow.org.repository;

import com.movienow.org.dto.BookingResponse;
import com.movienow.org.dto.SeatResponse;
import com.movienow.org.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query(value = "select s.id as seatId, s.seat_number as seatNumber, sho.seat_price as seatPrice from seat s  " +
            "join \"show\" sho " +
            "on sho.screen_id  = s.screen_id  " +
            "where sho.id = :timeSlotId " +
            "and s.id not in (select bd.seat_id  from booking_details bd  where bd.show_id  = :timeSlotId )" +
            "order by s.seat_number "
            , nativeQuery = true)
    List<SeatResponse> getSeats(@Param(value = "timeSlotId") Long timeSlotId);

    @Query(value = "select s.id as seatId , sm.seat_price as seatPrice from seat s " +
            "join screen_movie sm " +
            "on sm.screen_id = s.screen_id " +
            "join screen_time_slot sts " +
            "on sts.screen_movie_id = sm.id and sts.id = :timeSlotId " +
            "and s.id in :seatIds and s.id not in (select tss.seat_id from time_slot_seat tss where tss.time_slot_id = :timeSlotId )"
            , nativeQuery = true)
    List<BookingResponse> getSeats(@Param(value = "timeSlotId") Long timeSlotId, @Param(value = "seatIds") List<Long> seatIds);

    List<Seat> findAllByScreenId(Long screenId);

    List<Seat> findAllByScreenIdAndSeatNumberIn(Long screenId, List<Short> seatIds);
}
