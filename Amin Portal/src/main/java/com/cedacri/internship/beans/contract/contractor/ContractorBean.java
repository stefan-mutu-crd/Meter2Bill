package com.cedacri.internship.beans.contract.contractor;

import com.cedacri.internship.dtos.contract.ContractorSearchDto;
import com.cedacri.internship.entities.contract.Contractor;
import com.cedacri.internship.services.contract.ContractorService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("contractorsBean")
@Scope("view")
public class ContractorBean {

    private final ContractorService contractorService;

    @Getter
    private ContractorSearchDto filter = new ContractorSearchDto();
    @Getter
    private Page<Contractor> result;

    public ContractorBean(ContractorService contractorService) {
        this.contractorService = contractorService;
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
        filter = new ContractorSearchDto();
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

    private void load() {
        result = contractorService.searchPage(filter);
    }
}
