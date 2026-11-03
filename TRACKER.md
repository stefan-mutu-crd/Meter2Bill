# Meter2Bill Tracker

## Stack
Java17, Spring Boot 4.1.1, Maven multi-module, Lombok, JPA/Postgres17 (docker-compose in `Amin Portal/`), Flyway, JoinFaces 6.1.1 + PrimeFaces (theme arya), Spring Security, RabbitMQ (spring-boot-starter-amqp, `rabbitmq:4-management` in docker-compose, AMQP 5672, UI 15672 guest/guest), MongoDB (spring-boot-starter-data-mongodb in `common`, `mongo:8` in docker-compose, 27017, root/admin, auth DB admin, db `meter_to_bill`; Boot 4 props `spring.mongodb.*`). Jackson 3 (`tools.jackson`).
Schema: Hibernate `ddl-auto=update`. Flyway = seed data only (`V1__init_schema.sql` admin users, pwd `ChangeMe123!`). `dump.sql` UTF-16, not a migration.
Base pkg: `com.cedacri.internship`.

## Modules
- `common` (jar): domain + data + services.
- `Amin Portal` (dir name typo, artifact `admin_portal`): JSF UI, security. Main: `AdminPortalApp`.
- `meter-simulator` (artifact `meter_simulator`): standalone Spring Boot CLI simulating meters → RabbitMQ on a simulated clock. Own tracker: `meter-simulator/TRACKER.md` (keep it updated for simulator tasks).

## common
- `edums` (typo of enums): AdminUserRole, Gender, PaymentPlanType, TenantType, UnitOfMeasure, UnitType (incl. APARTMENT; flags streetNumber/block/floor/apartment = address fields used by the type), UtilityType, `specification.Operation`(EQ,NOT_EQ,LIKE,GT,GTE,LT,LTE,IN,IS_NULL,IS_NOT_NULL).
- `entities`: all extend `AbstractAuditable`(creationDate,lastModifiedDate,@Version). Pattern: `@Getter @Setter @Builder`, protected no-args, private all-args, id IDENTITY `@Setter(NONE)`, equals by id, LAZY relations, enums STRING. No cascades.
  - `user`: `BaseUser`(@MappedSuperclass: username uq, password, enabled; not in builder → set via setters) ← AdminUser(fullName, role), CustomerUser(person 1:1). Person(firstName,lastName,socialId uq,dateOfBirth,gender,phone,email uq,address 1:1).
  - Address, Unit(name 255, tree parent/children `mappedBy=parent`, tenant CustomerUser optional, tenantType, address 1:1), Utility, Meter(utilityType, manufacturer, serialNumber, secretKey uq, testedUntil; table `meter`), `contract`: Contract(unit, customer, contractTemplate, meter uq, dateStart, dateEnd), ContractTemplate, Contractor(owner CustomerUser, legalAddress 1:1, fiscalCode uq, active @Builder.Default), `finance`: Invoice, Payment.
- `documents.MeterReading` (Mongo `meter_readings`): id String, serialNumber, utilityType, value (DECIMAL128), readAt, receivedAt (@CreatedDate). Same Lombok pattern as entities.
- `configs.MongoConfig`: `@EnableMongoAuditing`, `@EnableMongoRepositories` limited to `repositories.mongo`.
- `models.specification.SearchCriteria` record(field, operation, value).
- `dtos`: search DTOs only: `BaseSearchDto`(page,size=20,sort="id",direction; `toCriteria()`, `toPageable()`) ← AdminUserSearchDto, CustomerUserSearchDto, `contract.ContractorSearchDto`, `contract.ContractTemplateSearchDto`(paymentPlanType, automaticRenewal EQ), `UtilitySearchDto`(type, unitOfMeasure EQ), `UnitSearchDto`(name LIKE, unitType/tenantType/parent EQ, topLevel → parent IS_NULL), `contract.ContractSearchDto`(customer/unit/contractTemplate EQ, activeOn → dateStart LTE + dateEnd GTE).
- `exceptions.BusinessException`: thrown by services for rule violations; beans catch and show message.
- `repositories`: `BaseRepository<T,ID>` (Jpa+SpecExecutor; search/searchOptional/count by criteria). `SpecificationBuilder.allOf` skips null values; top-level fields only (no dotted paths). Address/Contractor/ContractTemplate/Contract/Utility/Unit/Meter/Invoice repos. `mongo.MeterReadingRepository` (MongoRepository). `MeterRepository.findFree(type, validUntil, page)` = meters of type tested until date and not used by any Contract. `ContractRepository.existsOverlapping`, findByCustomer/UnitId (EntityGraph).
- `services`: `Operations<T,ID>` iface; `BaseAbstractService<T,ID>` (readOnly tx; findById throws EntityNotFoundException, searchOne, search, searchPage, count, create, update(id, Consumer), update(id, incoming), delete). Per entity: `XService extends Operations` + `impl.XServiceImpl extends BaseAbstractService`. AdminUserService.findByUsername.
  - `UtilityService`/`impl.UtilityServiceImpl`: create(utility, contractorId)/update(id, changes, contractorId) resolve contractor, type unique per contractor; delete refuses if used by ContractTemplate.
  - `UnitService`/`impl.UnitServiceImpl`: create(unit, parentId, tenantId)/update(id, changes, parentId, tenantId); keeps only address fields of the UnitType; blank name → generated from address (`City, Street No, bl. X, fl. Y, ap. Z`); name unique (case-insensitive) among siblings; parent cannot be self/descendant; tenantType required with tenant, cleared without; saves Address explicitly; delete refuses if has children or contracts, then deletes address. copyParentAddress (prefill child form), findChildren, findAncestors (breadcrumb), findParentCandidates (excludes self + descendants).
  - `contract.ContractTemplateService`: create(template, utilityId)/update(id, changes, utilityId) resolve utility and set contractor from utility.contractor; price > 0, duration > 0, plan required; inactive contractor refused on create/utility change; delete refuses if used by Contract.
  - `contract.ContractService`/`impl.contract.ContractServiceImpl`: create(contract, unitId, templateId): unit must have tenant → customer = tenant; contractor active; dateEnd empty → dateStart + duration months - 1 day; no overlapping contract for same unit + utility type; takes first free meter from pool (type = template utility type, testedUntil ≥ dateEnd). update(id, changes, templateId): unit/customer/meter fixed; template change must keep utility type; same date/overlap/meter-tested checks. delete refuses if invoices; meter returns to pool. findByCustomer, findByUnit.
  - `MeterReadingService` (single class, no iface): save(json payload) parses JSON via JsonMapper into MeterReading and stores it (secretKey ignored, not stored).
  - CustomerUserServiceImpl.delete refuses if customer has contracts.
  - `contract.ContractorService`: create(contractor, ownerId) saves Address explicitly then Contractor; update(id, changes, ownerId) copies fields onto managed entity; delete refuses if used by Utility/ContractTemplate, else deletes contractor then its address. Normalizes (trim, fiscalCode/IBAN uppercase no spaces, blank→null), checks fiscalCode unique, resolves owner.

## Tests (common)
- Service tests only, `src/test/java/.../services/**/*ServiceImplTest`: `@SpringBootTest` on H2 in-memory (`src/test/resources/application.properties`: create-drop, Flyway off). `TestApp` = test boot config (+ `@EnableJpaAuditing`).
- `TestData` (@Component): `cleanDb()` truncates all tables (call in `@BeforeEach`); no-arg factories `adminUser/address/customer/contractor/inactiveContractor/utility/template/unit/rentedUnit/meter/contract`, overloads with minimal params (`utility(type, contractor)`, `template(utility)`, `unit(parent)`, `meter(type, testedUntil)`, `contract(unit, template)`, `invoice(contract)`). Saves via EntityManager, unique values by counter.
- Tests are not transactional → assert lazy relations by id only, or reload via repository.
- `MeterReadingServiceTest`: Testcontainers `mongo:8` + `@ServiceConnection` (needs Docker). H2 test contexts also create a Mongo client → harmless `Connection refused` log noise.
- Coverage: JaCoCo (parent pom), `mvn -pl common test` → `common/target/site/jacoco/index.html`.

## Admin Portal (JSF)
- `configs.SecurityConfig`: form login `/login.xhtml` → POST `/login`, success `/index.xhtml`, logout `/logout`, csrf off, BCrypt, UserDetailsService on AdminUser (role → ROLE_x). Public: login, faces resources.
- `configs.RabbitConfig`: durable queue `meter.readings` (`METER_READINGS_QUEUE`). `messaging.MeterReadingListener`: `@RabbitListener` on it, logs the body and calls `MeterReadingService.save`. `default-requeue-rejected=false` → failed messages dropped, not requeued. Meters publish directly to RabbitMQ (no REST wrapper).
- `beans`: Spring `@Component("name")` + `@Scope("view")`, ctor injection, `@PostConstruct init`. Each bean standalone (no base classes).
  - LoginBean(@RequestScope, viewAction messages for ?error/denied/logout), HomeBean.
  - `user.admin_user`: AdminUserBean(`adminUsersBean`, list), AdminUserFormBean(add/edit by `?id`, create→redirect form, update, changePassword, delete, validatePasswordMatch).
  - `user.customer_user`: CustomerUserBean(`customerUsersBean`), CustomerUserFormBean(same flow + person/address, blankToNull for uq cols).
  - `utility`: UtilityBean(`utilitiesBean`, list), UtilityFormBean(`utilityFormBean`, binds `utility` + `contractorId`, contractors SelectItems).
  - `contract.contract_template`: ContractTemplateBean(`contractTemplatesBean`, list), ContractTemplateFormBean(`contractTemplateFormBean`, binds `contractTemplate` + `utilityId`, utilities SelectItems "TYPE – Contractor").
  - `unit`: UnitBean(`unitsBean`, list, `?parentId` preselects parent filter), UnitFormBean(`unitFormBean`, binds `unit` + `parentId` + `tenantId`; `?parentId` on add prefills parent + address; parent change on add copies parent address; ancestors, children).
  - `contract.contract`: ContractBean(`contractsBean`, list; filters customer/unit/template ids + activeOn; `?customerId`/`?unitId` preselect), ContractFormBean(`contractFormBean`, binds `contract` + `unitId` + `contractTemplateId`; `?unitId` prefills on add; unit read-only in edit; details card customer/price/meter).
  - `contract.contractor`: ContractorBean(`contractorsBean`, list), ContractorFormBean(`contractorFormBean`, binds `contractor` entity + `ownerId`, owners SelectItems; create/update/delete call service, catch BusinessException).
- Views `src/main/resources/META-INF/resources`: `templates/layout.xhtml`(insert title, content), `components/header|footer.xhtml`, `css/app.css`, login.xhtml (plain HTML form), index.xhtml, `user/admin-users|admin-user-form|customer-users|customer-user-form.xhtml`, `contract/contracts|contract-form|contractors|contractor-form|contract-templates|contract-template-form.xhtml`, `utility/utilities|utility-form.xhtml`, `unit/units|unit-form.xhtml`. Extensionless URLs on. Header menu: Users, Contracts > Contracts, Contractors, Contract templates, Utilities; Units.
- CSS classes (app.css): list-page, user-page, page-head, filter-grid, form-grid(.three,.wide), field, actions(.spacer), unit-path (breadcrumb).

## Page patterns
- List: page-head(h2 + New linkButton) → Filter card(filter-grid, Search/Reset) → `results` outputPanel: dataTable, header = flat commandButton sortBy+sortIcon, name p:link `?id`, enabled/active p:tag; pager bar (first/prev/next/last + size 10/20/50).
- Form: growl; notFound card; details card(field: outputLabel+input+p:message, ajax blur); add: Add/Cancel; edit: Actions card Update/Delete(p:confirm global)/Back. Users: password+confirm (matchWith attr), Change password card → dlg confirm. Dialogs in own h:form outside main form.
- Validation in XHTML (regex/length + messages). Flash messages for redirects.

## Done
- Auth (admin users only).
- CRUD + list(filter/sort/page): AdminUser, CustomerUser, Contractor, Utility, ContractTemplate.
- `V2__seed_utilities.sql`: seed customer `utility_owner` (person+address, pwd `ChangeMe123!`), contractors Moldovagaz, Termoelectrica, Apa-Canal Chisinau, Premier Energy (each with legal address), and one Utility per UtilityType (ELECTRICITY→Premier Energy kWh; TAP/INDUSTRIAL_WATER→Apa-Canal m³; HOT_WATER→Termoelectrica m³; HEATING→Termoelectrica Gcal; GAS→Moldovagaz m³).

- Unit CRUD + list (all units; filter name/type/tenant type/parent/top-level; sort incl. parent/tenant). Form: address fields by UnitType (ajax), name auto-generated if empty, breadcrumb to ancestors, Child units card (paginated) with Add child unit (`?parentId`) / Open in list. CustomerUserUnit removed (one tenant per Unit).
- `V4__unit_hierarchy.sql`: unit.tenant_id nullable, name varchar(255), drop `unit_unit_type_check` (APARTMENT added), drop unused `unit_children` and `customer_user_unit`.
- Contract CRUD + list (filter customer/unit/template/active on; sort incl. customer/unit/utility/meter; status tag Upcoming/Active/Ended). Meter auto-assigned from pool. Contracts card (paginated, Open in list) on customer form and unit form (+ New contract `?unitId`, disabled without tenant).
- `V5__contracts_and_meters.sql`: rename `utilities` → `meter`, add meter.utility_type, contract.customer_id (FK, not null); seed meter pool: 50 meters per UtilityType (placeholder manufacturers, serials `EL-00001`…, random secret keys, tested until 2032–2034).
- `V3__seed_contract_templates.sql`: 11 templates for the V2 utilities (per contractor: monthly auto-renew + longer non-renewing plan; Termoelectrica HOT_WATER monthly only). Prices are placeholders.

- Service unit tests (63) for all service impls + base service, H2 + `TestData`, JaCoCo coverage (services ~93% instructions). Fixed `ContractorServiceImpl.delete` leaving the legal address (lazy proxy → Spring Data `delete` treated it as new and skipped it; now unproxied).

- RabbitMQ integration (minimal): queue `meter.readings` + listener that logs incoming meter readings.
- MongoDB integration (minimal): meter readings from `meter.readings` stored in Mongo `meter_readings`. Message JSON: `{"serialNumber","secretKey","utilityType","value","readAt"}`. Testcontainers test (2).

- Meter simulator module (see `meter-simulator/TRACKER.md`). `V6__seed_simulation_data.sql`: 5 buildings × 20 apartments, 100 customers `customer_001..100` (OWNER), 500 contracts (5 utilities per apartment, MONTHLY templates, 2026-01-01 → 2028-12-31), 500 new meters (serials `-00051..-00150`).

## Rules (required)
- Clean code, NO comments/javadoc/section dividers. Exception (requested): comment on `default-requeue-rejected` in application.properties.
- Business logic in service layer only (validation rules, uniqueness, normalization, lookups, delete guards). Beans only bind, call services, show messages.
- CRUD binds entities directly in beans/views. No form/CRUD DTOs (DTOs only for search).
- No cascade rules on entities (to be decided later). Services save/delete related entities explicitly.
- Do not change `SpecificationBuilder`.
- No JSF abstractions yet: no abstract/base beans, no JSF util classes, no reusable xhtml components (ui:include/composite). Each page/bean is standalone, same pattern as existing ones.
- Keep changes minimal: only touch files the task needs.
- Styles only in app.css. Lombok for getters/setters, ctor injection, builders for entities.

## Tech debt
- Dotted paths in SpecificationBuilder not supported → no filters on related fields (customer person fields, contractor owner). SpecificationBuilder frozen; decide separately.
- Utility list has no contractor filter (needs association/dotted criteria); contractor dropdown loads all contractors (incl. inactive).
- Contractor owner dropdown loads all customers (no paging) → autoComplete when list grows.
- CustomerUser create relies on saving Person/Address without cascade → verify persistence.
- customer-user-form/customer-users have inline `<style>` duplicating app.css → remove.
- Existing comments in beans/XHTML/CSS → remove.
- AdminUserSearchDto `boolean isEnabled=true`: "All" filter impossible → `Boolean enabled`.
- SecurityConfig: throws EntityNotFoundException not UsernameNotFoundException; `enabled` ignored; customers can't log in (no portal).
- HomeBean uses jakarta `@ViewScoped` vs `@Scope("view")` elsewhere.
- No logout link in header. No role-based authorization.
- Seed contractors' fiscal codes, IBANs, banks, contacts and addresses are placeholders → replace with real data.
- Flyway runs before Hibernate `ddl-auto`, so seed migrations fail on an empty DB (tables not created yet) → move schema into Flyway.
- ContractTemplate keeps both `contractor` and `utility` (contractor redundant, derived from utility); list has no utility/contractor filter (dotted criteria). Template prices are placeholders.
- `mvn install` fails: boot plugin `repackage` runs on `common` (no main class) → configure plugin only in Admin Portal. Workaround `-Dspring-boot.repackage.skip=true` (needs `clean` after a failed run, jar left empty).
- Unit parent/tenant dropdowns and list parent filter load all units/customers (no paging) → autoComplete when lists grow. Parent candidate check walks ancestors in memory.
- Hibernate `ddl-auto=update` does not refresh enum check constraints → adding enum values needs a migration dropping/updating `<table>_<column>_check` (done for unit_type only).
- No meter pages (pool filled by migration only); no warning when pool runs low. Meter manufacturers/serials are placeholders.
- Contract list customer/unit/template dropdowns load everything (no paging) → autoComplete when lists grow.
- Contract status (Upcoming/Active/Ended) computed in XHTML from dates; no renewal handling for `automaticRenewal` templates yet.
- Local env: a native Windows PostgreSQL also listens on 5432 and shadows the Docker DB for `localhost`.
- `dump.sql` is stale (V1 checksum differs from current V1).
- JPA auditing not enabled in the app (no `@EnableJpaAuditing`) → `creationDate/lastModifiedDate` not set; in tests inserts failed on NOT NULL `creation_date` until enabled in `TestApp` (verify in app, likely needs `@EnableJpaAuditing` on `AdminPortalApp`). Same lazy-proxy `delete` pitfall may hit other `repository.delete(lazyRef)` calls.
- Not covered by tests: `BaseAbstractService.searchOne/search/update(id, incoming)` (`update(id, incoming)` copies null `version`/audit fields via BeanUtils).
- RabbitMQ/Mongo: no validation of readings (meter serial/secretKey vs Postgres, required fields, utility type match); failed messages dropped (no DLQ/retry); no auth per meter; flow not verified end-to-end in the running app (local Postgres conflict blocks app start). No indexes on `meter_readings` (serialNumber/readAt), no link to Meter id.
- `MeterReadingListener` handles ~1k msg/s (one insert + INFO log per message) → backs up under the simulator at high speed factors (1 sim year ≈ 4.4M readings).
- Typos: `edums` pkg, `Amin Portal` dir, artifact `metter_to_bill`.
