package com.cedacri.internship.beans.utility;

import com.cedacri.internship.dtos.contract.ContractorSearchDto;
import com.cedacri.internship.edums.UnitOfMeasure;
import com.cedacri.internship.edums.UtilityType;
import com.cedacri.internship.entities.Utility;
import com.cedacri.internship.entities.contract.Contractor;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.services.UtilityService;
import com.cedacri.internship.services.contract.ContractorService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("utilityFormBean")
@Scope("view")
public class UtilityFormBean {

    private static final String LIST_PAGE = "utilities?faces-redirect=true";
    private static final String FORM_PAGE = "utility-form?faces-redirect=true&id=";

    private final UtilityService utilityService;
    private final ContractorService contractorService;

    @Getter
    private Long id;
    @Getter
    private boolean notFound;

    @Getter
    private Utility utility = Utility.builder().build();
    @Getter @Setter
    private Long contractorId;
    @Getter
    private List<SelectItem> contractors;

    public UtilityFormBean(UtilityService utilityService, ContractorService contractorService) {
        this.utilityService = utilityService;
        this.contractorService = contractorService;
    }

    @PostConstruct
    private void init() {
        contractors = contractorService.search(new ContractorSearchDto()).stream()
                .map(this::toContractorItem)
                .toList();

        String idParam = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("id");

        if (idParam == null || idParam.isBlank()) {
            return;
        }

        try {
            id = Long.valueOf(idParam.trim());
            load();
        } catch (NumberFormatException | EntityNotFoundException e) {
            notFound = true;
            error("Utility not found", "The requested utility does not exist.");
        }
    }

    public String create() {
        try {
            Utility saved = utilityService.create(utility, contractorId);
            flash("Utility created", "Utility '" + saved.getType() + "' was created.");
            return FORM_PAGE + saved.getId();
        } catch (BusinessException e) {
            fail("Not saved", e.getMessage());
            return null;
        }
    }

    public void update() {
        try {
            utilityService.update(id, utility, contractorId);
            info("Utility updated", "Changes were saved.");
        } catch (BusinessException e) {
            fail("Not saved", e.getMessage());
        }
    }

    public String delete() {
        try {
            utilityService.delete(id);
            flash("Utility deleted", "Utility '" + utility.getType() + "' was deleted.");
            return LIST_PAGE;
        } catch (BusinessException e) {
            fail("Not deleted", e.getMessage());
            return null;
        }
    }

    public boolean isEditMode() {
        return id != null && !notFound;
    }

    public UtilityType[] getTypes() {
        return UtilityType.values();
    }

    public UnitOfMeasure[] getUnits() {
        return UnitOfMeasure.values();
    }

    private void load() {
        utility = utilityService.findById(id);
        contractorId = utility.getContractor().getId();
    }

    private SelectItem toContractorItem(Contractor contractor) {
        return new SelectItem(contractor.getId(), contractor.getCompanyName() + " – " + contractor.getFiscalCode());
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
