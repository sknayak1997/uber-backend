package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role")
public class Role extends Auditable {
    @Column(nullable = false, unique = true)
    private String name;
    private String description;
}

// Role Based Authentication
// Permission Based Authentication