package com.cedacri.internship.services.user;

import com.cedacri.internship.entities.user.AdminUser;
import com.cedacri.internship.services.Operations;

public interface AdminUserService extends Operations<AdminUser, Long> {

    AdminUser findByUsername(String username);
}
