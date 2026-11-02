WITH owner_address AS (
    INSERT INTO address (city, district, street, street_number, creation_date, last_modified_date, version)
        VALUES ('Chisinau', 'Centru', 'Stefan cel Mare si Sfant', '1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
        RETURNING id),
     owner_person AS (
         INSERT INTO person (first_name, last_name, date_of_birth, address_id, email, creation_date, last_modified_date, version)
             SELECT 'Utility', 'Owner', DATE '1990-01-01', id, 'utility.owner@meter2bill.local',
                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
             FROM owner_address
             RETURNING id)
INSERT INTO customer_user (username, password, enabled, person_id, creation_date, last_modified_date, version)
SELECT 'utility_owner', '$2a$10$pYb.tG5V4gxBBshRJSCJe.z8W1GjhKf1LreAOaHZtCvfRBf03QabO', TRUE, id,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM owner_person;

WITH legal_address AS (
    INSERT INTO address (city, district, street, street_number, creation_date, last_modified_date, version)
        VALUES ('Chisinau', 'Centru', 'Albisoara', '38', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
        RETURNING id)
INSERT INTO contractor (company_name, legal_name, fiscal_code, owner_id, legal_address_id, iban, bank_name, email,
                        phone_number, active, creation_date, last_modified_date, version)
SELECT 'Moldovagaz', 'S.A. Moldovagaz', '1002600001001',
       (SELECT id FROM customer_user WHERE username = 'utility_owner'), id,
       'MD24AG000000022510000001', 'Moldova Agroindbank', 'office@moldovagaz.md', '+37322578100', TRUE,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM legal_address;

WITH legal_address AS (
    INSERT INTO address (city, district, street, street_number, creation_date, last_modified_date, version)
        VALUES ('Chisinau', 'Botanica', 'Tudor Vladimirescu', '6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
        RETURNING id)
INSERT INTO contractor (company_name, legal_name, fiscal_code, owner_id, legal_address_id, iban, bank_name, email,
                        phone_number, active, creation_date, last_modified_date, version)
SELECT 'Termoelectrica', 'S.A. Termoelectrica', '1002600002002',
       (SELECT id FROM customer_user WHERE username = 'utility_owner'), id,
       'MD24ML000000022510000002', 'Moldindconbank', 'office@termoelectrica.md', '+37322436436', TRUE,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM legal_address;

WITH legal_address AS (
    INSERT INTO address (city, district, street, street_number, creation_date, last_modified_date, version)
        VALUES ('Chisinau', 'Centru', 'Columna', '96', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
        RETURNING id)
INSERT INTO contractor (company_name, legal_name, fiscal_code, owner_id, legal_address_id, iban, bank_name, email,
                        phone_number, active, creation_date, last_modified_date, version)
SELECT 'Apa-Canal Chisinau', 'S.A. Apa-Canal Chisinau', '1003600003003',
       (SELECT id FROM customer_user WHERE username = 'utility_owner'), id,
       'MD24VI000000022510000003', 'Victoriabank', 'office@acc.md', '+37322256600', TRUE,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM legal_address;

WITH legal_address AS (
    INSERT INTO address (city, district, street, street_number, creation_date, last_modified_date, version)
        VALUES ('Chisinau', 'Riscani', 'Andrei Doga', '4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
        RETURNING id)
INSERT INTO contractor (company_name, legal_name, fiscal_code, owner_id, legal_address_id, iban, bank_name, email,
                        phone_number, active, creation_date, last_modified_date, version)
SELECT 'Premier Energy', 'Premier Energy S.R.L.', '1002600004004',
       (SELECT id FROM customer_user WHERE username = 'utility_owner'), id,
       'MD24MO000000022510000004', 'MAIB', 'office@premierenergy.md', '+37322431111', TRUE,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM legal_address;

INSERT INTO utility (type, unit_of_measure, contractor_id, creation_date, last_modified_date, version)
SELECT u.type, u.unit_of_measure, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM (VALUES ('ELECTRICITY', 'KW_HOUR', '1002600004004'),
             ('TAP_WATER', 'CUBIC_METER', '1003600003003'),
             ('INDUSTRIAL_WATER', 'CUBIC_METER', '1003600003003'),
             ('HOT_WATER', 'CUBIC_METER', '1002600002002'),
             ('GAS', 'CUBIC_METER', '1002600001001'),
             ('HEATING', 'GIGACALORIE', '1002600002002')) AS u(type, unit_of_measure, fiscal_code)
         JOIN contractor c ON c.fiscal_code = u.fiscal_code;
