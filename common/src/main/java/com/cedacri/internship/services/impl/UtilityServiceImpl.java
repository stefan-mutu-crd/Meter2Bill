package com.cedacri.internship.services.impl;

import com.cedacri.internship.edums.UtilityType;
import com.cedacri.internship.entities.Utility;
import com.cedacri.internship.entities.contract.Contractor;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.repositories.ContractTemplateRepository;
import com.cedacri.internship.repositories.ContractorRepository;
import com.cedacri.internship.repositories.UtilityRepository;
import com.cedacri.internship.services.BaseAbstractService;
import com.cedacri.internship.services.UtilityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UtilityServiceImpl extends BaseAbstractService<Utility, Long> implements UtilityService {

    private final UtilityRepository utilityRepository;
    private final ContractorRepository contractorRepository;
    private final ContractTemplateRepository contractTemplateRepository;

    public UtilityServiceImpl(UtilityRepository utilityRepository,
                              ContractorRepository contractorRepository,
                              ContractTemplateRepository contractTemplateRepository) {
        super(utilityRepository, Utility.class);
        this.utilityRepository = utilityRepository;
        this.contractorRepository = contractorRepository;
        this.contractTemplateRepository = contractTemplateRepository;
    }

    @Override
    @Transactional
    public Utility create(Utility utility, Long contractorId) {
        Contractor contractor = findContractor(contractorId);
        ensureTypeAvailable(utility.getType(), contractorId, null);
        utility.setContractor(contractor);
        return create(utility);
    }

    @Override
    @Transactional
    public Utility update(Long id, Utility changes, Long contractorId) {
        Contractor contractor = findContractor(contractorId);
        ensureTypeAvailable(changes.getType(), contractorId, id);
        return update(id, utility -> {
            utility.setType(changes.getType());
            utility.setUnitOfMeasure(changes.getUnitOfMeasure());
            utility.setContractor(contractor);
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (contractTemplateRepository.existsByUtilityId(id)) {
            throw new BusinessException("Utility is used by contract templates and cannot be deleted.");
        }
        super.delete(id);
    }

    private void ensureTypeAvailable(UtilityType type, Long contractorId, Long id) {
        boolean taken = id == null
                ? utilityRepository.existsByTypeAndContractorId(type, contractorId)
                : utilityRepository.existsByTypeAndContractorIdAndIdNot(type, contractorId, id);
        if (taken) {
            throw new BusinessException("This contractor already provides " + type + ".");
        }
    }

    private Contractor findContractor(Long contractorId) {
        if (contractorId == null) {
            throw new BusinessException("Contractor is required.");
        }
        return contractorRepository.findById(contractorId)
                .orElseThrow(() -> new BusinessException("Selected contractor does not exist."));
    }
}
