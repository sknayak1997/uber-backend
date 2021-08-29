package com.uber.uberapi.controllers;

import com.uber.uberapi.exceptions.InvalidBookingException;
import com.uber.uberapi.exceptions.InvalidPassengerException;
import com.uber.uberapi.models.Booking;
import com.uber.uberapi.models.Passenger;
import com.uber.uberapi.models.Review;
import com.uber.uberapi.repositories.BookingRepository;
import com.uber.uberapi.repositories.PassengerRepository;
import com.uber.uberapi.repositories.ReviewRepository;
import com.uber.uberapi.services.BookingService;
import com.uber.uberapi.services.DriverMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/passenger")
public class PassengerController {
    // Endpoints for passengers
    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private DriverMatchingService driverMatchingService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookingService bookingService;

    public Passenger getPassengerFromId(Long passengerId) {
        Optional<Passenger> passenger = passengerRepository.findById(passengerId);
        if(passenger.isEmpty()) {
            throw new InvalidPassengerException("Passenger not found with id " + passengerId);
        }
        return passenger.get();
    }

    public Booking getPassengerBookingFromId(Long bookingId, Passenger passenger) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if(optionalBooking.isEmpty()) {
            throw new InvalidBookingException("Booking not found with id " + bookingId);
        }
        Booking booking = optionalBooking.get();
        if(!booking.getPassenger().equals(passenger)) {
            throw new InvalidBookingException("Passenger " + passenger.getId() + " has no such booking " + bookingId);
        }
        return booking;
    }

    @GetMapping("/{passengerId}")
    public Passenger getPassengerDetails(@PathVariable Long passengerId) {
        // ensure passenger is authenticated
        return getPassengerFromId(passengerId);
    }

    @GetMapping("/{passengerId}/bookings")
    public List<Booking> getAllBookings(@PathVariable Long passengerId) {
        Passenger passenger = getPassengerFromId(passengerId);
        return passenger.getBookings();
    }

    @GetMapping("/{passengerId}/bookings/{bookingId}")
    public Booking getBooking(@PathVariable Long passengerId,
                              @PathVariable Long bookingId) {
        Passenger passenger = getPassengerFromId(passengerId);
        Booking booking = getPassengerBookingFromId(bookingId, passenger);
        return booking;
    }

    @PostMapping("/{passengerId}/bookings")
    public void requestBooking(@PathVariable Long passengerId,
                               @RequestBody Booking data) {
        Passenger passenger = getPassengerFromId(passengerId);
        Booking booking = Booking.builder().build();
        bookingService.createBooking(booking);
        bookingRepository.save(booking);
        passengerRepository.save(passenger);
        // todo
    }

    @DeleteMapping("/{passengerId}/bookings/{bookingId}")
    public void cancelBooking(@PathVariable Long passengerId,
                              @PathVariable Long bookingId) {
        Passenger passenger = getPassengerFromId(passengerId);
        Booking booking = getPassengerBookingFromId(bookingId, passenger);
        bookingService.cancelByPassenger(passenger, booking);
    }


    @PatchMapping("{passengerId}/bookings/{bookingId}/rate")
    public void ratingByPassenger(@PathVariable Long passengerId,
                               @PathVariable Long bookingId,
                               @RequestBody Review data) {
        Passenger passenger = getPassengerFromId(passengerId);
        Booking booking = getPassengerBookingFromId(bookingId, passenger);
        Review review = Review.builder()
                .description(data.getDescription())
                .ratingOutOfFive((data.getRatingOutOfFive()))
                .build();
        booking.setReviewByPassenger(review);
        bookingRepository.save(booking);
        reviewRepository.save(review);
    }
}
