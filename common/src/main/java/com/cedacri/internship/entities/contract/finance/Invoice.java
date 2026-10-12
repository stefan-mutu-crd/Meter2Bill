package com.cedacri.internship.entities.contract.finance;

import com.cedacri.internship.entities.AbstractAuditable;
import com.cedacri.internship.entities.contract.Contract;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Invoice extends AbstractAuditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Contract contract;

    @Column(nullable = false, unique = true)
    Long invoiceNumber;

    @Column(nullable = false)
    LocalDate billingPeriodStart;

    @Column(nullable = false)
    LocalDate billingPeriodEnd;

    @Column(nullable = false)
    LocalDate billingDateLimit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invoice other)) return false;
        return id != null && id.equals(other.getId());
    }
}
