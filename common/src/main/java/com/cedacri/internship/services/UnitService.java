package com.cedacri.internship.services;

import com.cedacri.internship.entities.Address;
import com.cedacri.internship.entities.Unit;

import java.util.List;

public interface UnitService extends Operations<Unit, Long> {

    Unit create(Unit unit, Long parentId, Long tenantId);

    Unit update(Long id, Unit changes, Long parentId, Long tenantId);

    Address copyParentAddress(Long parentId);

    List<Unit> findChildren(Long id);

    List<Unit> findAncestors(Long id);

    List<Unit> findParentCandidates(Long id);
}
