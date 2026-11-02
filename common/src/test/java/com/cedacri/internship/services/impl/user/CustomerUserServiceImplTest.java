package com.cedacri.internship.services.impl.user;

import com.cedacri.internship.TestData;
import com.cedacri.internship.entities.contract.Contract;
import com.cedacri.internship.entities.user.CustomerUser;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.services.user.CustomerUserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CustomerUserServiceImplTest {

    @Autowired
    private CustomerUserService customerUserService;

    @Autowired
    private TestData data;

    @BeforeEach
    void setUp() {
        data.cleanDb();
    }

    @Test
    void deleteRemovesCustomerWithoutContracts() {
        CustomerUser customer = data.customer();

        customerUserService.delete(customer.getId());

        assertThatThrownBy(() -> customerUserService.findById(customer.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteRefusedWhenCustomerHasContracts() {
        Contract contract = data.contract();
        Long customerId = contract.getCustomer().getId();

        assertThatThrownBy(() -> customerUserService.delete(customerId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("has contracts");
    }

    @Test
    void deleteThrowsWhenCustomerMissing() {
        assertThatThrownBy(() -> customerUserService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
