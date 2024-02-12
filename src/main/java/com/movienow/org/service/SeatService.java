package com.movienow.org.service;

import com.movienow.org.dto.BookingResponse;
import com.movienow.org.dto.SeatResponse;
import com.movienow.org.dto.UserBookingDetails;
import com.movienow.org.entity.AppUser;
import com.movienow.org.entity.BookingDetails;
import com.movienow.org.entity.ScreenTimeSlot;
import com.movienow.org.entity.TimeSlotSeat;
import com.movienow.org.exception.BadRequestException;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.payment.PaymentGatewayService;
import com.movienow.org.payment.PaymentRequest;
import com.movienow.org.payment.PaymentResponse;
import com.movienow.org.repository.BookingDetailsRepository;
import com.movienow.org.repository.ScreenTimeSlotRepository;
import com.movienow.org.repository.SeatRepository;
import com.movienow.org.repository.SeatTimeSlotRepository;
import com.movienow.org.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Autowired
    private SeatTimeSlotRepository seatTimeSlotRepository;
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    @Autowired
    private BookingDetailsRepository bookingDetailsRepository;
    @Autowired
    private UserRepository userRepository;

    @Value("${redis.key.expiryTimeInMinutes}")
    private String redisKeyExpiryTime;


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
     * Used to block the seats temporarily for some time, to be booked by a User
     *
     * @param timeSlotId
     * @return
     */
    public Object bookSeats(Long timeSlotId, List<Long> seatTimeSlotIds) {
        List<BookingResponse> bookingResponses = screenTimeSlotRepository.getSeats(timeSlotId, seatTimeSlotIds);
        if (bookingResponses.size() != seatTimeSlotIds.size()) throw new BadRequestException("Invalid Request.");

        for (Long seatTimeSlotId : seatTimeSlotIds) {
            if (redisTemplate.opsForHash().get(seatTimeSlotId.toString(), seatTimeSlotId) != null) {
                throw new BadRequestException("Requested seats are booked by someone.");
            }
        }

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = userDetails.getUsername();
        List<UserBookingDetails> userBookingDetailsList = new ArrayList<>();
        for (BookingResponse bookingResponse : bookingResponses) {
            UserBookingDetails userBookingDetails = getUserBokkingDetails(bookingResponse);
            redisTemplate.opsForHash().put(bookingResponse.getSeatTimeSlotId().toString(), bookingResponse.getSeatTimeSlotId(), userBookingDetails);
            redisTemplate.expire(bookingResponse.getSeatTimeSlotId().toString(), Duration.ofMinutes(Integer.parseInt(redisKeyExpiryTime)));
            userBookingDetailsList.add(getUserBokkingDetails(bookingResponse));
        }
        redisTemplate.opsForHash().put(userName, userName, userBookingDetailsList);
        redisTemplate.expire(userName, Duration.ofMinutes(Integer.parseInt(redisKeyExpiryTime)));

        return "Seats Booked temporarily";
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

    /**
     * Used to perform the Payment function based on the payment Type selected by User
     *
     * @param paymentRequest
     * @return
     */
    @Transactional
    public String doPayment(PaymentRequest paymentRequest) {
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
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
        // do payment
        PaymentResponse paymentResponse = paymentGatewayService.paymentGateway(paymentRequest, totalPrice);
        if (paymentResponse != null && paymentResponse.getChargeId() != null) {
            savePaymentDetails(paymentResponse, userDetails, timeSlotSeatIds, totalPrice);
        }
        return "Payment Successful";
    }


    /**
     * Used to persists Payment and Booking details, after successfully charging the client
     *
     * @param paymentResponse
     * @param userDetails
     * @param timeSlotSeatIds
     * @param totalPrice
     */
    private void savePaymentDetails(PaymentResponse paymentResponse, UserDetails userDetails, List<Long> timeSlotSeatIds, Double totalPrice) {
        List<BookingDetails> bookingDetailsList = new ArrayList<>();
        AppUser user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new NotFoundException("User not found for given Id."));
        List<TimeSlotSeat> timeSlotSeats = seatTimeSlotRepository.findAllById(timeSlotSeatIds);
        ScreenTimeSlot timeSlot = new ScreenTimeSlot();
        if (!timeSlotSeats.isEmpty()) timeSlot = timeSlotSeats.get(0).getTimeSlot();
        String chargeId = paymentResponse.getChargeId();
        ScreenTimeSlot finalTimeSlot = timeSlot;
        timeSlotSeats.forEach(timeSlotSeat -> {
            timeSlotSeat.setBooked('Y');
            BookingDetails bookingDetails = new BookingDetails();
            bookingDetails.setUser(user);
            bookingDetails.setTotalBookingPrice(totalPrice);
            bookingDetails.getSeatTimeSlots().add(timeSlotSeat);
            bookingDetails.setTimeSlot(finalTimeSlot);
            bookingDetails.setChargeId(chargeId);
            bookingDetailsList.add(bookingDetails);
        });
        bookingDetailsRepository.saveAll(bookingDetailsList);
    }

}