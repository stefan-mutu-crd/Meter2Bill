package com.cedacri.internship.beans.user.customer_user;

import com.cedacri.internship.dtos.user.CustomerUserSearchDto;
import com.cedacri.internship.entities.user.CustomerUser;
import com.cedacri.internship.services.user.CustomerUserService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("customerUsersBean")
@Scope("view")
public class CustomerUserBean {

    private final CustomerUserService customerUserService;

    @Getter
    private CustomerUserSearchDto filter = new CustomerUserSearchDto();
    @Getter
    private Page<CustomerUser> result;

    public CustomerUserBean(CustomerUserService customerUserService) {
        this.customerUserService = customerUserService;
    }

    @PostConstruct
    private void init() {
        result = customerUserService.searchPage(filter);
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
        filter = new CustomerUserSearchDto();
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

    private void load() {
        result = customerUserService.searchPage(filter);
    }


}