package com.cedacri.internship.repositories;

import com.cedacri.internship.edums.UtilityType;
import com.cedacri.internship.entities.Meter;
import com.cedacri.internship.repositories.specification.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeterRepository extends BaseRepository<Meter, Long> {

    @Query("""
            select m from Meter m
            where m.utilityType = :utilityType
              and m.testedUntil >= :validUntil
              and not exists (select c.id from Contract c where c.meter = m)
            order by m.testedUntil asc, m.id asc
            """)
    List<Meter> findFree(UtilityType utilityType, LocalDate validUntil, Pageable pageable);
}
