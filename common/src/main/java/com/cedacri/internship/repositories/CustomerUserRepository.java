package com.cedacri.internship.repositories;

import com.cedacri.internship.entities.user.CustomerUser;
import com.cedacri.internship.repositories.specification.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerUserRepository extends BaseRepository<CustomerUser, Long> {
}
