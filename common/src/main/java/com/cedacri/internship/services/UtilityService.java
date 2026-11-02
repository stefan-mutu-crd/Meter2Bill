package com.cedacri.internship.services;

import com.cedacri.internship.entities.Utility;

public interface UtilityService extends Operations<Utility, Long> {

    Utility create(Utility utility, Long contractorId);

    Utility update(Long id, Utility changes, Long contractorId);
}
