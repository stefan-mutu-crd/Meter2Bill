package com.cedacri.internship.services.impl.contract;

import com.cedacri.internship.entities.Utility;
import com.cedacri.internship.entities.contract.ContractTemplate;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.repositories.ContractRepository;
import com.cedacri.internship.repositories.ContractTemplateRepository;
import com.cedacri.internship.repositories.UtilityRepository;
import com.cedacri.internship.services.BaseAbstractService;
import com.cedacri.internship.services.contract.ContractTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ContractTemplateServiceImpl extends BaseAbstractService<ContractTemplate, Long> implements ContractTemplateService {

    private final UtilityRepository utilityRepository;
    private final ContractRepository contractRepository;

    public ContractTemplateServiceImpl(ContractTemplateRepository contractTemplateRepository,
                                       UtilityRepository utilityRepository,
                                       ContractRepository contractRepository) {
        super(contractTemplateRepository, ContractTemplate.class);
        this.utilityRepository = utilityRepository;
        this.contractRepository = contractRepository;
    }

    @Override
    @Transactional
    public ContractTemplate create(ContractTemplate contractTemplate, Long utilityId) {
        validate(contractTemplate);
        Utility utility = findUtility(utilityId);
        ensureContractorActive(utility);
        contractTemplate.setUtility(utility);
        contractTemplate.setContractor(utility.getContractor());
        return create(contractTemplate);
    }

    @Override
    @Transactional
    public ContractTemplate update(Long id, ContractTemplate changes, Long utilityId) {
        validate(changes);
        Utility utility = findUtility(utilityId);
        return update(id, contractTemplate -> {
            if (!utility.getId().equals(contractTemplate.getUtility().getId())) {
                ensureContractorActive(utility);
            }
            contractTemplate.setUtility(utility);
            contractTemplate.setContractor(utility.getContractor());
            contractTemplate.setPrice(changes.getPrice());
            contractTemplate.setPaymentPlanType(changes.getPaymentPlanType());
            contractTemplate.setDurationInMonths(changes.getDurationInMonths());
            contractTemplate.setAutomaticRenewal(changes.isAutomaticRenewal());
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (contractRepository.existsByContractTemplateId(id)) {
            throw new BusinessException("Contract template is used by contracts and cannot be deleted.");
        }
        super.delete(id);
    }

    private void validate(ContractTemplate contractTemplate) {
        if (contractTemplate.getPrice() == null || contractTemplate.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Price must be greater than zero.");
        }
        if (contractTemplate.getDurationInMonths() == null || contractTemplate.getDurationInMonths() <= 0) {
            throw new BusinessException("Duration must be at least one month.");
        }
        if (contractTemplate.getPaymentPlanType() == null) {
            throw new BusinessException("Payment plan is required.");
        }
    }

    private void ensureContractorActive(Utility utility) {
        if (!utility.getContractor().isActive()) {
            throw new BusinessException("Contractor " + utility.getContractor().getCompanyName() + " is inactive.");
        }
    }

    private Utility findUtility(Long utilityId) {
        if (utilityId == null) {
            throw new BusinessException("Utility is required.");
        }
        return utilityRepository.findById(utilityId)
                .orElseThrow(() -> new BusinessException("Selected utility does not exist."));
    }
}
