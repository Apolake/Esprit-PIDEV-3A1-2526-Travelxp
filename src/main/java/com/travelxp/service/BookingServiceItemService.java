package com.travelxp.service;

import com.travelxp.model.*;
import com.travelxp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BookingServiceItemService {

    @Autowired
    private BookingServiceItemRepository bookingServiceItemRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // CREATE
    @Transactional
    public BookingServiceItem addServiceToBooking(Long bookingId, Long serviceId, Integer quantity) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        BookingServiceItem item = new BookingServiceItem(
                booking,
                service,
                quantity,
                service.getPrice()
        );

        return bookingServiceItemRepository.save(item);
    }

    // READ
    public List<BookingServiceItem> getServicesByBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        return bookingServiceItemRepository.findByBooking(booking);
    }

    // UPDATE
    @Transactional
    public BookingServiceItem updateQuantity(Long itemId, Integer newQuantity) {
        BookingServiceItem item = bookingServiceItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Service item not found"));

        item.setQuantity(newQuantity);
        return bookingServiceItemRepository.save(item);
    }

    // DELETE
    @Transactional
    public void removeServiceFromBooking(Long itemId) {
        bookingServiceItemRepository.deleteById(itemId);
    }
}
