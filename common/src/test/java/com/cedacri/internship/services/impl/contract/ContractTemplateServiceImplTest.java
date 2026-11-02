package com.cedacri.internship.services.impl.contract;

import com.cedacri.internship.TestData;
import com.cedacri.internship.edums.PaymentPlanType;
import com.cedacri.internship.edums.UtilityType;
import com.cedacri.internship.entities.Utility;
import com.cedacri.internship.entities.contract.Contract;
import com.cedacri.internship.entities.contract.ContractTemplate;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.services.contract.ContractTemplateService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ContractTemplateServiceImplTest {

    @Autowired
    private ContractTemplateService contractTemplateService;

    @Autowired
    private TestData data;

    @BeforeEach
    void setUp() {
        data.cleanDb();
    }

    @Test
    void createSetsUtilityAndContractor() {
        Utility utility = data.utility();

        ContractTemplate created = contractTemplateService.create(newTemplate(BigDecimal.ONE), utility.getId());

        assertThat(created.getId()).isNotNull();
        assertThat(created.getUtility().getId()).isEqualTo(utility.getId());
        assertThat(created.getContractor().getId()).isEqualTo(utility.getContractor().getId());
    }

    @Test
    void createRefusedWhenPriceNotPositive() {
        Utility utility = data.utility();

        assertThatThrownBy(() -> contractTemplateService.create(newTemplate(BigDecimal.ZERO), utility.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Price must be greater than zero.");
    }

    @Test
    void createRefusedWhenContractorInactive() {
        Utility utility = data.utility(UtilityType.GAS, data.inactiveContractor());

        assertThatThrownBy(() -> contractTemplateService.create(newTemplate(BigDecimal.ONE), utility.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("is inactive");
    }

    @Test
    void updateChangesFields() {
        ContractTemplate template = data.template();

        contractTemplateService.update(template.getId(), newTemplate(new BigDecimal("25.50")), template.getUtility().getId());

        ContractTemplate updated = contractTemplateService.findById(template.getId());
        assertThat(updated.getPrice()).isEqualByComparingTo("25.50");
        assertThat(updated.getPaymentPlanType()).isEqualTo(PaymentPlanType.YEARLY);
        assertThat(updated.getDurationInMonths()).isEqualTo(24);
    }

    @Test
    void updateAllowedWhenKeepingUtilityOfInactiveContractor() {
        ContractTemplate template = data.template(data.utility(UtilityType.GAS, data.inactiveContractor()));

        contractTemplateService.update(template.getId(), newTemplate(new BigDecimal("2")), template.getUtility().getId());

        assertThat(contractTemplateService.findById(template.getId()).getPrice()).isEqualByComparingTo("2");
    }

    @Test
    void updateRefusedWhenNewUtilityHasInactiveContractor() {
        ContractTemplate template = data.template();
        Utility inactive = data.utility(UtilityType.GAS, data.inactiveContractor());

        assertThatThrownBy(() -> contractTemplateService.update(template.getId(), newTemplate(BigDecimal.ONE), inactive.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("is inactive");
    }

    @Test
    void deleteRemovesUnusedTemplate() {
        ContractTemplate template = data.template();

        contractTemplateService.delete(template.getId());

        assertThatThrownBy(() -> contractTemplateService.findById(template.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteRefusedWhenUsedByContract() {
        Contract contract = data.contract();
        Long templateId = contract.getContractTemplate().getId();

        assertThatThrownBy(() -> contractTemplateService.delete(templateId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("used by contracts");
    }

    private ContractTemplate newTemplate(BigDecimal price) {
        return ContractTemplate.builder()
                .price(price)
                .paymentPlanType(PaymentPlanType.YEARLY)
                .durationInMonths(24)
                .build();
    }
}
