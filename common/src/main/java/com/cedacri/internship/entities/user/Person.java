package com.cedacri.internship.entities.user;

import com.cedacri.internship.edums.Gender;
import com.cedacri.internship.entities.AbstractAuditable;
import com.cedacri.internship.entities.Address;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(onlyExplicitlyIncluded = true)
public class Person extends AbstractAuditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @Setter(AccessLevel.NONE)
    private Long id;

    @ToString.Include
    @Column(nullable = false, length = 45)
    private String firstName;

    @ToString.Include
    @Column(nullable = false, length = 45)
    private String lastName;

    @Column(unique = true, length = 13)
    private String socialId;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, unique = true)
    private Address address;

    @Column(length = 16)
    private String phoneNumber;

    @Size(max = 254)
    @Column(unique = true, length = 254)
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person other)) return false;
        return id != null && id.equals(other.getId());
    }
}