package com.uber.uberapi.models;

import com.uber.uberapi.exceptions.InvalidActionOnBookingStateException;
import com.uber.uberapi.exceptions.InvalidOTPException;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "booking", indexes = {
        @Index(columnList = "passenger_id"),
        @Index(columnList = "driver_id")
})
public class Booking extends Auditable {
    @ManyToOne
    private Passenger passenger;

    @ManyToOne
    private Driver driver;

    @Enumerated(value = EnumType.STRING)
    private BookingType bookingType;

    @OneToOne
    private Review reviewByPassenger;

    @OneToOne
    private Review reviewByDriver;

    @OneToOne
    private PaymentReceipt paymentReceipt;

    @Enumerated(value = EnumType.STRING)
    private BookingStatus bookingStatus;

    @OneToMany
    @JoinTable(
            name="booking_route",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "exact_location_id"),
            indexes = {@Index(columnList = "booking_id")}
    )
    @OrderColumn(name = "location_index")
    private List<ExactLocation> route = new ArrayList<>();

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date startTime;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date endTime;

    @OneToOne
    private OTP rideStartOTP;

    private Long totalDistanceMetres;

    public void startRide(OTP otp) {
        if(!this.bookingStatus.equals(BookingStatus.CAB_ARRIVED)) {
            throw new InvalidActionOnBookingStateException("Cannot start the ride before reaching pick-up location");
        }
        if(!rideStartOTP.validateEnteredOTP(otp)) {
            throw new InvalidOTPException();
        }
        this.setBookingStatus(BookingStatus.IN_RIDE);
    }

    public void endRide() {
        if(!this.bookingStatus.equals(BookingStatus.IN_RIDE)) {
            throw new InvalidActionOnBookingStateException("Cannot end ride before reaching destination");
        }
        this.setBookingStatus(BookingStatus.COMPLETED);
    }
}
