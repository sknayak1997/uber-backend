package com.uber.uberapi.models;

import com.uber.uberapi.exceptions.UnapprovedDriverException;
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
@Table(name="driver")
public class Driver extends Auditable{
    private String name;
    private Gender gender;

    @OneToOne
    private User user;

    @OneToOne(mappedBy = "driver")
    private Car car;

    private String licenseDetails;

    @Temporal(value = TemporalType.DATE)
    private Date dob;

    @Enumerated(value = EnumType.STRING)
    private DriverApprovalStatus approvalStatus;

    @OneToMany(mappedBy = "driver")
    private List<Booking> bookings = new ArrayList<>();

    private Boolean isAvailable;

    private String activeCity;

    @OneToOne
    private ExactLocation home;

    @OneToOne
    private ExactLocation lastKnownLocation;

    public void setAvailable(Boolean available) {
        if(available && !this.approvalStatus.equals(DriverApprovalStatus.APPROVED) ) {
            throw new UnapprovedDriverException("Driver approval pending or denied "+ this.getId());
        }
        isAvailable = available;
    }
}
