package com.cedacri.internship.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Address extends AbstractAuditable implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(length = 60)
    private String city;

    @Column(length = 60)
    private String district;

    @Column(length = 100)
    private String street;

    @Column(length = 10)
    private String streetNumber;

    @Column(length = 10)
    private String block;

    @Column(length = 5)
    private String floor;

    @Column(length = 10)
    private String apartment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address other)) return false;
        return id != null && id.equals(other.getId());
    }
}