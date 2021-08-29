package com.uber.uberapi.controllers;

import com.uber.uberapi.exceptions.InvalidBookingException;
import com.uber.uberapi.exceptions.InvalidDriverException;
import com.uber.uberapi.models.*;
import com.uber.uberapi.repositories.BookingRepository;
import com.uber.uberapi.repositories.DriverRepository;
import com.uber.uberapi.repositories.ReviewRepository;
import com.uber.uberapi.services.BookingService;
import com.uber.uberapi.services.DriverMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/driver")
public class DriverController {
    // All the endpoints driver can use
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private DriverMatchingService driverMatchingService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookingService bookingService;

    public Driver getDriverFromId(Long driverId) {
        Optional<Driver> driver = driverRepository.findById(driverId);
        if(driver.isEmpty()) {
            throw new InvalidDriverException("Driver not found with id " + driverId);
        }
        return driver.get();
    }

    public Booking getDriverBookingFromId(Long bookingId, Driver driver) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if(optionalBooking.isEmpty()) {
            throw new InvalidBookingException("Booking not found with id " + bookingId);
        }
        Booking booking = optionalBooking.get();
        if(!booking.getDriver().equals(driver)) {
            throw new InvalidBookingException("Driver " + driver.getId() + " has no such booking " + bookingId);
        }
        return booking;
    }

    @GetMapping("/{driverId}")
    public Driver getDriverDetails(@PathVariable Long driverId) {
        // ensure driver is authenticated
        return getDriverFromId(driverId);
    }

    @PatchMapping("/{driverId}")
    public void changeAvailability(@PathVariable Long driverId,
                                   @RequestBody Boolean available) {
        Driver driver = getDriverFromId(driverId);
        driver.setIsAvailable(available);
        driverRepository.save(driver);
    }

    @GetMapping("/{driverId}/bookings")
    public List<Booking> getAllBookings(@PathVariable Long driverId) {
        Driver driver = getDriverFromId(driverId);
        return driver.getBookings();
    }

    @GetMapping("/{driverId}/bookings/{bookingId}")
    public Booking getBooking(@PathVariable Long driverId,
                              @PathVariable Long bookingId) {
        Driver driver = getDriverFromId(driverId);
        Booking booking = getDriverBookingFromId(bookingId, driver);
        return booking;
    }

    @PostMapping("/{driverId}/bookings/{bookingId}")
    public void acceptBooking(@PathVariable Long driverId,
                              @PathVariable Long bookingId) {
        Driver driver = getDriverFromId(driverId);
        Booking booking = getDriverBookingFromId(bookingId, driver);
        bookingService.acceptBooking(driver, booking);
    }

    @DeleteMapping("/{driverId}/bookings/{bookingId}")
    public void cancelBooking(@PathVariable Long driverId,
                              @PathVariable Long bookingId) {
        Driver driver = getDriverFromId(driverId);
        Booking booking = getDriverBookingFromId(bookingId, driver);
        //push to task queue - producer
        bookingService.cancelByDriver(driver, booking);
    }

    @PatchMapping("{driverId}/bookings/{bookingId}/start")
    public void startRide(@PathVariable Long driverId,
                          @PathVariable Long bookingId,
                          @RequestBody OTP otp) {
        Driver driver = getDriverFromId(driverId);
        Booking booking = getDriverBookingFromId(bookingId, driver);
        booking.startRide(otp);
        bookingRepository.save(booking);
    }

    @PatchMapping("{driverId}/bookings/{bookingId}/end")
    public void endRide(@PathVariable Long driverId,
                        @PathVariable Long bookingId) {
        Driver driver = getDriverFromId(driverId);
        Booking booking = getDriverBookingFromId(bookingId, driver);
        booking.endRide();
        bookingRepository.save(booking);
    }

    @PatchMapping("/{driverId}/bookings/{bookingId}/rate")
    public void ratingByDriver(@PathVariable Long driverId,
                               @PathVariable Long bookingId,
                               @RequestBody Review data) {
        Driver driver = getDriverFromId(driverId);
        Booking booking = getDriverBookingFromId(bookingId, driver);
        Review review = Review.builder()
                .description(data.getDescription())
                .ratingOutOfFive((data.getRatingOutOfFive()))
                .build();
        booking.setReviewByDriver(review);
        bookingRepository.save(booking);
        reviewRepository.save(review);
    }
}
