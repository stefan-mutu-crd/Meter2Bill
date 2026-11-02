package com.cedacri.internship.services.impl.contract;

import com.cedacri.internship.TestData;
import com.cedacri.internship.edums.UtilityType;
import com.cedacri.internship.entities.Address;
import com.cedacri.internship.entities.contract.Contractor;
import com.cedacri.internship.entities.user.CustomerUser;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.repositories.AddressRepository;
import com.cedacri.internship.services.contract.ContractorService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ContractorServiceImplTest {

    @Autowired
    private ContractorService contractorService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TestData data;

    @BeforeEach
    void setUp() {
        data.cleanDb();
    }

    @Test
    void createNormalizesFieldsAndSavesAddress() {
        CustomerUser owner = data.customer();

        Contractor created = contractorService.create(newContractor(" md 123 "), owner.getId());

        assertThat(created.getId()).isNotNull();
        assertThat(created.getCompanyName()).isEqualTo("Acme");
        assertThat(created.getFiscalCode()).isEqualTo("MD123");
        assertThat(created.getIban()).isEqualTo("MD24AG000225100013104168");
        assertThat(created.getEmail()).isNull();
        assertThat(created.getOwner().getId()).isEqualTo(owner.getId());
        Address address = addressRepository.findById(created.getLegalAddress().getId()).orElseThrow();
        assertThat(address.getCity()).isEqualTo("Balti");
        assertThat(address.getStreet()).isNull();
    }

    @Test
    void createRefusedWhenFiscalCodeTaken() {
        Contractor existing = data.contractor();
        Long ownerId = data.customer().getId();

        assertThatThrownBy(() -> contractorService.create(newContractor(existing.getFiscalCode()), ownerId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already used");
    }

    @Test
    void createRefusedWithoutOwner() {
        assertThatThrownBy(() -> contractorService.create(newContractor("MD1"), null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Owner is required.");
    }

    @Test
    void updateCopiesFieldsAndAddress() {
        Contractor contractor = data.contractor();
        CustomerUser newOwner = data.customer();
        Contractor changes = newContractor(contractor.getFiscalCode());
        changes.setActive(false);

        contractorService.update(contractor.getId(), changes, newOwner.getId());

        Contractor updated = contractorService.findById(contractor.getId());
        assertThat(updated.getCompanyName()).isEqualTo("Acme");
        assertThat(updated.isActive()).isFalse();
        assertThat(updated.getOwner().getId()).isEqualTo(newOwner.getId());
        Address address = addressRepository.findById(contractor.getLegalAddress().getId()).orElseThrow();
        assertThat(address.getCity()).isEqualTo("Balti");
    }

    @Test
    void updateRefusedWhenFiscalCodeUsedByAnotherContractor() {
        Contractor contractor = data.contractor();
        Contractor other = data.contractor();
        Long ownerId = contractor.getOwner().getId();

        assertThatThrownBy(() -> contractorService.update(contractor.getId(), newContractor(other.getFiscalCode()), ownerId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already used");
    }

    @Test
    void updateRefusedWhenOwnerMissing() {
        Contractor contractor = data.contractor();

        assertThatThrownBy(() -> contractorService.update(contractor.getId(), newContractor("MD9"), 99L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Selected owner does not exist.");
    }

    @Test
    void deleteRemovesContractorAndAddress() {
        Contractor contractor = data.contractor();
        Long addressId = contractor.getLegalAddress().getId();

        contractorService.delete(contractor.getId());

        assertThatThrownBy(() -> contractorService.findById(contractor.getId()))
                .isInstanceOf(EntityNotFoundException.class);
        assertThat(addressRepository.existsById(addressId)).isFalse();
    }

    @Test
    void deleteRefusedWhenUsedByUtility() {
        Contractor contractor = data.contractor();
        data.utility(UtilityType.GAS, contractor);

        assertThatThrownBy(() -> contractorService.delete(contractor.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Deactivate it instead");
    }

    private Contractor newContractor(String fiscalCode) {
        return Contractor.builder()
                .companyName(" Acme ")
                .legalName("Acme SRL")
                .fiscalCode(fiscalCode)
                .iban("md24 ag00 0225 1000 1310 4168")
                .bankName("Bank")
                .email(" ")
                .legalAddress(Address.builder().city(" Balti ").street(" ").build())
                .build();
    }
}
