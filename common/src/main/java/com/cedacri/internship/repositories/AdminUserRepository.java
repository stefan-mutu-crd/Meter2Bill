package com.cedacri.internship.repositories;

import com.cedacri.internship.entities.user.AdminUser;
import com.cedacri.internship.repositories.specification.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserRepository extends BaseRepository<AdminUser, Long> {

    AdminUser findByUsername(String username);

}
