package com.cedacri.internship.services.impl.user;

import com.cedacri.internship.entities.user.CustomerUser;
import com.cedacri.internship.repositories.CustomerUserRepository;
import com.cedacri.internship.services.BaseAbstractService;
import com.cedacri.internship.services.user.CustomerUserService;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserServiceImpl extends BaseAbstractService<CustomerUser, Long> implements CustomerUserService {

    private final CustomerUserRepository customerUserRepository;

    public CustomerUserServiceImpl(CustomerUserRepository customerUserRepository) {
        super(customerUserRepository, CustomerUser.class);
        this.customerUserRepository = customerUserRepository;
    }
}
