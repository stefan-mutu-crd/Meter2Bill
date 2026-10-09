package com.cedacri.internship.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "contractors")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(onlyExplicitlyIncluded = true)
public class Contractor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @Setter(AccessLevel.NONE)
    private Long id;

    /**
     * Trade name shown in the UI, e.g. "Apă-Canal".
     */
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    @ToString.Include
    private String companyName;

    /**
     * Registered name, e.g. "S.A. Apă-Canal Chișinău".
     */
    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String legalName;

    /**
     * Tax identification number (IDNO / CUI). Adjust the pattern to your country.
     */
    @NotBlank
    @Pattern(regexp = "\\d{13}", message = "must contain exactly 13 digits")
    @Column(nullable = false, unique = true, length = 13)
    @ToString.Include
    private String fiscalCode;

    @Size(max = 20)
    @Column(length = 20)
    private String vatCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private CustomerUser owner;

    @Valid
    @NotNull
    @Embedded
    private Address legalAddress;

    @NotBlank
    @Pattern(regexp = "[A-Z]{2}\\d{2}[A-Z0-9]{11,30}", message = "must be a valid IBAN")
    @Column(nullable = false, length = 34)
    private String iban;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String bankName;

    @Pattern(regexp = "[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?", message = "must be a valid BIC/SWIFT code")
    @Column(length = 11)
    private String bic;

    @Email
    @Size(max = 254)
    @Column(length = 254)
    private String email;

    @Pattern(regexp = "\\+?\\d{7,15}", message = "must be a valid phone number")
    @Column(length = 16)
    private String phoneNumber;

    @Builder.Default
    @Column(nullable = false)
    @ToString.Include
    private boolean active = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contractor other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Contractor.class.hashCode();
    }
}