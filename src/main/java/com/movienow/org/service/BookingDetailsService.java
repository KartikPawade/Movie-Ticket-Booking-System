package com.movienow.org.service;

import com.movienow.org.dto.BookingDetailsDto;
import com.movienow.org.dto.BookingDetailsResponse;
import com.movienow.org.repository.BookingDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingDetailsService {
    @Autowired
    private BookingDetailsRepository bookingDetailsRepository;

    /**
     * Used to get Upcoming Booking Details for a User
     *
     * @return
     */
    public Map<String, BookingDetailsResponse> getUpcomingBookings() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<BookingDetailsDto> bookingDetailsList = bookingDetailsRepository.getUpcomingBookings(userDetails.getUsername());
        Map<String, BookingDetailsResponse> bookingDetailsMap = new HashMap<>();
        bookingDetailsList.forEach(bookingDetailsDto -> {
            BookingDetailsResponse bookingDetailsResponse = bookingDetailsMap.getOrDefault(bookingDetailsDto.getChargeId(), getBookingDetailsResponse(bookingDetailsDto));
            bookingDetailsResponse.getSeatIds().add(bookingDetailsDto.getSeatId());
            bookingDetailsMap.put(bookingDetailsDto.getChargeId(),bookingDetailsResponse);
        });
        return bookingDetailsMap;
    }

    /**
     * Used to get Booking Details Response
     *
     * @param bookingDetailsDto
     * @return
     */
    private BookingDetailsResponse getBookingDetailsResponse(BookingDetailsDto bookingDetailsDto) {
        BookingDetailsResponse bookingDetailsResponse = new BookingDetailsResponse();
        bookingDetailsResponse.setBookingPrice(bookingDetailsDto.getBookingPrice());
        bookingDetailsResponse.setDate(bookingDetailsDto.getDate().toLocalDate());
        bookingDetailsResponse.setShowTime(bookingDetailsDto.getShowTime());
        bookingDetailsResponse.setMovieName(bookingDetailsDto.getMovieName());
        bookingDetailsResponse.setMovieLengthInMinutes(bookingDetailsDto.getMovieLengthInMinutes());
        bookingDetailsResponse.setScreen(bookingDetailsDto.getScreen());
        return bookingDetailsResponse;
    }
}
