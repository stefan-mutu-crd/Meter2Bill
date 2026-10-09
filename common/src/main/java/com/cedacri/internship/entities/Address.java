package com.cedacri.internship.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Entity
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Size(max = 60)
    @Column(length = 60)
    private String city;

    @Size(max = 60)
    @Column(length = 60)
    private String district;

    @NotBlank
    @Size(max = 100)
    @Column(length = 100)
    private String street;

    @NotBlank
    @Size(max = 10)
    @Column(length = 10)
    private String streetNumber;

    @Size(max = 10)
    @Column(length = 10)
    private String block;

    @Size(max = 5)
    @Column(length = 5)
    private String floor;

    @Size(max = 10)
    @Column(length = 10)
    private String apartment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerUser other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}