package com.cedacri.internship.repositories;

import com.cedacri.internship.entities.contract.ContractTemplate;
import com.cedacri.internship.repositories.specification.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractTemplateRepository extends BaseRepository<ContractTemplate, Long> {

    boolean existsByContractorId(Long contractorId);

    boolean existsByUtilityId(Long utilityId);
}
