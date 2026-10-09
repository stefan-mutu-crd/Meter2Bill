package com.cedacri.internship.entities;

import com.cedacri.internship.edums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "persons")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(onlyExplicitlyIncluded = true)   // personal data (PII) stays out of logs
public class Person implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank
    @Size(max = 45)
    @Column(nullable = false, length = 45)
    private String firstName;

    @NotBlank
    @Size(max = 45)
    @Column(nullable = false, length = 45)
    private String lastName;

    /**
     * National ID (e.g. CNP / IDNP): 13 digits, kept as text so leading zeros survive.
     */
    @Pattern(regexp = "\\d{13}", message = "must contain exactly 13 digits")
    @Column(unique = true, length = 13)
    private String socialId;

    @Past
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", nullable = false, unique = true)
    private Address address;

    /**
     * E.164-style: optional +, 7–15 digits.
     */
    @Pattern(regexp = "\\+?\\d{7,15}", message = "must be a valid phone number")
    @Column(length = 16)
    private String phoneNumber;

    @Email
    @Size(max = 254)
    @Column(unique = true, length = 254)
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Person.class.hashCode();
    }
}