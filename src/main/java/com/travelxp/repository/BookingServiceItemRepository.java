package com.travelxp.repository;

import com.travelxp.model.Booking;
import com.travelxp.model.BookingServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingServiceItemRepository extends JpaRepository<BookingServiceItem, Long> {

    List<BookingServiceItem> findByBooking(Booking booking);
}
