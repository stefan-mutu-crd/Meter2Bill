package com.cedacri.internship.repositories;

import com.cedacri.internship.entities.contract.Contractor;
import com.cedacri.internship.repositories.specification.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractorRepository extends BaseRepository<Contractor, Long> {

    boolean existsByFiscalCode(String fiscalCode);

    boolean existsByFiscalCodeAndIdNot(String fiscalCode, Long id);
}
