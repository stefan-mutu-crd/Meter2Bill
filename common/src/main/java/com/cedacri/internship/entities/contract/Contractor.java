package com.cedacri.internship.entities.contract;

import com.cedacri.internship.entities.AbstractAuditable;
import com.cedacri.internship.entities.Address;
import com.cedacri.internship.entities.user.CustomerUser;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Contractor extends AbstractAuditable implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String companyName;

    @Column(nullable = false, length = 150)
    private String legalName;

    @Column(nullable = false, unique = true, length = 13)
    private String fiscalCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private CustomerUser owner;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Address legalAddress;

    @Column(nullable = false, length = 34)
    private String iban;

    @Column(nullable = false, length = 100)
    private String bankName;

    @Column(length = 254)
    private String email;

    @Column(length = 16)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean active = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contractor other)) return false;
        return id != null && id.equals(other.getId());
    }
}