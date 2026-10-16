package com.cedacri.internship.beans;

import com.cedacri.internship.dtos.BaseSearchDto;
import com.cedacri.internship.edums.AdminUserRole;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public abstract class AbstractListBean<T extends BaseSearchDto> {

    @Getter
    private T filter;

    private Page<?> result;

    protected abstract void load();

    protected abstract T getNewFilter();



    public void search() {
        filter.setPage(0);
        load();
    }

    public void reset() {
        int size = filter.getSize();
        filter = getNewFilter();
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

    public AdminUserRole[] getRoles() {
        return AdminUserRole.values();
    }
}
