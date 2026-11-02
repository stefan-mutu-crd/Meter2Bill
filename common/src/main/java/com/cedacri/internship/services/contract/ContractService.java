package com.cedacri.internship.services.contract;

import com.cedacri.internship.entities.contract.Contract;
import com.cedacri.internship.services.Operations;

import java.util.List;

public interface ContractService extends Operations<Contract, Long> {

    Contract create(Contract contract, Long unitId, Long contractTemplateId);

    Contract update(Long id, Contract changes, Long contractTemplateId);

    List<Contract> findByCustomer(Long customerId);

    List<Contract> findByUnit(Long unitId);
}
