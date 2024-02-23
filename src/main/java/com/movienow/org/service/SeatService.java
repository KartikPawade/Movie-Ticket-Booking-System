package com.movienow.org.service;

import com.movienow.org.dto.BookingResponse;
import com.movienow.org.dto.EmailDetails;
import com.movienow.org.dto.SeatResponse;
import com.movienow.org.dto.UserBookingDetails;
import com.movienow.org.entity.*;
import com.movienow.org.exception.BadRequestException;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.payment.PaymentGatewayService;
import com.movienow.org.payment.PaymentRequest;
import com.movienow.org.payment.PaymentResponse;
import com.movienow.org.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

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
    private ScreenRepository screenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${redis.key.expiryTimeInMinutes}")
    private String redisKeyExpiryTime;

    @Value("${rabbitmq.email.exchange.name}")
    private String emailExchangeName;
    @Value("${rabbitmq.email.routing.key}")
    private String emailRoutingKey;


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
     * Used to get seats for a Screen in Theatre
     * @param theatreId
     * @param screenId
     * @return
     */
    public List<Seat> getSeats(Long theatreId, Long screenId) {
        System.out.println("ksjd");
        screenRepository.findByIdAndTheatreId(screenId, theatreId).orElseThrow(() -> new BadRequestException("ScreenId does not belong to theatreId."));

        return seatRepository.findAllByScreenId(screenId);
    }

    /**
     * Used to block the seats temporarily for some time, to be booked by a User
     *
     * @param timeSlotId
     * @return
     */
    public Object bookSeats(Long timeSlotId, List<Long> seatIds) {
        List<BookingResponse> bookingResponses = screenTimeSlotRepository.getSeats(timeSlotId, seatIds);
        if (bookingResponses.size() != seatIds.size())
            throw new BadRequestException("Sorry, some of the selected seats have been booked by this time.");

        for (Long seatId : seatIds) {
            if (redisTemplate.opsForHash().get(seatId.toString(), seatId) != null) {
                throw new BadRequestException("Requested seats are booked by someone.");
            }
        }

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = userDetails.getUsername();
        List<UserBookingDetails> userBookingDetailsList = new ArrayList<>();
        for (BookingResponse bookingResponse : bookingResponses) {
            UserBookingDetails userBookingDetails = getUserBokkingDetails(bookingResponse);
            redisTemplate.opsForHash().put(bookingResponse.getSeatId().toString(), bookingResponse.getSeatId(), userBookingDetails);
            redisTemplate.expire(bookingResponse.getSeatId().toString(), Duration.ofMinutes(Integer.parseInt(redisKeyExpiryTime)));
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
        userBookingDetails.setSeatId(bookingResponse.getSeatId());
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
    public String doPayment(Long timeSlotId, PaymentRequest paymentRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = userDetails.getUsername();
        Double totalPrice = (double) 0;
        Set<Long> seatIds = paymentRequest.getSeatIds();
        try {
            List<UserBookingDetails> list = (List<UserBookingDetails>) redisTemplate.opsForHash().get(userName, userName);
            if (list == null) throw new BadRequestException("Payment window Timeout.");

            for (UserBookingDetails userBookingDetails : list) {
                if (!seatIds.contains(userBookingDetails.getSeatId()))
                    throw new BadRequestException("Invalid Seat Bookings Requested.");
                totalPrice += userBookingDetails.getPrice();
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

        PaymentResponse paymentResponse = paymentGatewayService.paymentGateway(paymentRequest, totalPrice);
        if (paymentResponse != null && paymentResponse.getChargeId() != null) {
            savePaymentDetails(paymentResponse, userDetails, timeSlotId, seatIds, totalPrice);
        }
        EmailDetails emailDetails = getEmailDetails(userName, totalPrice, seatIds);
        rabbitTemplate.convertAndSend(emailExchangeName, emailRoutingKey, emailDetails);
        return "Payment Successful";
    }


    private EmailDetails getEmailDetails(String userName, Double totalPrice, Set<Long> seatIds) {
        return new EmailDetails(userName, totalPrice, new ArrayList<>(seatIds));
    }


    /**
     * Used to persists Payment and Booking details, after successfully charging the client
     *
     * @param paymentResponse
     * @param userDetails
     * @param timeSlotId
     * @param seatIds
     * @param totalPrice
     */
    private void savePaymentDetails(PaymentResponse paymentResponse, UserDetails userDetails, Long timeSlotId, Set<Long> seatIds, Double totalPrice) {
        List<BookingDetails> bookingDetailsList = new ArrayList<>();
        AppUser user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new NotFoundException("User not found for given Id."));
        List<TimeSlotSeat> timeSlotSeats = seatTimeSlotRepository.findAllTimeSlotSeatRecords(timeSlotId, seatIds);
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