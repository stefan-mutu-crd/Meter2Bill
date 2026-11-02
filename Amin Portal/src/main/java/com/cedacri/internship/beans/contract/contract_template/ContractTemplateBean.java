package com.cedacri.internship.beans.contract.contract_template;

import com.cedacri.internship.dtos.contract.ContractTemplateSearchDto;
import com.cedacri.internship.edums.PaymentPlanType;
import com.cedacri.internship.entities.contract.ContractTemplate;
import com.cedacri.internship.services.contract.ContractTemplateService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("contractTemplatesBean")
@Scope("view")
public class ContractTemplateBean {

    private final ContractTemplateService contractTemplateService;

    @Getter
    private ContractTemplateSearchDto filter = new ContractTemplateSearchDto();
    @Getter
    private Page<ContractTemplate> result;

    public ContractTemplateBean(ContractTemplateService contractTemplateService) {
        this.contractTemplateService = contractTemplateService;
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
        filter = new ContractTemplateSearchDto();
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

    public PaymentPlanType[] getPaymentPlanTypes() {
        return PaymentPlanType.values();
    }

    private void load() {
        result = contractTemplateService.searchPage(filter);
    }
}
