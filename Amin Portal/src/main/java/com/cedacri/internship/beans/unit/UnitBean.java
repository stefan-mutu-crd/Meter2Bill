package com.cedacri.internship.beans.unit;

import com.cedacri.internship.dtos.UnitSearchDto;
import com.cedacri.internship.edums.TenantType;
import com.cedacri.internship.edums.UnitType;
import com.cedacri.internship.entities.Unit;
import com.cedacri.internship.services.UnitService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("unitsBean")
@Scope("view")
public class UnitBean {

    private final UnitService unitService;

    @Getter
    private UnitSearchDto filter = new UnitSearchDto();
    @Getter
    private Page<Unit> result;
    @Getter @Setter
    private Long parentId;
    @Getter
    private List<SelectItem> parents;

    public UnitBean(UnitService unitService) {
        this.unitService = unitService;
    }

    @PostConstruct
    private void init() {
        parents = unitService.findParentCandidates(null).stream()
                .map(unit -> new SelectItem(unit.getId(), unit.getName()))
                .toList();

        String parentParam = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("parentId");

        if (parentParam != null && parentParam.matches("\\d+")) {
            parentId = Long.valueOf(parentParam);
        }
        applyParent();
        load();
    }

    public void search() {
        applyParent();
        filter.setPage(0);
        load();
    }

    public void reset() {
        int size = filter.getSize();
        filter = new UnitSearchDto();
        filter.setSize(size);
        parentId = null;
        load();
    }

    public void sortBy(String field) {
        if (field.equals(filter.getSort())) {
            filter.setDirection(filter.getDirection() == Sort.Direction.ASC
                    ? Sort.Direction.DESC : Sort.Direction.ASC);
        } else {
            filter.setSort(field);
            filter.setDirection(Sort.Direction.ASC);
        }
        filter.setPage(0);
        load();
    }

    public void goToPage(int page) {
        filter.setPage(Math.max(0, Math.min(page, getLastPage())));
        load();
    }

    public void changeSize() {
        filter.setPage(0);
        load();
    }

    public String sortIcon(String field) {
        if (!field.equals(filter.getSort())) {
            return "pi pi-sort-alt";
        }
        return filter.getDirection() == Sort.Direction.ASC
                ? "pi pi-sort-amount-up-alt" : "pi pi-sort-amount-down";
    }

    public int getLastPage() {
        return Math.max(0, result.getTotalPages() - 1);
    }

    public UnitType[] getUnitTypes() {
        return UnitType.values();
    }

    public TenantType[] getTenantTypes() {
        return TenantType.values();
    }

    private void applyParent() {
        try {
            filter.setParent(parentId == null ? null : unitService.findById(parentId));
        } catch (EntityNotFoundException e) {
            parentId = null;
            filter.setParent(null);
        }
    }

    private void load() {
        result = unitService.searchPage(filter);
    }
}
