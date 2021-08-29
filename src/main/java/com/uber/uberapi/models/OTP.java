package com.uber.uberapi.models;

import com.uber.uberapi.exceptions.InvalidOTPException;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

import static com.uber.uberapi.models.Constants.RIDE_START_OTP_EXPIRY_SECONDS;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "otp")
public class OTP extends Auditable{
    private String code;
    private String sentToNumber;

    public boolean validateEnteredOTP(OTP otp) {
        if(!this.getCode().equals(otp.getCode())) {
            throw new InvalidOTPException();
        }
        // createdAt + expiryTimeinSeconds > currentTime return true
        // else return false
        //OTP expiry time = 60 seconds
        if(otp.getCreatedAt().toInstant().plusSeconds(RIDE_START_OTP_EXPIRY_SECONDS).isAfter(new Date().toInstant()))
            return true;
        return false;
    }
}

