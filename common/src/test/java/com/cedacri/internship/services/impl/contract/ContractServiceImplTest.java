package com.cedacri.internship.services.impl.contract;

import com.cedacri.internship.TestData;
import com.cedacri.internship.edums.UtilityType;
import com.cedacri.internship.entities.Meter;
import com.cedacri.internship.entities.Unit;
import com.cedacri.internship.entities.contract.Contract;
import com.cedacri.internship.entities.contract.ContractTemplate;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.repositories.MeterRepository;
import com.cedacri.internship.services.contract.ContractService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ContractServiceImplTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 1);

    @Autowired
    private ContractService contractService;

    @Autowired
    private MeterRepository meterRepository;

    @Autowired
    private TestData data;

    @BeforeEach
    void setUp() {
        data.cleanDb();
    }

    @Test
    void createAssignsTenantMeterAndEndDate() {
        Unit unit = data.rentedUnit();
        ContractTemplate template = data.template();
        Meter meter = data.meter();

        Contract created = contractService.create(newContract(START, null), unit.getId(), template.getId());

        assertThat(created.getCustomer().getId()).isEqualTo(unit.getTenant().getId());
        assertThat(created.getMeter().getId()).isEqualTo(meter.getId());
        assertThat(created.getDateEnd()).isEqualTo(LocalDate.of(2026, 12, 31));
    }

    @Test
    void createRefusedWhenUnitHasNoTenant() {
        Unit unit = data.unit();
        ContractTemplate template = data.template();

        assertThatThrownBy(() -> contractService.create(newContract(START, null), unit.getId(), template.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("has no tenant");
    }

    @Test
    void createRefusedWhenNoFreeMeter() {
        Unit unit = data.rentedUnit();
        ContractTemplate template = data.template();
        data.meter(UtilityType.ELECTRICITY, START.plusMonths(6));
        data.meter(UtilityType.GAS, START.plusYears(5));

        assertThatThrownBy(() -> contractService.create(newContract(START, null), unit.getId(), template.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No free ELECTRICITY meter");
    }

    @Test
    void createRefusedWhenContractOverlaps() {
        Unit unit = data.rentedUnit();
        ContractTemplate template = data.template();
        Contract existing = data.contract(unit, template);
        data.meter();

        assertThatThrownBy(() -> contractService.create(newContract(existing.getDateStart(), null), unit.getId(), template.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already has a ELECTRICITY contract");
    }

    @Test
    void createRefusedWhenContractorInactive() {
        Unit unit = data.rentedUnit();
        ContractTemplate template = data.template(data.utility(UtilityType.ELECTRICITY, data.inactiveContractor()));
        data.meter();

        assertThatThrownBy(() -> contractService.create(newContract(START, null), unit.getId(), template.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("is inactive");
    }

    @Test
    void createRefusedWhenEndBeforeStart() {
        Unit unit = data.rentedUnit();
        ContractTemplate template = data.template();
        data.meter();

        assertThatThrownBy(() -> contractService.create(newContract(START, START.minusDays(1)), unit.getId(), template.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("End date must be after start date.");
    }

    @Test
    void updateChangesDatesAndTemplate() {
        Contract contract = data.contract();
        ContractTemplate other = data.template(data.utility());
        LocalDate start = contract.getDateStart().plusMonths(1);

        contractService.update(contract.getId(), newContract(start, null), other.getId());

        Contract updated = contractService.findById(contract.getId());
        assertThat(updated.getContractTemplate().getId()).isEqualTo(other.getId());
        assertThat(updated.getDateStart()).isEqualTo(start);
        assertThat(updated.getDateEnd()).isEqualTo(start.plusMonths(12).minusDays(1));
    }

    @Test
    void updateRefusedWhenTemplateHasOtherUtilityType() {
        Contract contract = data.contract();
        ContractTemplate gas = data.template(data.utility(UtilityType.GAS, data.contractor()));

        assertThatThrownBy(() -> contractService.update(contract.getId(), newContract(contract.getDateStart(), null), gas.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Template must be for ELECTRICITY");
    }

    @Test
    void updateRefusedWhenMeterTestedTooShort() {
        Contract contract = data.contract();
        Long templateId = contract.getContractTemplate().getId();
        Contract changes = newContract(contract.getDateStart(), contract.getDateStart().plusYears(10));

        assertThatThrownBy(() -> contractService.update(contract.getId(), changes, templateId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("is tested only until");
    }

    @Test
    void deleteRemovesContractAndFreesMeter() {
        Contract contract = data.contract();
        Long meterId = contract.getMeter().getId();

        contractService.delete(contract.getId());

        assertThatThrownBy(() -> contractService.findById(contract.getId()))
                .isInstanceOf(EntityNotFoundException.class);
        assertThat(meterRepository.findFree(UtilityType.ELECTRICITY, contract.getDateEnd(), Pageable.unpaged()))
                .extracting(Meter::getId)
                .containsExactly(meterId);
    }

    @Test
    void deleteRefusedWhenContractHasInvoices() {
        Contract contract = data.contract();
        data.invoice(contract);

        assertThatThrownBy(() -> contractService.delete(contract.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("has invoices");
    }

    @Test
    void findByCustomerReturnsCustomerContracts() {
        Contract contract = data.contract();
        data.contract();

        assertThat(contractService.findByCustomer(contract.getCustomer().getId()))
                .extracting(Contract::getId)
                .containsExactly(contract.getId());
        assertThat(contractService.findByCustomer(99L)).isEmpty();
    }

    @Test
    void findByUnitReturnsUnitContracts() {
        Contract contract = data.contract();
        data.contract();

        assertThat(contractService.findByUnit(contract.getUnit().getId()))
                .extracting(Contract::getId)
                .containsExactly(contract.getId());
        assertThat(contractService.findByUnit(99L)).isEmpty();
    }

    private Contract newContract(LocalDate dateStart, LocalDate dateEnd) {
        return Contract.builder().dateStart(dateStart).dateEnd(dateEnd).build();
    }
}
