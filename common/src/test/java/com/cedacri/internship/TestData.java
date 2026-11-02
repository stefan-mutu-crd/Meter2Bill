package com.cedacri.internship;

import com.cedacri.internship.edums.AdminUserRole;
import com.cedacri.internship.edums.PaymentPlanType;
import com.cedacri.internship.edums.TenantType;
import com.cedacri.internship.edums.UnitOfMeasure;
import com.cedacri.internship.edums.UnitType;
import com.cedacri.internship.edums.UtilityType;
import com.cedacri.internship.entities.Address;
import com.cedacri.internship.entities.Meter;
import com.cedacri.internship.entities.Unit;
import com.cedacri.internship.entities.Utility;
import com.cedacri.internship.entities.contract.Contract;
import com.cedacri.internship.entities.contract.ContractTemplate;
import com.cedacri.internship.entities.contract.Contractor;
import com.cedacri.internship.entities.contract.finance.Invoice;
import com.cedacri.internship.entities.user.AdminUser;
import com.cedacri.internship.entities.user.CustomerUser;
import com.cedacri.internship.entities.user.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Transactional
public class TestData {

    private final AtomicInteger sequence = new AtomicInteger();
    private final JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public TestData(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void cleanDb() {
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_TYPE = 'BASE TABLE'",
                String.class);
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        tables.forEach(table -> jdbcTemplate.execute("TRUNCATE TABLE \"" + table + "\" RESTART IDENTITY"));
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    public AdminUser adminUser() {
        int n = next();
        AdminUser adminUser = AdminUser.builder()
                .fullName("Admin " + n)
                .role(AdminUserRole.MAINTAINER)
                .build();
        adminUser.setUsername("admin" + n);
        adminUser.setPassword("secret");
        return persist(adminUser);
    }

    public Address address() {
        return persist(Address.builder()
                .city("Chisinau")
                .street("Stefan cel Mare")
                .streetNumber(String.valueOf(next()))
                .build());
    }

    public CustomerUser customer() {
        int n = next();
        Person person = persist(Person.builder()
                .firstName("First" + n)
                .lastName("Last" + n)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address(address())
                .build());
        CustomerUser customer = CustomerUser.builder().person(person).build();
        customer.setUsername("customer" + n);
        customer.setPassword("secret");
        return persist(customer);
    }

    public Contractor contractor() {
        int n = next();
        return persist(Contractor.builder()
                .companyName("Company " + n)
                .legalName("Company " + n + " SRL")
                .fiscalCode("FC" + n)
                .owner(customer())
                .legalAddress(address())
                .iban("MD00BANK" + n)
                .bankName("Bank")
                .build());
    }

    public Contractor inactiveContractor() {
        Contractor contractor = contractor();
        contractor.setActive(false);
        return entityManager.merge(contractor);
    }

    public Utility utility() {
        return utility(UtilityType.ELECTRICITY, contractor());
    }

    public Utility utility(UtilityType type, Contractor contractor) {
        return persist(Utility.builder()
                .type(type)
                .unitOfMeasure(UnitOfMeasure.KW_HOUR)
                .contractor(contractor)
                .build());
    }

    public ContractTemplate template() {
        return template(utility());
    }

    public ContractTemplate template(Utility utility) {
        return persist(ContractTemplate.builder()
                .utility(utility)
                .contractor(utility.getContractor())
                .price(BigDecimal.TEN)
                .paymentPlanType(PaymentPlanType.MONTHLY)
                .durationInMonths(12)
                .build());
    }

    public Unit unit() {
        return unit(null);
    }

    public Unit unit(Unit parent) {
        return persist(Unit.builder()
                .unitType(UnitType.HOUSE)
                .name("Unit " + next())
                .parent(parent)
                .address(address())
                .build());
    }

    public Unit rentedUnit() {
        return persist(Unit.builder()
                .unitType(UnitType.HOUSE)
                .name("Unit " + next())
                .address(address())
                .tenant(customer())
                .tenantType(TenantType.OWNER)
                .build());
    }

    public Meter meter() {
        return meter(UtilityType.ELECTRICITY, LocalDate.now().plusYears(5));
    }

    public Meter meter(UtilityType type, LocalDate testedUntil) {
        return persist(Meter.builder()
                .utilityType(type)
                .manufacturer("Manufacturer")
                .serialNumber("SN-" + next())
                .secretKey("KEY-" + next())
                .testedUntil(testedUntil)
                .build());
    }

    public Contract contract() {
        Unit unit = rentedUnit();
        return contract(unit, template());
    }

    public Contract contract(Unit unit, ContractTemplate template) {
        LocalDate start = LocalDate.now();
        return persist(Contract.builder()
                .unit(unit)
                .customer(unit.getTenant())
                .contractTemplate(template)
                .meter(meter(template.getUtility().getType(), start.plusYears(5)))
                .dateStart(start)
                .dateEnd(start.plusYears(1).minusDays(1))
                .build());
    }

    public Invoice invoice(Contract contract) {
        LocalDate start = contract.getDateStart();
        return persist(Invoice.builder()
                .contract(contract)
                .invoiceNumber((long) next())
                .billingPeriodStart(start)
                .billingPeriodEnd(start.plusMonths(1))
                .billingDateLimit(start.plusMonths(2))
                .build());
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    private int next() {
        return sequence.incrementAndGet();
    }
}
