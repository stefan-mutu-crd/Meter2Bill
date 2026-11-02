package com.cedacri.internship.beans.utility;

import com.cedacri.internship.dtos.UtilitySearchDto;
import com.cedacri.internship.edums.UnitOfMeasure;
import com.cedacri.internship.edums.UtilityType;
import com.cedacri.internship.entities.Utility;
import com.cedacri.internship.services.UtilityService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("utilitiesBean")
@Scope("view")
public class UtilityBean {

    private final UtilityService utilityService;

    @Getter
    private UtilitySearchDto filter = new UtilitySearchDto();
    @Getter
    private Page<Utility> result;

    public UtilityBean(UtilityService utilityService) {
        this.utilityService = utilityService;
    }

    @PostConstruct
    private void init() {
        load();
    }

    public void search() {
        filter.setPage(0);
        load();
    }

    public void reset() {
        int size = filter.getSize();
        filter = new UtilitySearchDto();
        filter.setSize(size);
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

    public UtilityType[] getTypes() {
        return UtilityType.values();
    }

    public UnitOfMeasure[] getUnits() {
        return UnitOfMeasure.values();
    }

    private void load() {
        result = utilityService.searchPage(filter);
    }
}
