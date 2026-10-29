package com.cedacri.internship.beans.user.admin_user;

import com.cedacri.internship.edums.AdminUserRole;
import com.cedacri.internship.entities.user.AdminUser;
import com.cedacri.internship.services.user.AdminUserService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("adminUserFormBean")
@Scope("view")
public class AdminUserFormBean {

    private static final String LIST_PAGE = "admin-users?faces-redirect=true";
    private static final String FORM_PAGE = "admin-user-form?faces-redirect=true&id=";

    private final AdminUserService adminUserService;
    private final PasswordEncoder passwordEncoder;

    @Getter
    private Long id;
    @Getter
    private boolean notFound;

    // details
    @Getter @Setter
    private String fullName;
    @Getter @Setter
    private String username;
    @Getter @Setter
    private AdminUserRole role;

    // ADD mode passwords
    @Getter @Setter
    private String password;
    @Getter @Setter
    private String confirmPassword;

    // EDIT mode password change
    @Getter @Setter
    private String newPassword;
    @Getter @Setter
    private String confirmNewPassword;

    public AdminUserFormBean(AdminUserService adminUserService, PasswordEncoder passwordEncoder) {
        this.adminUserService = adminUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void init() {
        String idParam = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("id");

        if (idParam == null || idParam.isBlank()) {
            return; // ADD mode
        }

        try {
            id = Long.valueOf(idParam.trim());
            load();
        } catch (NumberFormatException | EntityNotFoundException e) {
            markNotFound();
        }
    }

    // ---------- actions ----------

    /**
     * ADD mode: create the user, then open it in edit mode.
     */
    public String create() {
        AdminUser user = AdminUser.builder()
                .fullName(fullName.trim())
                .role(role)
                .build();
        // username / password live in BaseUser, so the builder doesn't cover them
        user.setUsername(username.trim());
        user.setPassword(passwordEncoder.encode(password));

        try {
            AdminUser saved = adminUserService.create(user);
            flash("User created", "User '" + saved.getUsername() + "' was created.");
            return FORM_PAGE + saved.getId();
        } catch (DataIntegrityViolationException e) {
            usernameTaken();
            return null;
        }
    }

    /**
     * EDIT mode: save full name, username, role.
     */
    public void update() {
        try {
            adminUserService.update(id, u -> {
                u.setFullName(fullName.trim());
                u.setUsername(username.trim());
                u.setRole(role);
            });
            info("User updated", "Changes were saved.");
        } catch (DataIntegrityViolationException e) {
            usernameTaken();
        }
    }

    /**
     * EDIT mode: called from the confirmation dialog, after the fields were validated.
     */
    public void changePassword() {
        adminUserService.update(id, u -> u.setPassword(passwordEncoder.encode(newPassword)));
        newPassword = null;
        confirmNewPassword = null;
        info("Password changed", "The new password is active.");
    }

    /**
     * EDIT mode: called after the delete confirmation.
     */
    public String delete() {
        adminUserService.delete(id);
        flash("User deleted", "User '" + username + "' was deleted.");
        return LIST_PAGE;
    }

    /**
     * Confirm-password validator: the confirm field points to the original one
     * with f:attribute name="matchWith"; both must be processed in the same request.
     */
    public void validatePasswordMatch(FacesContext ctx, UIComponent component, Object value) {
        String otherId = (String) component.getAttributes().get("matchWith");
        UIComponent found = component.findComponent(otherId);
        if (!(found instanceof UIInput other) || !other.isValid()) {
            return; // the original field already shows its own error
        }
        if (!Objects.equals(other.getValue(), value)) {
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Passwords do not match", "Passwords do not match"));
        }
    }

    // ---------- view helpers ----------

    public boolean isEditMode() {
        return id != null && !notFound;
    }

    public AdminUserRole[] getRoles() {
        return AdminUserRole.values();
    }

    private void load() {
        AdminUser user = adminUserService.findById(id);
        fullName = user.getFullName();
        username = user.getUsername();
        role = user.getRole();
    }

    private void markNotFound() {
        notFound = true;
        error(null, "User not found", "The requested user does not exist.");
    }

    private void usernameTaken() {
        error("userForm:username", "Username already exists", "Username already exists");
        FacesContext.getCurrentInstance().validationFailed();
    }

    private void info(String summary, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    private void error(String clientId, String summary, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    /**
     * Info message that survives a redirect.
     */
    private void flash(String summary, String detail) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getFlash().setKeepMessages(true);
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }


}