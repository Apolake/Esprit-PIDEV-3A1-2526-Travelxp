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
    public Booking saveBooking(Booking booking) {
        validateBooking(booking, booking.getId());
        recalcTotals(booking);
        return bookingRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Transactional
    public Booking createBooking(Property property, User guest, LocalDate checkIn,
                                  LocalDate checkOut, Integer numberOfGuests, String specialRequests) {
        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setGuest(guest);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setNumberOfGuests(numberOfGuests);
        booking.setSpecialRequests(specialRequests);
        booking.setStatus("CONFIRMED");

        validateBooking(booking, null);
        recalcTotals(booking);

        Booking savedBooking = bookingRepository.save(booking);

        gamificationService.awardExperiencePoints(guest, booking.getXpEarned(), "Booking completed");
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

    @Transactional
    public Booking updateBooking(Booking booking) {
        validateBooking(booking, booking.getId());
        recalcTotals(booking);
        return bookingRepository.save(booking);
    }

    private void recalcTotals(Booking booking) {
        long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        BigDecimal totalPrice = booking.getProperty().getPricePerNight().multiply(BigDecimal.valueOf(nights));
        booking.setTotalPrice(totalPrice);
        booking.setXpEarned(calculateBookingXP(totalPrice));
    }

    private void validateBooking(Booking booking, Long currentId) {
        if (booking.getProperty() == null) {
            throw new IllegalArgumentException("Property is required.");
        }
        if (booking.getProperty().getPricePerNight() == null || booking.getProperty().getPricePerNight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Property price must be positive.");
        }
        if (booking.getGuest() == null) {
            throw new IllegalArgumentException("Guest is required.");
        }
        if (booking.getCheckInDate() == null || booking.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required.");
        }
        if (!booking.getCheckInDate().isBefore(booking.getCheckOutDate())) {
            throw new IllegalArgumentException("Check-in date must be before check-out date.");
        }
        long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        if (nights <= 0) {
            throw new IllegalArgumentException("Stay must be at least one night.");
        }
        if (booking.getNumberOfGuests() == null || booking.getNumberOfGuests() <= 0) {
            throw new IllegalArgumentException("Number of guests must be positive.");
        }

        if (currentId == null) {
            if (bookingRepository.existsByPropertyAndGuestAndCheckInDateAndCheckOutDate(
                    booking.getProperty(), booking.getGuest(), booking.getCheckInDate(), booking.getCheckOutDate())) {
                throw new IllegalArgumentException("A booking for this property, guest, and dates already exists.");
            }
        } else {
            if (bookingRepository.existsByPropertyAndGuestAndCheckInDateAndCheckOutDateAndIdNot(
                    booking.getProperty(), booking.getGuest(), booking.getCheckInDate(), booking.getCheckOutDate(), currentId)) {
                throw new IllegalArgumentException("A booking for this property, guest, and dates already exists.");
            }
        }
    }
}
