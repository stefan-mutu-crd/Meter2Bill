package com.cedacri.internship.services.impl;

import com.cedacri.internship.entities.user.AdminUser;
import com.cedacri.internship.models.specification.SearchCriteria;
import com.cedacri.internship.repositories.AdminUserRepository;
import com.cedacri.internship.services.AdminUserService;
import com.cedacri.internship.services.BaseAbstractService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.cedacri.internship.edums.specification.Operation.EQ;

@Service
public class AdminUserServiceImpl extends BaseAbstractService<AdminUser, Long> implements AdminUserService {

    private final AdminUserRepository adminUserRepository;

    public AdminUserServiceImpl(AdminUserRepository adminUserRepository) {
        super(adminUserRepository, AdminUser.class);
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    public AdminUser findByUsername(String username) {
        return adminUserRepository
                .searchOptional(List.of(
                        new SearchCriteria("username", EQ, username)
                )).orElseThrow(
                        () -> new EntityNotFoundException("Admin User with username: " + username + " can't be found."));
    }
}
