DO
$$
    DECLARE
        v_streets     TEXT[][] := ARRAY [['Botanica', 'Dacia', '21'],
                                         ['Riscani', 'Moscova', '12'],
                                         ['Ciocana', 'Mircea cel Batran', '8'],
                                         ['Buiucani', 'Alba Iulia', '75'],
                                         ['Centru', 'Ismail', '46']];
        v_first_names TEXT[]   := ARRAY ['Ion', 'Maria', 'Andrei', 'Elena', 'Mihai', 'Ana', 'Vasile', 'Natalia', 'Alexandru', 'Cristina'];
        v_last_names  TEXT[]   := ARRAY ['Popescu', 'Rusu', 'Ceban', 'Ciobanu', 'Lungu', 'Munteanu', 'Rotaru', 'Cojocaru', 'Sirbu', 'Turcan'];
        v_utilities   TEXT[][] := ARRAY [['ELECTRICITY', 'Landis+Gyr', 'EL'],
                                         ['TAP_WATER', 'Zenner', 'TW'],
                                         ['HOT_WATER', 'Itron', 'HW'],
                                         ['GAS', 'Elster', 'GS'],
                                         ['HEATING', 'Kamstrup', 'HT']];
        v_templates   BIGINT[] := ARRAY []::BIGINT[];
        v_building_id BIGINT;
        v_address_id  BIGINT;
        v_person_id   BIGINT;
        v_customer_id BIGINT;
        v_unit_id     BIGINT;
        v_meter_id    BIGINT;
        v_name_index  INT;
        v_floor       TEXT;
        k             INT;
    BEGIN
        FOR u IN 1..5
            LOOP
                v_templates := v_templates || (SELECT MIN(ct.id)
                                               FROM contract_template ct
                                                        JOIN utility ut ON ut.id = ct.utility_id
                                               WHERE ut.type = v_utilities[u][1]
                                                 AND ct.payment_plan_type = 'MONTHLY');
            END LOOP;

        FOR b IN 1..5
            LOOP
                INSERT INTO address (city, district, street, street_number, creation_date, last_modified_date, version)
                VALUES ('Chisinau', v_streets[b][1], v_streets[b][2], v_streets[b][3], CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
                RETURNING id INTO v_address_id;

                INSERT INTO unit (unit_type, name, address_id, creation_date, last_modified_date, version)
                VALUES ('BUILDING', 'Chisinau, ' || v_streets[b][2] || ' ' || v_streets[b][3], v_address_id,
                        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
                RETURNING id INTO v_building_id;

                FOR i IN 1..20
                    LOOP
                        k := (b - 1) * 20 + i;
                        v_name_index := ((k - 1) % 10) + 1;
                        v_floor := (((i - 1) / 4) + 1)::TEXT;

                        INSERT INTO address (city, district, street, street_number, floor, apartment,
                                             creation_date, last_modified_date, version)
                        VALUES ('Chisinau', v_streets[b][1], v_streets[b][2], v_streets[b][3], v_floor, i::TEXT,
                                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
                        RETURNING id INTO v_address_id;

                        INSERT INTO person (first_name, last_name, social_id, date_of_birth, gender, address_id,
                                            phone_number, email, creation_date, last_modified_date, version)
                        VALUES (v_first_names[v_name_index], v_last_names[((k - 1) / 10) + 1],
                                '2009000000' || LPAD(k::TEXT, 3, '0'),
                                DATE '1960-01-01' + (k * 137) % 15000,
                                CASE WHEN v_name_index % 2 = 1 THEN 'MALE' ELSE 'FEMALE' END,
                                v_address_id,
                                '+3736' || LPAD(k::TEXT, 7, '0'),
                                'customer' || LPAD(k::TEXT, 3, '0') || '@meter2bill.local',
                                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
                        RETURNING id INTO v_person_id;

                        INSERT INTO customer_user (username, password, enabled, person_id,
                                                   creation_date, last_modified_date, version)
                        VALUES ('customer_' || LPAD(k::TEXT, 3, '0'),
                                '$2a$10$pYb.tG5V4gxBBshRJSCJe.z8W1GjhKf1LreAOaHZtCvfRBf03QabO', TRUE, v_person_id,
                                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
                        RETURNING id INTO v_customer_id;

                        INSERT INTO address (city, district, street, street_number, floor, apartment,
                                             creation_date, last_modified_date, version)
                        VALUES ('Chisinau', v_streets[b][1], v_streets[b][2], v_streets[b][3], v_floor, i::TEXT,
                                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
                        RETURNING id INTO v_address_id;

                        INSERT INTO unit (unit_type, name, parent_id, address_id, tenant_id, tenant_type,
                                          creation_date, last_modified_date, version)
                        VALUES ('APARTMENT',
                                'Chisinau, ' || v_streets[b][2] || ' ' || v_streets[b][3] || ', fl. ' || v_floor || ', ap. ' || i,
                                v_building_id, v_address_id, v_customer_id, 'OWNER',
                                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
                        RETURNING id INTO v_unit_id;

                        FOR u IN 1..5
                            LOOP
                                INSERT INTO meter (utility_type, manufacturer, serial_number, secret_key, tested_until,
                                                   creation_date, last_modified_date, version)
                                VALUES (v_utilities[u][1], v_utilities[u][2],
                                        v_utilities[u][3] || '-' || LPAD((50 + k)::TEXT, 5, '0'),
                                        MD5(v_utilities[u][1] || '-sim-' || k || '-' || RANDOM()::TEXT),
                                        DATE '2034-12-31',
                                        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
                                RETURNING id INTO v_meter_id;

                                INSERT INTO contract (unit_id, customer_id, contract_template_id, meter_id, date_start,
                                                      date_end, creation_date, last_modified_date, version)
                                VALUES (v_unit_id, v_customer_id, v_templates[u], v_meter_id, DATE '2026-01-01',
                                        DATE '2028-12-31', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
                            END LOOP;
                    END LOOP;
            END LOOP;
    END
$$;
