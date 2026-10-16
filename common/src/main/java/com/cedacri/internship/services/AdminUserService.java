package com.cedacri.internship.services;

import com.cedacri.internship.entities.user.AdminUser;

public interface AdminUserService extends Operations<AdminUser, Long> {

    AdminUser findByUsername(String username);
}
