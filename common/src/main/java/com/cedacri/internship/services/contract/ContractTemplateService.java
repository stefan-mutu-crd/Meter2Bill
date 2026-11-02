package com.cedacri.internship.services.contract;

import com.cedacri.internship.entities.contract.ContractTemplate;
import com.cedacri.internship.services.Operations;

public interface ContractTemplateService extends Operations<ContractTemplate, Long> {

    ContractTemplate create(ContractTemplate contractTemplate, Long utilityId);

    ContractTemplate update(Long id, ContractTemplate changes, Long utilityId);
}
