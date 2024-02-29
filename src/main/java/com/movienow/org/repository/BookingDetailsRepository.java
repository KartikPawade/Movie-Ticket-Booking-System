package com.movienow.org.repository;

import com.movienow.org.dto.BookingDetailsDto;
import com.movienow.org.entity.BookingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDetailsRepository extends JpaRepository<BookingDetails, Long> {

    @Query(value = "select  pd.total_booking_price as bookingPrice,bd.seat_id as seatId, sh.\"date\", sh.show_time as showTime, m.\"name\" as movieName, m.movie_length_in_minutes as movieLengthInMinutes, scr.name as screen, pd.charge_id as chargeId " +
            " from booking_details bd \n" +
            "join payment_details pd \n" +
            "on bd.payment_details_id = pd.id \n" +
            "join show sh \n" +
            "on  bd.show_id = sh.id \n" +
            "join screen scr \n" +
            "on scr.id = sh.screen_id \n" +
            "join movie m \n" +
            "on m.id = sh.movie_id \n" +
            "where pd.user_id = :username \n" +
            "and ((sh.date = current_date and sh.show_time > current_time) or (sh.date > current_date)) " +
            "order by sh.date, sh.show_time",
            nativeQuery = true)
    List<BookingDetailsDto> getUpcomingBookings(@Param(value = "username") String username);
}
