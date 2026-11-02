package com.cedacri.internship.services.contract;

import com.cedacri.internship.entities.contract.Contractor;
import com.cedacri.internship.services.Operations;

public interface ContractorService extends Operations<Contractor, Long> {

    Contractor create(Contractor contractor, Long ownerId);

    Contractor update(Long id, Contractor changes, Long ownerId);
}
