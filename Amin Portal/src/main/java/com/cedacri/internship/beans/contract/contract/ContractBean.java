package com.cedacri.internship.beans.contract.contract;

import com.cedacri.internship.dtos.UnitSearchDto;
import com.cedacri.internship.dtos.contract.ContractSearchDto;
import com.cedacri.internship.dtos.contract.ContractTemplateSearchDto;
import com.cedacri.internship.dtos.user.CustomerUserSearchDto;
import com.cedacri.internship.entities.contract.Contract;
import com.cedacri.internship.services.UnitService;
import com.cedacri.internship.services.contract.ContractService;
import com.cedacri.internship.services.contract.ContractTemplateService;
import com.cedacri.internship.services.user.CustomerUserService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component("contractsBean")
@Scope("view")
public class ContractBean {

    private final ContractService contractService;
    private final CustomerUserService customerUserService;
    private final UnitService unitService;
    private final ContractTemplateService contractTemplateService;

    @Getter
    private ContractSearchDto filter = new ContractSearchDto();
    @Getter
    private Page<Contract> result;
    @Getter @Setter
    private Long customerId;
    @Getter @Setter
    private Long unitId;
    @Getter @Setter
    private Long contractTemplateId;
    @Getter
    private List<SelectItem> customers;
    @Getter
    private List<SelectItem> units;
    @Getter
    private List<SelectItem> contractTemplates;

    public ContractBean(ContractService contractService,
                        CustomerUserService customerUserService,
                        UnitService unitService,
                        ContractTemplateService contractTemplateService) {
        this.contractService = contractService;
        this.customerUserService = customerUserService;
        this.unitService = unitService;
        this.contractTemplateService = contractTemplateService;
    }

    @PostConstruct
    private void init() {
        customers = customerUserService.search(new CustomerUserSearchDto()).stream()
                .map(c -> new SelectItem(c.getId(), c.getUsername() + " – "
                        + c.getPerson().getFirstName() + " " + c.getPerson().getLastName()))
                .toList();
        units = unitService.search(new UnitSearchDto()).stream()
                .map(u -> new SelectItem(u.getId(), u.getName()))
                .toList();
        contractTemplates = contractTemplateService.search(new ContractTemplateSearchDto()).stream()
                .map(t -> new SelectItem(t.getId(), "#" + t.getId() + " " + t.getUtility().getType() + " – "
                        + t.getContractor().getCompanyName() + " – " + t.getPaymentPlanType()))
                .toList();

        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap();
        customerId = idParam(params.get("customerId"));
        unitId = idParam(params.get("unitId"));
        search();
    }

    public void search() {
        applySelections();
        filter.setPage(0);
        load();
    }

    public void reset() {
        int size = filter.getSize();
        filter = new ContractSearchDto();
        filter.setSize(size);
        customerId = null;
        unitId = null;
        contractTemplateId = null;
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

    public LocalDate getToday() {
        return LocalDate.now();
    }

    private void applySelections() {
        try {
            filter.setCustomer(customerId == null ? null : customerUserService.findById(customerId));
            filter.setUnit(unitId == null ? null : unitService.findById(unitId));
            filter.setContractTemplate(contractTemplateId == null ? null : contractTemplateService.findById(contractTemplateId));
        } catch (EntityNotFoundException e) {
            customerId = null;
            unitId = null;
            contractTemplateId = null;
            filter.setCustomer(null);
            filter.setUnit(null);
            filter.setContractTemplate(null);
        }
    }

    private Long idParam(String value) {
        return value != null && value.matches("\\d+") ? Long.valueOf(value) : null;
    }

    private void load() {
        result = contractService.searchPage(filter);
    }
}
