package com.cedacri.internship.repositories;

import com.cedacri.internship.edums.UtilityType;
import com.cedacri.internship.entities.contract.Contract;
import com.cedacri.internship.repositories.specification.BaseRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ContractRepository extends BaseRepository<Contract, Long> {

    boolean existsByContractTemplateId(Long contractTemplateId);

    boolean existsByUnitId(Long unitId);

    boolean existsByCustomerId(Long customerId);

    @EntityGraph(attributePaths = {"unit", "contractTemplate", "contractTemplate.utility", "contractTemplate.contractor", "meter"})
    List<Contract> findByCustomerIdOrderByDateStartDesc(Long customerId);

    @EntityGraph(attributePaths = {"customer", "contractTemplate", "contractTemplate.utility", "contractTemplate.contractor", "meter"})
    List<Contract> findByUnitIdOrderByDateStartDesc(Long unitId);

    @Query("""
            select count(c) > 0 from Contract c
            where c.unit.id = :unitId
              and c.contractTemplate.utility.type = :utilityType
              and c.dateStart <= :dateEnd
              and c.dateEnd >= :dateStart
              and c.id <> :excludeId
            """)
    boolean existsOverlapping(Long unitId, UtilityType utilityType, LocalDate dateStart, LocalDate dateEnd, Long excludeId);
}
