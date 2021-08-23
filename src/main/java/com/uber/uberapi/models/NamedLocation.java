package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "named_location")
public class NamedLocation extends Auditable{
    @OneToOne
    private ExactLocation exactLocation;
    private String name;
    private String zipCode;
    private String city;
    private String state;
    private String country;
}
