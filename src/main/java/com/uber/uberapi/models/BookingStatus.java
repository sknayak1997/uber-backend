package com.uber.uberapi.models;

public enum BookingStatus {
    CANCELLED("The booking has been cancelled due to some reason"),
    SCHEDULED("The booking scheduled for future time"),
    ASSIGNING_DRIVER("The rider has requested and driver is yet to be assigned"),
    REACHING_PICKUP_LOCATION("The driver has accepted the booking ans heading towards pickup location"),
    CAB_ARRIVED("The driver has arrived at the pickup location and waiting for passenger"),
    IN_RIDE("The ride is currently in progress"),
    COMPLETED("The ride has already been completed");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }
}
