package com.cedacri.internship.services.impl;

import com.cedacri.internship.TestData;
import com.cedacri.internship.edums.TenantType;
import com.cedacri.internship.edums.UnitType;
import com.cedacri.internship.entities.Address;
import com.cedacri.internship.entities.Unit;
import com.cedacri.internship.entities.contract.Contract;
import com.cedacri.internship.entities.user.CustomerUser;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.repositories.AddressRepository;
import com.cedacri.internship.services.UnitService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UnitServiceImplTest {

    @Autowired
    private UnitService unitService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TestData data;

    @BeforeEach
    void setUp() {
        data.cleanDb();
    }

    @Test
    void createGeneratesNameFromAddress() {
        Unit unit = newUnit(UnitType.APARTMENT, " ", fullAddress());

        Unit created = unitService.create(unit, null, null);

        assertThat(created.getName()).isEqualTo("Chisinau, Main 1, bl. A, fl. 2, ap. 3");
        assertThat(created.getAddress().getId()).isNotNull();
    }

    @Test
    void createKeepsOnlyAddressFieldsOfUnitType() {
        Unit created = unitService.create(newUnit(UnitType.HOUSE, "House", fullAddress()), null, null);

        Address address = addressRepository.findById(created.getAddress().getId()).orElseThrow();
        assertThat(address.getStreetNumber()).isEqualTo("1");
        assertThat(address.getBlock()).isNull();
        assertThat(address.getFloor()).isNull();
        assertThat(address.getApartment()).isNull();
    }

    @Test
    void createRefusedWhenSiblingHasSameName() {
        Unit parent = data.unit();
        Unit sibling = data.unit(parent);

        assertThatThrownBy(() -> unitService.create(newUnit(UnitType.HOUSE, sibling.getName().toUpperCase(), null), parent.getId(), null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void createRefusedWhenTenantWithoutTenantType() {
        CustomerUser tenant = data.customer();

        assertThatThrownBy(() -> unitService.create(newUnit(UnitType.HOUSE, "House", null), null, tenant.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Tenant type is required");
    }

    @Test
    void createRefusedWithoutNameAndAddress() {
        assertThatThrownBy(() -> unitService.create(newUnit(UnitType.HOUSE, null, null), null, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Enter a name");
    }

    @Test
    void updateChangesNameParentAndTenant() {
        Unit unit = data.unit();
        Unit parent = data.unit();
        CustomerUser tenant = data.customer();
        Unit changes = newUnit(UnitType.HOUSE, "Renamed", null);
        changes.setTenantType(TenantType.RENTER);

        unitService.update(unit.getId(), changes, parent.getId(), tenant.getId());

        Unit updated = unitService.findById(unit.getId());
        assertThat(updated.getName()).isEqualTo("Renamed");
        assertThat(updated.getParent().getId()).isEqualTo(parent.getId());
        assertThat(updated.getTenant().getId()).isEqualTo(tenant.getId());
        assertThat(updated.getTenantType()).isEqualTo(TenantType.RENTER);
    }

    @Test
    void updateClearsTenantTypeWithoutTenant() {
        Unit unit = data.rentedUnit();
        Unit changes = newUnit(UnitType.HOUSE, unit.getName(), null);
        changes.setTenantType(TenantType.OWNER);

        unitService.update(unit.getId(), changes, null, null);

        Unit updated = unitService.findById(unit.getId());
        assertThat(updated.getTenant()).isNull();
        assertThat(updated.getTenantType()).isNull();
    }

    @Test
    void updateRefusedWhenParentIsDescendant() {
        Unit unit = data.unit();
        Unit child = data.unit(unit);

        assertThatThrownBy(() -> unitService.update(unit.getId(), newUnit(UnitType.HOUSE, "Loop", null), child.getId(), null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cannot be placed under itself");
    }

    @Test
    void deleteRemovesUnitAndAddress() {
        Unit unit = data.unit();
        Long addressId = unit.getAddress().getId();

        unitService.delete(unit.getId());

        assertThatThrownBy(() -> unitService.findById(unit.getId()))
                .isInstanceOf(EntityNotFoundException.class);
        assertThat(addressRepository.existsById(addressId)).isFalse();
    }

    @Test
    void deleteRefusedWhenUnitHasChildren() {
        Unit unit = data.unit();
        data.unit(unit);

        assertThatThrownBy(() -> unitService.delete(unit.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("has child units");
    }

    @Test
    void deleteRefusedWhenUnitHasContracts() {
        Contract contract = data.contract();
        Long unitId = contract.getUnit().getId();

        assertThatThrownBy(() -> unitService.delete(unitId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("used by contracts");
    }

    @Test
    void copyParentAddressReturnsUnsavedCopy() {
        Unit parent = data.unit();

        Address copy = unitService.copyParentAddress(parent.getId());

        assertThat(copy.getId()).isNull();
        assertThat(copy.getCity()).isEqualTo(parent.getAddress().getCity());
        assertThat(copy.getStreetNumber()).isEqualTo(parent.getAddress().getStreetNumber());
    }

    @Test
    void copyParentAddressThrowsWhenParentMissing() {
        assertThatThrownBy(() -> unitService.copyParentAddress(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findChildrenReturnsDirectChildrenOnly() {
        Unit root = data.unit();
        Unit child = data.unit(root);
        data.unit(child);

        assertThat(unitService.findChildren(root.getId())).extracting(Unit::getId).containsExactly(child.getId());
        assertThat(unitService.findChildren(child.getId())).hasSize(1);
    }

    @Test
    void findAncestorsReturnsPathFromRoot() {
        Unit root = data.unit();
        Unit child = data.unit(root);
        Unit grandChild = data.unit(child);

        assertThat(unitService.findAncestors(grandChild.getId())).extracting(Unit::getId)
                .containsExactly(root.getId(), child.getId());
        assertThat(unitService.findAncestors(root.getId())).isEmpty();
    }

    @Test
    void findParentCandidatesExcludesSelfAndDescendants() {
        Unit root = data.unit();
        Unit child = data.unit(root);
        Unit other = data.unit();

        assertThat(unitService.findParentCandidates(root.getId())).extracting(Unit::getId)
                .containsExactlyInAnyOrder(other.getId());
        assertThat(unitService.findParentCandidates(null)).hasSize(3);
        assertThat(unitService.findParentCandidates(child.getId())).extracting(Unit::getId)
                .containsExactlyInAnyOrder(root.getId(), other.getId());
    }

    private Unit newUnit(UnitType type, String name, Address address) {
        return Unit.builder().unitType(type).name(name).address(address).build();
    }

    private Address fullAddress() {
        return Address.builder()
                .city("Chisinau")
                .street("Main")
                .streetNumber("1")
                .block("A")
                .floor("2")
                .apartment("3")
                .build();
    }
}
