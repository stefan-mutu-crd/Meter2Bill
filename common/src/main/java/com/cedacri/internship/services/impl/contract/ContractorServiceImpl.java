package com.cedacri.internship.services.impl.contract;

import com.cedacri.internship.entities.Address;
import com.cedacri.internship.entities.contract.Contractor;
import com.cedacri.internship.entities.user.CustomerUser;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.repositories.AddressRepository;
import com.cedacri.internship.repositories.ContractTemplateRepository;
import com.cedacri.internship.repositories.ContractorRepository;
import com.cedacri.internship.repositories.CustomerUserRepository;
import com.cedacri.internship.repositories.UtilityRepository;
import com.cedacri.internship.services.BaseAbstractService;
import com.cedacri.internship.services.contract.ContractorService;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class ContractorServiceImpl extends BaseAbstractService<Contractor, Long> implements ContractorService {

    private final ContractorRepository contractorRepository;
    private final AddressRepository addressRepository;
    private final CustomerUserRepository customerUserRepository;
    private final UtilityRepository utilityRepository;
    private final ContractTemplateRepository contractTemplateRepository;

    public ContractorServiceImpl(ContractorRepository contractorRepository,
                                 AddressRepository addressRepository,
                                 CustomerUserRepository customerUserRepository,
                                 UtilityRepository utilityRepository,
                                 ContractTemplateRepository contractTemplateRepository) {
        super(contractorRepository, Contractor.class);
        this.contractorRepository = contractorRepository;
        this.addressRepository = addressRepository;
        this.customerUserRepository = customerUserRepository;
        this.utilityRepository = utilityRepository;
        this.contractTemplateRepository = contractTemplateRepository;
    }

    @Override
    @Transactional
    public Contractor create(Contractor contractor, Long ownerId) {
        normalize(contractor);
        ensureFiscalCodeAvailable(contractor.getFiscalCode(), null);
        contractor.setOwner(findOwner(ownerId));
        normalize(contractor.getLegalAddress());
        contractor.setLegalAddress(addressRepository.save(contractor.getLegalAddress()));
        return create(contractor);
    }

    @Override
    @Transactional
    public Contractor update(Long id, Contractor changes, Long ownerId) {
        normalize(changes);
        ensureFiscalCodeAvailable(changes.getFiscalCode(), id);
        CustomerUser owner = findOwner(ownerId);
        return update(id, contractor -> {
            contractor.setCompanyName(changes.getCompanyName());
            contractor.setLegalName(changes.getLegalName());
            contractor.setFiscalCode(changes.getFiscalCode());
            contractor.setOwner(owner);
            contractor.setIban(changes.getIban());
            contractor.setBankName(changes.getBankName());
            contractor.setEmail(changes.getEmail());
            contractor.setPhoneNumber(changes.getPhoneNumber());
            contractor.setActive(changes.isActive());
            copyAddress(changes.getLegalAddress(), contractor.getLegalAddress());
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (utilityRepository.existsByContractorId(id) || contractTemplateRepository.existsByContractorId(id)) {
            throw new BusinessException("Contractor is used by utilities or contract templates. Deactivate it instead.");
        }
        Contractor contractor = findById(id);
        Address address = Hibernate.unproxy(contractor.getLegalAddress(), Address.class);
        contractorRepository.delete(contractor);
        contractorRepository.flush();
        addressRepository.delete(address);
    }

    private void ensureFiscalCodeAvailable(String fiscalCode, Long id) {
        boolean taken = id == null
                ? contractorRepository.existsByFiscalCode(fiscalCode)
                : contractorRepository.existsByFiscalCodeAndIdNot(fiscalCode, id);
        if (taken) {
            throw new BusinessException("Fiscal code " + fiscalCode + " is already used by another contractor.");
        }
    }

    private CustomerUser findOwner(Long ownerId) {
        if (ownerId == null) {
            throw new BusinessException("Owner is required.");
        }
        return customerUserRepository.findById(ownerId)
                .orElseThrow(() -> new BusinessException("Selected owner does not exist."));
    }

    private void normalize(Contractor contractor) {
        contractor.setCompanyName(contractor.getCompanyName().trim());
        contractor.setLegalName(contractor.getLegalName().trim());
        contractor.setFiscalCode(normalizeCode(contractor.getFiscalCode()));
        contractor.setIban(normalizeCode(contractor.getIban()));
        contractor.setBankName(contractor.getBankName().trim());
        contractor.setEmail(blankToNull(contractor.getEmail()));
        contractor.setPhoneNumber(blankToNull(contractor.getPhoneNumber()));
    }

    private void normalize(Address address) {
        copyAddress(address, address);
    }

    private void copyAddress(Address source, Address target) {
        target.setCity(blankToNull(source.getCity()));
        target.setDistrict(blankToNull(source.getDistrict()));
        target.setStreet(blankToNull(source.getStreet()));
        target.setStreetNumber(blankToNull(source.getStreetNumber()));
        target.setBlock(blankToNull(source.getBlock()));
        target.setFloor(blankToNull(source.getFloor()));
        target.setApartment(blankToNull(source.getApartment()));
    }

    private static String normalizeCode(String value) {
        return value.replaceAll("\\s", "").toUpperCase(Locale.ROOT);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
