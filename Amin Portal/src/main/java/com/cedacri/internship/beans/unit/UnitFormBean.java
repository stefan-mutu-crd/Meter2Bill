package com.cedacri.internship.beans.unit;

import com.cedacri.internship.dtos.user.CustomerUserSearchDto;
import com.cedacri.internship.edums.TenantType;
import com.cedacri.internship.edums.UnitType;
import com.cedacri.internship.entities.Address;
import com.cedacri.internship.entities.Unit;
import com.cedacri.internship.entities.user.CustomerUser;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.services.UnitService;
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
import java.util.Map;

@Component("unitFormBean")
@Scope("view")
public class UnitFormBean {

    private static final String LIST_PAGE = "units?faces-redirect=true";
    private static final String FORM_PAGE = "unit-form?faces-redirect=true&id=";

    private final UnitService unitService;
    private final CustomerUserService customerUserService;

    @Getter
    private Long id;
    @Getter
    private boolean notFound;

    @Getter
    private Unit unit = Unit.builder()
            .address(Address.builder().build())
            .build();
    @Getter @Setter
    private Long parentId;
    @Getter @Setter
    private Long tenantId;
    @Getter
    private List<SelectItem> parents;
    @Getter
    private List<SelectItem> tenants;
    @Getter
    private List<Unit> ancestors = List.of();
    @Getter
    private List<Unit> children = List.of();

    public UnitFormBean(UnitService unitService, CustomerUserService customerUserService) {
        this.unitService = unitService;
        this.customerUserService = customerUserService;
    }

    @PostConstruct
    private void init() {
        tenants = customerUserService.search(new CustomerUserSearchDto()).stream()
                .map(this::toTenantItem)
                .toList();

        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap();
        String idParam = params.get("id");

        if (idParam == null || idParam.isBlank()) {
            preselectParent(params.get("parentId"));
            loadParents();
            return;
        }

        try {
            id = Long.valueOf(idParam.trim());
            load();
        } catch (NumberFormatException | EntityNotFoundException e) {
            notFound = true;
            error("Unit not found", "The requested unit does not exist.");
        }
    }

    public void onParentChange() {
        if (!isEditMode() && parentId != null) {
            unit.setAddress(unitService.copyParentAddress(parentId));
        }
    }

    public String create() {
        try {
            Unit saved = unitService.create(unit, parentId, tenantId);
            flash("Unit created", "Unit '" + saved.getName() + "' was created.");
            return FORM_PAGE + saved.getId();
        } catch (BusinessException e) {
            fail("Not saved", e.getMessage());
            return null;
        }
    }

    public void update() {
        try {
            unitService.update(id, unit, parentId, tenantId);
            load();
            info("Unit updated", "Changes were saved.");
        } catch (BusinessException e) {
            fail("Not saved", e.getMessage());
        }
    }

    public String delete() {
        try {
            unitService.delete(id);
            flash("Unit deleted", "Unit '" + unit.getName() + "' was deleted.");
            return LIST_PAGE;
        } catch (BusinessException e) {
            fail("Not deleted", e.getMessage());
            return null;
        }
    }

    public boolean isEditMode() {
        return id != null && !notFound;
    }

    public UnitType[] getUnitTypes() {
        return UnitType.values();
    }

    public TenantType[] getTenantTypes() {
        return TenantType.values();
    }

    private void preselectParent(String parentParam) {
        if (parentParam == null || !parentParam.matches("\\d+")) {
            return;
        }
        try {
            Long candidateId = Long.valueOf(parentParam);
            unit.setAddress(unitService.copyParentAddress(candidateId));
            parentId = candidateId;
        } catch (EntityNotFoundException e) {
            error("Parent not found", "The requested parent unit does not exist.");
        }
    }

    private void load() {
        unit = unitService.findById(id);
        parentId = unit.getParent() == null ? null : unit.getParent().getId();
        tenantId = unit.getTenant() == null ? null : unit.getTenant().getId();
        ancestors = unitService.findAncestors(id);
        children = unitService.findChildren(id);
        loadParents();
    }

    private void loadParents() {
        parents = unitService.findParentCandidates(id).stream()
                .map(candidate -> new SelectItem(candidate.getId(), candidate.getName()))
                .toList();
    }

    private SelectItem toTenantItem(CustomerUser customer) {
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
