package com.cedacri.internship.services.impl.contract;

import com.cedacri.internship.edums.UtilityType;
import com.cedacri.internship.entities.Meter;
import com.cedacri.internship.entities.Unit;
import com.cedacri.internship.entities.contract.Contract;
import com.cedacri.internship.entities.contract.ContractTemplate;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.repositories.ContractRepository;
import com.cedacri.internship.repositories.ContractTemplateRepository;
import com.cedacri.internship.repositories.InvoiceRepository;
import com.cedacri.internship.repositories.MeterRepository;
import com.cedacri.internship.repositories.UnitRepository;
import com.cedacri.internship.services.BaseAbstractService;
import com.cedacri.internship.services.contract.ContractService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ContractServiceImpl extends BaseAbstractService<Contract, Long> implements ContractService {

    private final ContractRepository contractRepository;
    private final UnitRepository unitRepository;
    private final ContractTemplateRepository contractTemplateRepository;
    private final MeterRepository meterRepository;
    private final InvoiceRepository invoiceRepository;

    public ContractServiceImpl(ContractRepository contractRepository,
                               UnitRepository unitRepository,
                               ContractTemplateRepository contractTemplateRepository,
                               MeterRepository meterRepository,
                               InvoiceRepository invoiceRepository) {
        super(contractRepository, Contract.class);
        this.contractRepository = contractRepository;
        this.unitRepository = unitRepository;
        this.contractTemplateRepository = contractTemplateRepository;
        this.meterRepository = meterRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    @Transactional
    public Contract create(Contract contract, Long unitId, Long contractTemplateId) {
        Unit unit = findUnit(unitId);
        if (unit.getTenant() == null) {
            throw new BusinessException("Unit " + unit.getName() + " has no tenant. Assign a tenant before creating a contract.");
        }
        ContractTemplate template = findTemplate(contractTemplateId);
        ensureContractorActive(template);
        LocalDate dateEnd = resolveDateEnd(contract.getDateStart(), contract.getDateEnd(), template);
        UtilityType utilityType = template.getUtility().getType();
        ensureNoOverlap(unit.getId(), utilityType, contract.getDateStart(), dateEnd, null);

        contract.setUnit(unit);
        contract.setCustomer(unit.getTenant());
        contract.setContractTemplate(template);
        contract.setDateEnd(dateEnd);
        contract.setMeter(takeFreeMeter(utilityType, dateEnd));
        return create(contract);
    }

    @Override
    @Transactional
    public Contract update(Long id, Contract changes, Long contractTemplateId) {
        ContractTemplate template = findTemplate(contractTemplateId);
        return update(id, contract -> {
            UtilityType utilityType = contract.getContractTemplate().getUtility().getType();
            if (!template.getId().equals(contract.getContractTemplate().getId())) {
                if (template.getUtility().getType() != utilityType) {
                    throw new BusinessException("Template must be for " + utilityType + " (the installed meter type).");
                }
                ensureContractorActive(template);
            }
            LocalDate dateEnd = resolveDateEnd(changes.getDateStart(), changes.getDateEnd(), template);
            ensureNoOverlap(contract.getUnit().getId(), utilityType, changes.getDateStart(), dateEnd, id);
            if (contract.getMeter().getTestedUntil().isBefore(dateEnd)) {
                throw new BusinessException("Meter " + contract.getMeter().getSerialNumber()
                        + " is tested only until " + contract.getMeter().getTestedUntil() + ".");
            }
            contract.setContractTemplate(template);
            contract.setDateStart(changes.getDateStart());
            contract.setDateEnd(dateEnd);
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (invoiceRepository.existsByContractId(id)) {
            throw new BusinessException("Contract has invoices and cannot be deleted.");
        }
        super.delete(id);
    }

    @Override
    public List<Contract> findByCustomer(Long customerId) {
        return contractRepository.findByCustomerIdOrderByDateStartDesc(customerId);
    }

    @Override
    public List<Contract> findByUnit(Long unitId) {
        return contractRepository.findByUnitIdOrderByDateStartDesc(unitId);
    }

    private LocalDate resolveDateEnd(LocalDate dateStart, LocalDate dateEnd, ContractTemplate template) {
        if (dateStart == null) {
            throw new BusinessException("Start date is required.");
        }
        LocalDate resolved = dateEnd != null ? dateEnd
                : dateStart.plusMonths(template.getDurationInMonths()).minusDays(1);
        if (!resolved.isAfter(dateStart)) {
            throw new BusinessException("End date must be after start date.");
        }
        return resolved;
    }

    private void ensureNoOverlap(Long unitId, UtilityType utilityType, LocalDate dateStart, LocalDate dateEnd, Long excludeId) {
        if (contractRepository.existsOverlapping(unitId, utilityType, dateStart, dateEnd, excludeId == null ? 0L : excludeId)) {
            throw new BusinessException("Unit already has a " + utilityType + " contract in this period.");
        }
    }

    private Meter takeFreeMeter(UtilityType utilityType, LocalDate validUntil) {
        return meterRepository.findFree(utilityType, validUntil, PageRequest.of(0, 1)).stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException("No free " + utilityType + " meter tested until " + validUntil + "."));
    }

    private void ensureContractorActive(ContractTemplate template) {
        if (!template.getContractor().isActive()) {
            throw new BusinessException("Contractor " + template.getContractor().getCompanyName() + " is inactive.");
        }
    }

    private Unit findUnit(Long unitId) {
        if (unitId == null) {
            throw new BusinessException("Unit is required.");
        }
        return unitRepository.findById(unitId)
                .orElseThrow(() -> new BusinessException("Selected unit does not exist."));
    }

    private ContractTemplate findTemplate(Long contractTemplateId) {
        if (contractTemplateId == null) {
            throw new BusinessException("Contract template is required.");
        }
        return contractTemplateRepository.findById(contractTemplateId)
                .orElseThrow(() -> new BusinessException("Selected contract template does not exist."));
    }
}
