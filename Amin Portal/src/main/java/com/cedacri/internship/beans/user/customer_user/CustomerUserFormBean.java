package com.cedacri.internship.beans.user.customer_user;

import com.cedacri.internship.edums.Gender;
import com.cedacri.internship.entities.Address;
import com.cedacri.internship.entities.contract.Contract;
import com.cedacri.internship.entities.user.CustomerUser;
import com.cedacri.internship.entities.user.Person;
import com.cedacri.internship.exceptions.BusinessException;
import com.cedacri.internship.services.contract.ContractService;
import com.cedacri.internship.services.user.CustomerUserService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component("customerUserFormBean")
@Scope("view")
public class CustomerUserFormBean {

    private static final String LIST_PAGE = "customer-users?faces-redirect=true";
    private static final String FORM_PAGE = "customer-user-form?faces-redirect=true&id=";

    private final CustomerUserService customerUserService;
    private final PasswordEncoder passwordEncoder;
    private final ContractService contractService;

    @Getter
    private Long id;
    @Getter
    private boolean notFound;
    @Getter
    private List<Contract> contracts = List.of();

    // account
    @Getter @Setter
    private String username;

    // person
    @Getter @Setter
    private String firstName;
    @Getter @Setter
    private String lastName;
    @Getter @Setter
    private String socialId;
    @Getter @Setter
    private LocalDate dateOfBirth;
    @Getter @Setter
    private Gender gender;
    @Getter @Setter
    private String phoneNumber;
    @Getter @Setter
    private String email;

    // address
    @Getter @Setter
    private String city;
    @Getter @Setter
    private String district;
    @Getter @Setter
    private String street;
    @Getter @Setter
    private String streetNumber;
    @Getter @Setter
    private String block;
    @Getter @Setter
    private String floor;
    @Getter @Setter
    private String apartment;

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

    public CustomerUserFormBean(CustomerUserService customerUserService, PasswordEncoder passwordEncoder,
                                ContractService contractService) {
        this.customerUserService = customerUserService;
        this.passwordEncoder = passwordEncoder;
        this.contractService = contractService;
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
     * ADD mode: create the customer (with person and address), then open it in edit mode.
     */
    public String create() {
        Address address = Address.builder().build();
        applyAddress(address);

        Person person = Person.builder()
                .address(address)
                .build();
        applyPerson(person);

        CustomerUser customer = CustomerUser.builder()
                .person(person)
                .build();
        // username / password live in BaseUser, so the builder doesn't cover them
        customer.setUsername(username.trim());
        customer.setPassword(passwordEncoder.encode(password));

        try {
            CustomerUser saved = customerUserService.create(customer);
            flash("Customer created", "Customer '" + saved.getUsername() + "' was created.");
            return FORM_PAGE + saved.getId();
        } catch (DataIntegrityViolationException e) {
            duplicateValue();
            return null;
        }
    }

    /**
     * EDIT mode: save username, person and address.
     */
    public void update() {
        try {
            customerUserService.update(id, c -> {
                c.setUsername(username.trim());
                applyPerson(c.getPerson());
                applyAddress(c.getPerson().getAddress());
            });
            info("Customer updated", "Changes were saved.");
        } catch (DataIntegrityViolationException e) {
            duplicateValue();
        }
    }

    /**
     * EDIT mode: called from the confirmation dialog, after the fields were validated.
     */
    public void changePassword() {
        customerUserService.update(id, c -> c.setPassword(passwordEncoder.encode(newPassword)));
        newPassword = null;
        confirmNewPassword = null;
        info("Password changed", "The new password is active.");
    }

    /**
     * EDIT mode: called after the delete confirmation. Person and address are removed by cascade.
     */
    public String delete() {
        try {
            customerUserService.delete(id);
            flash("Customer deleted", "Customer '" + username + "' was deleted.");
            return LIST_PAGE;
        } catch (BusinessException e) {
            error("Not deleted", e.getMessage());
            FacesContext.getCurrentInstance().validationFailed();
            return null;
        }
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

    public Gender[] getGenders() {
        return Gender.values();
    }

    public LocalDate getToday() {
        return LocalDate.now();
    }

    private void load() {
        CustomerUser customer = customerUserService.findById(id);
        username = customer.getUsername();

        Person p = customer.getPerson();
        firstName = p.getFirstName();
        lastName = p.getLastName();
        socialId = p.getSocialId();
        dateOfBirth = p.getDateOfBirth();
        gender = p.getGender();
        phoneNumber = p.getPhoneNumber();
        email = p.getEmail();

        Address a = p.getAddress();
        city = a.getCity();
        district = a.getDistrict();
        street = a.getStreet();
        streetNumber = a.getStreetNumber();
        block = a.getBlock();
        floor = a.getFloor();
        apartment = a.getApartment();

        contracts = contractService.findByCustomer(id);
    }

    private void applyPerson(Person p) {
        p.setFirstName(firstName.trim());
        p.setLastName(lastName.trim());
        p.setSocialId(blankToNull(socialId));   // unique column: store NULL, never ""
        p.setDateOfBirth(dateOfBirth);
        p.setGender(gender);
        p.setPhoneNumber(blankToNull(phoneNumber));
        p.setEmail(blankToNull(email));         // unique column: store NULL, never ""
    }

    private void applyAddress(Address a) {
        a.setCity(blankToNull(city));
        a.setDistrict(blankToNull(district));
        a.setStreet(blankToNull(street));
        a.setStreetNumber(blankToNull(streetNumber));
        a.setBlock(blankToNull(block));
        a.setFloor(blankToNull(floor));
        a.setApartment(blankToNull(apartment));
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private void markNotFound() {
        notFound = true;
        error("Customer not found", "The requested customer does not exist.");
    }

    /**
     * username, email and socialId are unique columns; the DB error doesn't say which one clashed.
     */
    private void duplicateValue() {
        error("Already in use", "Username, email or social ID is already used by another customer");
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

    /**
     * Info message that survives a redirect.
     */
    private void flash(String summary, String detail) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getFlash().setKeepMessages(true);
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }


}