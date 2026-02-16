package com.travelxp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "booking_services")
public class BookingServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "price_at_booking", nullable = false)
    private BigDecimal priceAtBooking;

    public BookingServiceItem() {}

    public BookingServiceItem(Booking booking, Service service, Integer quantity, BigDecimal priceAtBooking) {
        this.booking = booking;
        this.service = service;
        this.quantity = quantity;
        this.priceAtBooking = priceAtBooking;
    }

    public BigDecimal getTotalPrice() {
        return priceAtBooking.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters & Setters
}
