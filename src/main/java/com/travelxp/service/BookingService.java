package com.travelxp.service;

import com.travelxp.model.Booking;
import com.travelxp.model.Property;
import com.travelxp.model.User;
import com.travelxp.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private GamificationService gamificationService;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getBookingsByGuest(User guest) {
        return bookingRepository.findByGuestOrderByCreatedAtDesc(guest);
    }

    public List<Booking> getBookingsByProperty(Property property) {
        return bookingRepository.findByProperty(property);
    }

    @Transactional
    public Booking createBooking(Property property, User guest, LocalDate checkIn,
                                  LocalDate checkOut, Integer numberOfGuests, String specialRequests) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal totalPrice = property.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setGuest(guest);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setNumberOfGuests(numberOfGuests);
        booking.setTotalPrice(totalPrice);
        booking.setSpecialRequests(specialRequests);
        booking.setStatus("CONFIRMED");

        int xpEarned = calculateBookingXP(totalPrice);
        booking.setXpEarned(xpEarned);

        Booking savedBooking = bookingRepository.save(booking);

        gamificationService.awardExperiencePoints(guest, xpEarned, "Booking completed");
        gamificationService.checkBookingAchievements(guest);

        return savedBooking;
    }

    @Transactional
    public Booking updateBookingStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    private int calculateBookingXP(BigDecimal totalPrice) {
        int xp = totalPrice.divide(BigDecimal.TEN).intValue();
        return Math.max(xp, 20);
    }
}
