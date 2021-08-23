package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

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
}

