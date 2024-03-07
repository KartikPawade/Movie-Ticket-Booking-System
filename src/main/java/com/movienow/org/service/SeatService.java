package com.movienow.org.service;

import com.movienow.org.dto.*;
import com.movienow.org.entity.*;
import com.movienow.org.exception.BadRequestException;
import com.movienow.org.exception.NotFoundException;
import com.movienow.org.payment.PaymentGatewayService;
import com.movienow.org.payment.PaymentRequest;
import com.movienow.org.payment.PaymentResponse;
import com.movienow.org.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SeatService {
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private MovieShowRepository movieShowRepository;
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    @Autowired
    private PaymentDetailsRepository paymentDetailsRepository;
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

    @Value("${rabbitmq.payment.details.unsaved.exchange.name}")
    private String unsavedPaymentDetailsExchange;
    @Value("${rabbitmq.payment.details.unsaved.routing.key}")
    private String unsavedPaymentDetailsRoutingKey;


    /**
     * Used to get all available seats fot timeSlot for a screen
     *
     * @param showId
     * @return
     */
    public List<SeatResponse> getSeats(Long showId) {
        movieShowRepository.findById(showId).orElseThrow(() -> new NotFoundException("Movie Time Slot does not exist for given Id."));
        return seatRepository.getAvailableSeats(showId);
    }


    /**
     * Used to get seats for a Screen in Theatre
     *
     * @param theatreId
     * @param screenId
     * @return
     */
    public List<Seat> getSeats(Long theatreId, Long screenId) {
        screenRepository.findByIdAndTheatreId(screenId, theatreId).orElseThrow(() -> new BadRequestException("ScreenId does not belong to theatreId."));
        return seatRepository.findAllByScreenId(screenId);
    }

    /**
     * Used to block the seats temporarily for some time, to be booked by a User
     *
     * @param showId
     * @return
     */
    public String bookSeats(Long showId, List<Long> seatIds) {
        validateSeatsAndShow(seatIds, showId);

        List<BookingResponse> bookingResponses = seatRepository.getSeats(showId, seatIds);
        if (bookingResponses.size() != seatIds.size()) {
            throw new BadRequestException("Sorry, some of the selected seats have been booked by this time.");
        }
        validateIfSeatsAreBookedTemporarily(seatIds);

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
     * Used to check if request seats are already in Booking process by other user
     *
     * @param seatIds
     */
    private void validateIfSeatsAreBookedTemporarily(List<Long> seatIds) {
        for (Long seatId : seatIds) {
            if (redisTemplate.opsForHash().get(seatId.toString(), seatId) != null) {
                throw new BadRequestException("Some of requested seats are being booked by someone else.");
            }
        }
    }

    /**
     * Used to validate SeatIds and Show
     *
     * @param seatIds
     * @param showId
     */
    private void validateSeatsAndShow(List<Long> seatIds, Long showId) {
        movieShowRepository.findById(showId).orElseThrow(() -> new NotFoundException("Show not found for given Id."));
        List<Long> existingSeatIds = seatRepository.getAllExistingSeatIds(seatIds);
        if (existingSeatIds.size() != seatIds.size()) {
            throw new BadRequestException("Invalid seats requested for Booking.");
        }
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
        userBookingDetails.setPrice(bookingResponse.getSeatPrice());
        return userBookingDetails;
    }

    /**
     * Used to perform the Payment function based on the payment Type selected by User
     *
     * @param paymentRequest
     * @return
     */
    public String checkout(Long showId, PaymentRequest paymentRequest) {
        Show timeSlot = movieShowRepository.findById(showId).orElseThrow(() -> new NotFoundException("Movie Show not found with given Id."));
        List<Seat> seats = seatRepository.findAllById(paymentRequest.getSeatIds());
        if (seats.size() != paymentRequest.getSeatIds().size()) {
            throw new BadRequestException("Invalid Seat Ids for Checkout request.");
        }

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = userDetails.getUsername();
        Double totalPrice = (double) 0;
        Set<Long> seatIds = paymentRequest.getSeatIds();
        try {
            List<UserBookingDetails> list = getBookingDetailsForUser(userName);
            if (list == null) throw new BadRequestException("Sorry, Invalid request");
            for (UserBookingDetails userBookingDetails : list) {
                if (!seatIds.contains(userBookingDetails.getSeatId()))
                    throw new BadRequestException("Invalid Seat Bookings Requested.");
                totalPrice += userBookingDetails.getPrice();
            }
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
        // commenting out payment service call, as test-STRIPE-payment-API is not allowed in INDIA
//        PaymentResponse paymentResponse = paymentGatewayService.paymentGateway(paymentRequest, totalPrice);
        PaymentResponse paymentResponse = getPaymentResponseWithRandomChargeId();

        removeTemporaryBookingDetailsForUser(userName);
        if (paymentResponse != null && paymentResponse.getChargeId() != null) {
            try {
                savePaymentDetails(paymentResponse, userDetails, timeSlot, seats, totalPrice);
            } catch (Exception e) {
                log.error("Unsaved Payment::" + e.getMessage());
                rabbitTemplate.convertAndSend(unsavedPaymentDetailsExchange, unsavedPaymentDetailsRoutingKey, getPaymentDetails(userDetails.getUsername(), totalPrice, paymentRequest.getSeatIds(), showId, paymentResponse.getChargeId()));
            }
            EmailDetails emailDetails = getEmailDetails(userName, totalPrice, seatIds);
            rabbitTemplate.convertAndSend(emailExchangeName, emailRoutingKey, emailDetails);
            return "Payment Successful";
        }
        return "Payment was unsuccessful.";
    }

    /**
     * Used to get Payment Response with random chargeId
     *
     * @return
     */
    private PaymentResponse getPaymentResponseWithRandomChargeId() {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setChargeId(getSaltString());
        return paymentResponse;
    }

    /**
     * Used to generate random String, to be used as chargeId
     *
     * @return
     */
    protected static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    /**
     * Used to remove Booking details
     *
     * @param userName
     */
    private void removeTemporaryBookingDetailsForUser(String userName) {
        redisTemplate.opsForHash().delete(userName, userName);
    }

    /**
     * Used to Fetch Seats booked temporarily by User
     *
     * @param userName
     * @return
     */
    private List<UserBookingDetails> getBookingDetailsForUser(String userName) {
        return (List<UserBookingDetails>) redisTemplate.opsForHash().get(userName, userName);
    }

    /**
     * Used to get Payment Details
     *
     * @param username
     * @param totalPrice
     * @param seatIds
     * @param showId
     * @param chargeId
     * @return
     */
    private PaymentDetailsDto getPaymentDetails(String username, Double totalPrice, Set<Long> seatIds, Long showId, String chargeId) {
        PaymentDetailsDto paymentDetails = new PaymentDetailsDto();
        paymentDetails.setUserEmail(username);
        paymentDetails.setShowId(showId);
        paymentDetails.getSeatIds().addAll(seatIds);
        paymentDetails.setTotalPrice(totalPrice);
        paymentDetails.setChargeId(chargeId);
        return paymentDetails;
    }


    /**
     * Used to get Email Details
     *
     * @param userName
     * @param totalPrice
     * @param seatIds
     * @return
     */
    private EmailDetails getEmailDetails(String userName, Double totalPrice, Set<Long> seatIds) {
        return new EmailDetails(userName, totalPrice, new ArrayList<>(seatIds));
    }


    /**
     * Used to persists Payment and Booking details, after successfully charging the client
     *
     * @param paymentResponse
     * @param userDetails
     * @param show
     * @param seats
     * @param totalPrice
     */
    @Transactional
    private void savePaymentDetails(PaymentResponse paymentResponse, UserDetails userDetails, Show show, List<Seat> seats, Double totalPrice) {
        List<BookingDetails> bookingDetailsList = new ArrayList<>();
        AppUser user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new NotFoundException("User not found for given Id."));

        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setChargeId(paymentResponse.getChargeId());
        paymentDetails.setTotalBookingPrice(totalPrice);
        paymentDetails.setUser(user);

        seats.forEach(seat -> {
            paymentDetails.getBookingDetails().add(getTimeSlotSeat(show, seat, paymentDetails));
        });
        paymentDetailsRepository.save(paymentDetails);
    }

    /**
     * Used to create link for Movie-time and Seat
     *
     * @param show
     * @param seat
     * @param paymentDetails
     * @return
     */
    private static BookingDetails getTimeSlotSeat(Show show, Seat seat, PaymentDetails paymentDetails) {
        BookingDetails bookingDetails = new BookingDetails();
        bookingDetails.setSeat(seat);
        bookingDetails.setShow(show);
        bookingDetails.setPaymentDetails(paymentDetails);
        return bookingDetails;
    }

    /**
     * Used to add Seats to the Screen
     *
     * @param screenId
     * @param startSeatNumber
     * @param endSeatNumber
     * @return
     */
    public String addSeats(Long screenId, Short startSeatNumber, Short endSeatNumber) {
        if (endSeatNumber < startSeatNumber) throw new BadRequestException("Invalid Seats requested");
        List<Short> seatIds = getSeatNumbers(startSeatNumber, endSeatNumber);

        Screen screen = screenRepository.findById(screenId).orElseThrow(() -> new NotFoundException("Screen not found for given Id."));
        List<Seat> existingSeats = seatRepository.findAllByScreenIdAndSeatNumberIn(screenId, seatIds);
        if (!existingSeats.isEmpty()) {
            throw new BadRequestException("Some of the requested Seats are already present in the Screen.");
        }
        List<Seat> seats = seatIds.stream().map(seatNumber -> {
            Seat seat = new Seat();
            seat.setSeatNumber(seatNumber);
            seat.setScreen(screen);
            return seat;
        }).toList();

        seatRepository.saveAll(seats);
        return "Seats added Successfully";
    }

    private List<Short> getSeatNumbers(Short startSeatNumber, Short endSeatNumber) {
        List<Short> seatNumbers = new ArrayList<>();
        for (Short i = startSeatNumber; i <= endSeatNumber; i++) {
            seatNumbers.add(i);
        }
        return seatNumbers;
    }
}