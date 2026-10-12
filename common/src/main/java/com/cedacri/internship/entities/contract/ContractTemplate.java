package com.cedacri.internship.entities.contract;

import com.cedacri.internship.edums.PaymentPlanType;
import com.cedacri.internship.entities.AbstractAuditable;
import com.cedacri.internship.entities.Utility;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractTemplate extends AbstractAuditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Contractor contractor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Utility utility;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentPlanType paymentPlanType;

    @Column(nullable = false)
    private Integer durationInMonths;

    @Column(nullable = false)
    private boolean automaticRenewal;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContractTemplate other)) return false;
        return id != null && id.equals(other.getId());
    }
}
