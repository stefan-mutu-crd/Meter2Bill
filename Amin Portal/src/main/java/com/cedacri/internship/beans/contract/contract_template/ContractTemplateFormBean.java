package com.cedacri.internship.beans.contract.contract_template;

import com.cedacri.internship.dtos.UtilitySearchDto;
import com.cedacri.internship.edums.PaymentPlanType;
import com.cedacri.internship.entities.Utility;
import com.cedacri.internship.entities.contract.ContractTemplate;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.services.UtilityService;
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

import java.util.List;

@Component("contractTemplateFormBean")
@Scope("view")
public class ContractTemplateFormBean {

    private static final String LIST_PAGE = "contract-templates?faces-redirect=true";
    private static final String FORM_PAGE = "contract-template-form?faces-redirect=true&id=";

    private final ContractTemplateService contractTemplateService;
    private final UtilityService utilityService;

    @Getter
    private Long id;
    @Getter
    private boolean notFound;

    @Getter
    private ContractTemplate contractTemplate = ContractTemplate.builder().build();
    @Getter @Setter
    private Long utilityId;
    @Getter
    private List<SelectItem> utilities;

    public ContractTemplateFormBean(ContractTemplateService contractTemplateService, UtilityService utilityService) {
        this.contractTemplateService = contractTemplateService;
        this.utilityService = utilityService;
    }

    @PostConstruct
    private void init() {
        utilities = utilityService.search(new UtilitySearchDto()).stream()
                .map(this::toUtilityItem)
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
            error("Contract template not found", "The requested contract template does not exist.");
        }
    }

    public String create() {
        try {
            ContractTemplate saved = contractTemplateService.create(contractTemplate, utilityId);
            flash("Contract template created", "Contract template #" + saved.getId() + " was created.");
            return FORM_PAGE + saved.getId();
        } catch (BusinessException e) {
            fail("Not saved", e.getMessage());
            return null;
        }
    }

    public void update() {
        try {
            contractTemplate = contractTemplateService.update(id, contractTemplate, utilityId);
            info("Contract template updated", "Changes were saved.");
        } catch (BusinessException e) {
            fail("Not saved", e.getMessage());
        }
    }

    public String delete() {
        try {
            contractTemplateService.delete(id);
            flash("Contract template deleted", "Contract template #" + id + " was deleted.");
            return LIST_PAGE;
        } catch (BusinessException e) {
            fail("Not deleted", e.getMessage());
            return null;
        }
    }

    public boolean isEditMode() {
        return id != null && !notFound;
    }

    public PaymentPlanType[] getPaymentPlanTypes() {
        return PaymentPlanType.values();
    }

    private void load() {
        contractTemplate = contractTemplateService.findById(id);
        utilityId = contractTemplate.getUtility().getId();
    }

    private SelectItem toUtilityItem(Utility utility) {
        return new SelectItem(utility.getId(), utility.getType() + " – " + utility.getContractor().getCompanyName());
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
