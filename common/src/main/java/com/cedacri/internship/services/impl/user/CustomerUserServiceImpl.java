package com.cedacri.internship.services.impl.user;

import com.cedacri.internship.entities.user.CustomerUser;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.repositories.ContractRepository;
import com.cedacri.internship.repositories.CustomerUserRepository;
import com.cedacri.internship.services.BaseAbstractService;
import com.cedacri.internship.services.user.CustomerUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerUserServiceImpl extends BaseAbstractService<CustomerUser, Long> implements CustomerUserService {

    private final CustomerUserRepository customerUserRepository;
    private final ContractRepository contractRepository;

    public CustomerUserServiceImpl(CustomerUserRepository customerUserRepository,
                                   ContractRepository contractRepository) {
        super(customerUserRepository, CustomerUser.class);
        this.customerUserRepository = customerUserRepository;
        this.contractRepository = contractRepository;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (contractRepository.existsByCustomerId(id)) {
            throw new BusinessException("Customer has contracts and cannot be deleted.");
        }
        super.delete(id);
    }
}
