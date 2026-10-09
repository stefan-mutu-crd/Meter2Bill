package com.cedacri.internship.entities;


import com.cedacri.internship.edums.TenantType;
import com.cedacri.internship.edums.UnitType;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "units")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(onlyExplicitlyIncluded = true)
public class Unit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @ToString.Include
    private UnitType unitType;

    /** Local name inside the parent: "Bl. 3", "Floor 2", "Ap. 12". */
    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    @ToString.Include
    private String name;

    /** Null for top-level units (condominium, standalone house/building). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Unit parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    @Setter(AccessLevel.NONE)                 // modify only through addChild/removeChild
    private List<Unit> children = new ArrayList<>();

    /** Only filled on root units; children inherit it via getEffectiveAddress(). */
    @Valid
    @Embedded
    private Address address;

    /** Null for container units (a building or floor has no tenant). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private CustomerUser tenant;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TenantType tenantType;

    // --- tree helpers: keep both sides of the relation in sync ---

    public void addChild(Unit child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Unit child) {
        children.remove(child);
        child.setParent(null);
    }

    public boolean isRoot() {
        return parent == null;
    }

    public Address getEffectiveAddress() {
        return address != null || parent == null ? address : parent.getEffectiveAddress();
    }

    /** "Bl. 3 / Floor 2 / Ap. 12" */
    public String getFullName() {
        return parent == null ? name : parent.getFullName() + " / " + name;
    }

    // --- identity ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Unit other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Unit.class.hashCode();
    }
}