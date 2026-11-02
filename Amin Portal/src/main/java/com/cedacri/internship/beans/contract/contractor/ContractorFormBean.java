package com.cedacri.internship.beans.contract.contractor;

import com.cedacri.internship.dtos.user.CustomerUserSearchDto;
import com.cedacri.internship.entities.Address;
import com.cedacri.internship.entities.contract.Contractor;
import com.cedacri.internship.entities.user.CustomerUser;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.services.contract.ContractorService;
import com.cedacri.internship.services.user.CustomerUserService;
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

@Component("contractorFormBean")
@Scope("view")
public class ContractorFormBean {

    private static final String LIST_PAGE = "contractors?faces-redirect=true";
    private static final String FORM_PAGE = "contractor-form?faces-redirect=true&id=";

    private final ContractorService contractorService;
    private final CustomerUserService customerUserService;

    @Getter
    private Long id;
    @Getter
    private boolean notFound;

    @Getter
    private Contractor contractor = Contractor.builder()
            .legalAddress(Address.builder().build())
            .build();
    @Getter @Setter
    private Long ownerId;
    @Getter
    private List<SelectItem> owners;

    public ContractorFormBean(ContractorService contractorService, CustomerUserService customerUserService) {
        this.contractorService = contractorService;
        this.customerUserService = customerUserService;
    }

    @PostConstruct
    private void init() {
        owners = customerUserService.search(new CustomerUserSearchDto()).stream()
                .map(this::toOwnerItem)
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
            error("Contractor not found", "The requested contractor does not exist.");
        }
    }

    public String create() {
        try {
            Contractor saved = contractorService.create(contractor, ownerId);
            flash("Contractor created", "Contractor '" + saved.getCompanyName() + "' was created.");
            return FORM_PAGE + saved.getId();
        } catch (BusinessException e) {
            fail("Not saved", e.getMessage());
            return null;
        }
    }

    public void update() {
        try {
            contractorService.update(id, contractor, ownerId);
            info("Contractor updated", "Changes were saved.");
        } catch (BusinessException e) {
            fail("Not saved", e.getMessage());
        }
    }

    public String delete() {
        try {
            contractorService.delete(id);
            flash("Contractor deleted", "Contractor '" + contractor.getCompanyName() + "' was deleted.");
            return LIST_PAGE;
        } catch (BusinessException e) {
            fail("Not deleted", e.getMessage());
            return null;
        }
    }

    public boolean isEditMode() {
        return id != null && !notFound;
    }

    private void load() {
        contractor = contractorService.findById(id);
        ownerId = contractor.getOwner().getId();
    }

    private SelectItem toOwnerItem(CustomerUser customer) {
        return new SelectItem(customer.getId(), customer.getUsername() + " – "
                + customer.getPerson().getFirstName() + " " + customer.getPerson().getLastName());
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
