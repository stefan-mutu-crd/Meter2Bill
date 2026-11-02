package com.cedacri.internship.repositories;

import com.cedacri.internship.edums.UtilityType;
import com.cedacri.internship.entities.Utility;
import com.cedacri.internship.repositories.specification.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilityRepository extends BaseRepository<Utility, Long> {

    boolean existsByContractorId(Long contractorId);

    boolean existsByTypeAndContractorId(UtilityType type, Long contractorId);

    boolean existsByTypeAndContractorIdAndIdNot(UtilityType type, Long contractorId, Long id);
}
