package com.cedacri.internship.beans.contract.contract;

import com.cedacri.internship.dtos.UnitSearchDto;
import com.cedacri.internship.dtos.contract.ContractTemplateSearchDto;
import com.cedacri.internship.entities.Unit;
import com.cedacri.internship.entities.contract.Contract;
import com.cedacri.internship.entities.contract.ContractTemplate;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.services.UnitService;
import com.cedacri.internship.services.contract.ContractService;
import com.cedacri.internship.services.contract.ContractTemplateService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component("contractFormBean")
@Scope("view")
public class ContractFormBean {

    private static final String LIST_PAGE = "contracts?faces-redirect=true";
    private static final String FORM_PAGE = "contract-form?faces-redirect=true&id=";

    private final ContractService contractService;
    private final UnitService unitService;
    private final ContractTemplateService contractTemplateService;

    @Getter
    private Long id;
    @Getter
    private boolean notFound;

    @Getter
    private Contract contract = Contract.builder().dateStart(LocalDate.now()).build();
    @Getter @Setter
    private Long unitId;
    @Getter @Setter
    private Long contractTemplateId;
    @Getter
    private List<SelectItem> units;
    @Getter
    private List<SelectItem> contractTemplates;

    public ContractFormBean(ContractService contractService,
                            UnitService unitService,
                            ContractTemplateService contractTemplateService) {
        this.contractService = contractService;
        this.unitService = unitService;
        this.contractTemplateService = contractTemplateService;
    }

    @PostConstruct
    private void init() {
        contractTemplates = contractTemplateService.search(new ContractTemplateSearchDto()).stream()
                .map(this::toTemplateItem)
                .toList();

        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap();
        String idParam = params.get("id");

        if (idParam == null || idParam.isBlank()) {
            units = unitService.search(new UnitSearchDto()).stream()
                    .map(this::toUnitItem)
                    .toList();
            String unitParam = params.get("unitId");
            unitId = unitParam != null && unitParam.matches("\\d+") ? Long.valueOf(unitParam) : null;
            return;
        }

        try {
            id = Long.valueOf(idParam.trim());
            load();
        } catch (NumberFormatException | EntityNotFoundException e) {
            notFound = true;
            error("Contract not found", "The requested contract does not exist.");
        }
    }

    public String create() {
        try {
            Contract saved = contractService.create(contract, unitId, contractTemplateId);
            flash("Contract created", "Contract #" + saved.getId() + " was created with meter "
                    + saved.getMeter().getSerialNumber() + ".");
            return FORM_PAGE + saved.getId();
        } catch (BusinessException e) {
            fail("Not saved", e.getMessage());
            return null;
        }
    }

    public void update() {
        try {
            contractService.update(id, contract, contractTemplateId);
            load();
            info("Contract updated", "Changes were saved.");
        } catch (BusinessException e) {
            fail("Not saved", e.getMessage());
        }
    }

    public String delete() {
        try {
            contractService.delete(id);
            flash("Contract deleted", "Contract #" + id + " was deleted. Its meter is free again.");
            return LIST_PAGE;
        } catch (BusinessException e) {
            fail("Not deleted", e.getMessage());
            return null;
        }
    }

    public boolean isEditMode() {
        return id != null && !notFound;
    }

    public LocalDate getToday() {
        return LocalDate.now();
    }

    private void load() {
        contract = contractService.findById(id);
        unitId = contract.getUnit().getId();
        contractTemplateId = contract.getContractTemplate().getId();
    }

    private SelectItem toUnitItem(Unit unit) {
        String tenant = unit.getTenant() == null ? "no tenant" : unit.getTenant().getUsername();
        return new SelectItem(unit.getId(), unit.getName() + " – " + tenant);
    }

    private SelectItem toTemplateItem(ContractTemplate template) {
        return new SelectItem(template.getId(), "#" + template.getId() + " " + template.getUtility().getType()
                + " – " + template.getContractor().getCompanyName()
                + " – " + template.getPaymentPlanType() + ", " + template.getDurationInMonths() + " months");
    }

    private void fail(String summary, String detail) {
        error(summary, detail);
        FacesContext.getCurrentInstance().validationFailed();
    }

    private void info(String summary, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    private void error(String summary, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    private void flash(String summary, String detail) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getFlash().setKeepMessages(true);
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }
}
