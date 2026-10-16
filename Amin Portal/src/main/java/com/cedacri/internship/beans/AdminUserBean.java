package com.cedacri.internship.beans;

import com.cedacri.internship.dtos.AdminUserSearchDto;
import com.cedacri.internship.edums.AdminUserRole;
import com.cedacri.internship.entities.user.AdminUser;
import com.cedacri.internship.services.AdminUserService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("adminUsersBean")
@Scope("view")
public class AdminUserBean {

    private final AdminUserService adminUserService;

    @Getter
    private AdminUserSearchDto filter = new AdminUserSearchDto();
    @Getter
    private Page<AdminUser> result;

    public AdminUserBean(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PostConstruct
    private void init() {
        result = adminUserService.searchPage(filter);
    }

    // ---------- actions ----------

    /**
     * Apply filter, back to first page.
     */
    public void search() {
        filter.setPage(0);
        load();
    }

    /**
     * Clear filter (keep page size), back to first page.
     */
    public void reset() {
        int size = filter.getSize();
        filter = new AdminUserSearchDto();
        filter.setSize(size);
        load();
    }

    /**
     * Click on a column header: same column toggles direction, new column starts ASC.
     */
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


    //TODO de creat un componenet pentru filtru cu metodele de baza reutilizabile

    // ---------- view helpers ----------

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

    private void load() {
        result = adminUserService.searchPage(filter);
    }


}
