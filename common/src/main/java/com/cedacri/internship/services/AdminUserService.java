package com.cedacri.internship.services;

import com.cedacri.internship.entities.user.AdminUser;

public interface AdminUserService {

    AdminUser findByUsername(String username);
}
