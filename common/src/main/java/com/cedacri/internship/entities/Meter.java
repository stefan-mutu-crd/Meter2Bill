package com.cedacri.internship.entities;

import com.cedacri.internship.edums.UtilityType;
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
@ToString(onlyExplicitlyIncluded = true)
public class Meter extends AbstractAuditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @Setter(AccessLevel.NONE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UtilityType utilityType;

    @Column(nullable = false)
    private String manufacturer;

    @Column(nullable = false)
    private LocalDate testedUntil;

    @Column(nullable = false)
    private String serialNumber;

    @Column(nullable = false, unique = true)
    private String secretKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Meter other)) return false;
        return id != null && id.equals(other.getId());
    }
}
