package com.cedacri.internship.entities;

import com.cedacri.internship.edums.UnitOfMeasure;
import com.cedacri.internship.edums.UtilityType;
import com.cedacri.internship.entities.contract.Contractor;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Utility extends AbstractAuditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UtilityType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UnitOfMeasure unitOfMeasure;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Contractor contractor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utility other)) return false;
        return id != null && id.equals(other.getId());
    }
}