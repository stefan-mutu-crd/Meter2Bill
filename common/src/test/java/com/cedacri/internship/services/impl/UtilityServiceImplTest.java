package com.cedacri.internship.services.impl;

import com.cedacri.internship.TestData;
import com.cedacri.internship.edums.UnitOfMeasure;
import com.cedacri.internship.edums.UtilityType;
import com.cedacri.internship.entities.Utility;
import com.cedacri.internship.entities.contract.Contractor;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.services.UtilityService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UtilityServiceImplTest {

    @Autowired
    private UtilityService utilityService;

    @Autowired
    private TestData data;

    @BeforeEach
    void setUp() {
        data.cleanDb();
    }

    @Test
    void createAssignsContractor() {
        Contractor contractor = data.contractor();

        Utility created = utilityService.create(newUtility(UtilityType.GAS), contractor.getId());

        assertThat(created.getId()).isNotNull();
        assertThat(created.getContractor().getId()).isEqualTo(contractor.getId());
    }

    @Test
    void createRefusedWhenContractorAlreadyProvidesType() {
        Contractor contractor = data.contractor();
        data.utility(UtilityType.GAS, contractor);

        assertThatThrownBy(() -> utilityService.create(newUtility(UtilityType.GAS), contractor.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already provides GAS");
    }

    @Test
    void createRefusedWithoutContractor() {
        assertThatThrownBy(() -> utilityService.create(newUtility(UtilityType.GAS), null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Contractor is required.");
    }

    @Test
    void updateChangesTypeAndContractor() {
        Utility utility = data.utility();
        Contractor other = data.contractor();

        utilityService.update(utility.getId(), newUtility(UtilityType.HEATING), other.getId());

        Utility updated = utilityService.findById(utility.getId());
        assertThat(updated.getType()).isEqualTo(UtilityType.HEATING);
        assertThat(updated.getContractor().getId()).isEqualTo(other.getId());
    }

    @Test
    void updateKeepsSameTypeForSameUtility() {
        Utility utility = data.utility();
        Long contractorId = utility.getContractor().getId();

        utilityService.update(utility.getId(), newUtility(utility.getType()), contractorId);

        assertThat(utilityService.findById(utility.getId()).getUnitOfMeasure()).isEqualTo(UnitOfMeasure.CUBIC_METER);
    }

    @Test
    void updateRefusedWhenTypeTakenByAnotherUtility() {
        Contractor contractor = data.contractor();
        Utility gas = data.utility(UtilityType.GAS, contractor);
        data.utility(UtilityType.HEATING, contractor);

        assertThatThrownBy(() -> utilityService.update(gas.getId(), newUtility(UtilityType.HEATING), contractor.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already provides HEATING");
    }

    @Test
    void deleteRemovesUnusedUtility() {
        Utility utility = data.utility();

        utilityService.delete(utility.getId());

        assertThatThrownBy(() -> utilityService.findById(utility.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteRefusedWhenUsedByTemplate() {
        Utility utility = data.utility();
        data.template(utility);

        assertThatThrownBy(() -> utilityService.delete(utility.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("used by contract templates");
    }

    private Utility newUtility(UtilityType type) {
        return Utility.builder().type(type).unitOfMeasure(UnitOfMeasure.CUBIC_METER).build();
    }
}
