package com.movienow.org.service;

import com.movienow.org.dto.BookingResponse;
import com.movienow.org.dto.SeatResponse;
import com.movienow.org.dto.UserBookingDetails;
import com.movienow.org.exception.BadRequestException;
import com.movienow.org.repository.ScreenTimeSlotRepository;
import com.movienow.org.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeatService {
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ScreenTimeSlotRepository screenTimeSlotRepository;


    /**
     * Used to get all available seats fot timeSlot for a screen
     *
     * @param timeSlotId
     * @return
     */
    public List<SeatResponse> getSeats(Long timeSlotId) {
        return seatRepository.getSeats(timeSlotId);
    }

    /**
     * Used to book seats
     *
     * @param timeSlotId
     * @return
     */
    public Object bookSeats(Long timeSlotId, List<Long> seatTimeSlotIds) {
        List<BookingResponse> bookingResponses = screenTimeSlotRepository.getSeats(timeSlotId, seatTimeSlotIds);
        System.out.println(bookingResponses);
        if (bookingResponses.size() != seatTimeSlotIds.size()) throw new BadRequestException("Invalid Request.");
/////////////////


        //check if present
        //key , multi field

        for(Long seatTimeSlotId:seatTimeSlotIds){
            if (redisTemplate.opsForHash().get(seatTimeSlotId.toString(),seatTimeSlotId) != null){
                throw new BadRequestException("Requested seats are booked by someone.");
            }
        }

////////////////
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = userDetails.getUsername();
        List<UserBookingDetails> userBookingDetailsList = new ArrayList<>();
        for (BookingResponse bookingResponse : bookingResponses) {
            UserBookingDetails userBookingDetails = getUserBokkingDetails(bookingResponse);
            redisTemplate.opsForHash().put(bookingResponse.getSeatTimeSlotId().toString(), bookingResponse.getSeatTimeSlotId(), userBookingDetails);
            redisTemplate.expire(bookingResponse.getSeatTimeSlotId().toString(), Duration.ofMinutes(6L));
            userBookingDetailsList.add(getUserBokkingDetails(bookingResponse));
        }
        redisTemplate.opsForHash().put(userName, userName, userBookingDetailsList);
        redisTemplate.expire(userName,Duration.ofMinutes(6L));

//        redisTemplate.opsForHash().put(key, timeSlotId,seatTimeSlotId);
//        System.out.println(redisTemplate.opsForHash().get(key, timeSlotId));


        // check if given seats are available in DB and Redis
        // check if they are temporarily booked
        /**
         * if not booked, then block those temporarily for 6 mins. with price , seatIds, userId
         */
        return "Seat Blocked temporarily";
    }

    /**
     * Used to get User Booking details
     *
     * @param bookingResponse
     * @return
     */
    private UserBookingDetails getUserBokkingDetails(BookingResponse bookingResponse) {
        UserBookingDetails userBookingDetails = new UserBookingDetails();
        userBookingDetails.setTimeSlotSeatId(bookingResponse.getSeatTimeSlotId());
        userBookingDetails.setPrice(bookingResponse.getPrice());
        return userBookingDetails;
    }

    public Object doPayment() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = userDetails.getUsername();
        Double totalPrice = (double) 0;
        List<Long> timeSlotSeatIds = new ArrayList<>();
        try {
            List<UserBookingDetails> list = (List<UserBookingDetails>) redisTemplate.opsForHash().get(userName, userName);
            if (list == null) throw new BadRequestException("Payment window Timeout.");

            for (UserBookingDetails userBookingDetails : list) {
                totalPrice += userBookingDetails.getPrice();
                timeSlotSeatIds.add(userBookingDetails.getTimeSlotSeatId());
            }
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }

        // do payment
        // the persist db

        return "Payment Successful";


    }

    /**
     * Transaction API :
     *
     * to check whether our booked tickets time is expired
     * if expired, throw ERROR
     *
     * if not,
     * get the data, and tickets
     * hit payment API:
     * if Payment is successful
     * persist data in DB
     *
     * if un-successful remove blocks and do not persist. throw error.
     *
     *
     */


}