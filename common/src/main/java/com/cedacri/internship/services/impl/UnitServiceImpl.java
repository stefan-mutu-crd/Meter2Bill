package com.cedacri.internship.services.impl;

import com.cedacri.internship.edums.TenantType;
import com.cedacri.internship.edums.UnitType;
import com.cedacri.internship.entities.Address;
import com.cedacri.internship.entities.Unit;
import com.cedacri.internship.entities.user.CustomerUser;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.repositories.AddressRepository;
import com.cedacri.internship.repositories.ContractRepository;
import com.cedacri.internship.repositories.CustomerUserRepository;
import com.cedacri.internship.repositories.UnitRepository;
import com.cedacri.internship.services.BaseAbstractService;
import com.cedacri.internship.services.UnitService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UnitServiceImpl extends BaseAbstractService<Unit, Long> implements UnitService {

    private final UnitRepository unitRepository;
    private final AddressRepository addressRepository;
    private final CustomerUserRepository customerUserRepository;
    private final ContractRepository contractRepository;

    public UnitServiceImpl(UnitRepository unitRepository,
                           AddressRepository addressRepository,
                           CustomerUserRepository customerUserRepository,
                           ContractRepository contractRepository) {
        super(unitRepository, Unit.class);
        this.unitRepository = unitRepository;
        this.addressRepository = addressRepository;
        this.customerUserRepository = customerUserRepository;
        this.contractRepository = contractRepository;
    }

    @Override
    @Transactional
    public Unit create(Unit unit, Long parentId, Long tenantId) {
        UnitType unitType = requireUnitType(unit.getUnitType());
        Unit parent = findParent(parentId);
        CustomerUser tenant = findTenant(tenantId);
        Address address = unit.getAddress() == null ? Address.builder().build() : unit.getAddress();
        copyAddress(address, address, unitType);
        String name = resolveName(unit.getName(), address);
        ensureNameAvailable(name, parentId, null);

        unit.setName(name);
        unit.setParent(parent);
        unit.setTenant(tenant);
        unit.setTenantType(resolveTenantType(unit, tenant));
        unit.setAddress(addressRepository.save(address));
        return create(unit);
    }

    @Override
    @Transactional
    public Unit update(Long id, Unit changes, Long parentId, Long tenantId) {
        UnitType unitType = requireUnitType(changes.getUnitType());
        Unit parent = findParent(parentId);
        if (parent != null && isSelfOrDescendant(parent, id)) {
            throw new BusinessException("A unit cannot be placed under itself or one of its child units.");
        }
        CustomerUser tenant = findTenant(tenantId);
        Address addressChanges = changes.getAddress() == null ? Address.builder().build() : changes.getAddress();
        copyAddress(addressChanges, addressChanges, unitType);
        String name = resolveName(changes.getName(), addressChanges);
        ensureNameAvailable(name, parentId, id);
        TenantType tenantType = resolveTenantType(changes, tenant);

        return update(id, unit -> {
            unit.setUnitType(unitType);
            unit.setName(name);
            unit.setParent(parent);
            unit.setTenant(tenant);
            unit.setTenantType(tenantType);
            if (unit.getAddress() == null) {
                unit.setAddress(addressRepository.save(Address.builder().build()));
            }
            copyAddress(addressChanges, unit.getAddress(), unitType);
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (unitRepository.existsByParentId(id)) {
            throw new BusinessException("Unit has child units. Delete or move them first.");
        }
        if (contractRepository.existsByUnitId(id)) {
            throw new BusinessException("Unit is used by contracts.");
        }
        Unit unit = findById(id);
        Address address = unit.getAddress();
        unitRepository.delete(unit);
        unitRepository.flush();
        if (address != null) {
            addressRepository.delete(address);
        }
    }

    @Override
    public Address copyParentAddress(Long parentId) {
        Address source = findById(parentId).getAddress();
        if (source == null) {
            return Address.builder().build();
        }
        return Address.builder()
                .city(source.getCity())
                .district(source.getDistrict())
                .street(source.getStreet())
                .streetNumber(source.getStreetNumber())
                .block(source.getBlock())
                .floor(source.getFloor())
                .apartment(source.getApartment())
                .build();
    }

    @Override
    public List<Unit> findChildren(Long id) {
        return unitRepository.findByParentIdOrderByNameAsc(id);
    }

    @Override
    public List<Unit> findAncestors(Long id) {
        List<Unit> ancestors = new ArrayList<>();
        for (Unit current = findById(id).getParent(); current != null; current = current.getParent()) {
            ancestors.add(0, current);
        }
        return ancestors;
    }

    @Override
    public List<Unit> findParentCandidates(Long id) {
        List<Unit> units = unitRepository.findAll(Sort.by("name"));
        if (id == null) {
            return units;
        }
        return units.stream()
                .filter(unit -> !isSelfOrDescendant(unit, id))
                .toList();
    }

    private boolean isSelfOrDescendant(Unit unit, Long id) {
        for (Unit current = unit; current != null; current = current.getParent()) {
            if (id.equals(current.getId())) {
                return true;
            }
        }
        return false;
    }

    private UnitType requireUnitType(UnitType unitType) {
        if (unitType == null) {
            throw new BusinessException("Unit type is required.");
        }
        return unitType;
    }

    private Unit findParent(Long parentId) {
        if (parentId == null) {
            return null;
        }
        return unitRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException("Selected parent unit does not exist."));
    }

    private CustomerUser findTenant(Long tenantId) {
        if (tenantId == null) {
            return null;
        }
        return customerUserRepository.findById(tenantId)
                .orElseThrow(() -> new BusinessException("Selected tenant does not exist."));
    }

    private TenantType resolveTenantType(Unit unit, CustomerUser tenant) {
        if (tenant == null) {
            return null;
        }
        if (unit.getTenantType() == null) {
            throw new BusinessException("Tenant type is required when a tenant is selected.");
        }
        return unit.getTenantType();
    }

    private String resolveName(String name, Address address) {
        String resolved = blankToNull(name);
        if (resolved == null) {
            resolved = generateName(address);
        }
        if (resolved == null) {
            throw new BusinessException("Enter a name or an address to generate it from.");
        }
        return resolved;
    }

    private void ensureNameAvailable(String name, Long parentId, Long id) {
        boolean taken;
        if (parentId == null) {
            taken = id == null
                    ? unitRepository.existsByNameIgnoreCaseAndParentIsNull(name)
                    : unitRepository.existsByNameIgnoreCaseAndParentIsNullAndIdNot(name, id);
        } else {
            taken = id == null
                    ? unitRepository.existsByNameIgnoreCaseAndParentId(name, parentId)
                    : unitRepository.existsByNameIgnoreCaseAndParentIdAndIdNot(name, parentId, id);
        }
        if (taken) {
            throw new BusinessException("A unit named '" + name + "' already exists under the same parent.");
        }
    }

    private void copyAddress(Address source, Address target, UnitType unitType) {
        target.setCity(blankToNull(source.getCity()));
        target.setDistrict(blankToNull(source.getDistrict()));
        target.setStreet(blankToNull(source.getStreet()));
        target.setStreetNumber(unitType.isStreetNumber() ? blankToNull(source.getStreetNumber()) : null);
        target.setBlock(unitType.isBlock() ? blankToNull(source.getBlock()) : null);
        target.setFloor(unitType.isFloor() ? blankToNull(source.getFloor()) : null);
        target.setApartment(unitType.isApartment() ? blankToNull(source.getApartment()) : null);
    }

    private static String generateName(Address address) {
        return join(", ",
                address.getCity(),
                join(" ", address.getStreet(), address.getStreetNumber()),
                prefixed("bl. ", address.getBlock()),
                prefixed("fl. ", address.getFloor()),
                prefixed("ap. ", address.getApartment()));
    }

    private static String join(String separator, String... parts) {
        String joined = Stream.of(parts).filter(Objects::nonNull).collect(Collectors.joining(separator));
        return joined.isEmpty() ? null : joined;
    }

    private static String prefixed(String prefix, String value) {
        return value == null ? null : prefix + value;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
