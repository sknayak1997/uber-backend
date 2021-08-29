package com.uber.uberapi.services;

import com.uber.uberapi.models.Booking;

public interface ScheduleBookingService {
    void schedule(Booking booking);
}
