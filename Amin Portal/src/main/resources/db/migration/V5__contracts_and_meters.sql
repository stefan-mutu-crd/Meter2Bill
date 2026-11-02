ALTER TABLE IF EXISTS utilities RENAME TO meter;
ALTER TABLE IF EXISTS meter ADD COLUMN IF NOT EXISTS utility_type VARCHAR(20);
ALTER TABLE IF EXISTS contract ADD COLUMN IF NOT EXISTS customer_id BIGINT REFERENCES customer_user (id);

INSERT INTO meter (utility_type, manufacturer, serial_number, secret_key, tested_until,
                   creation_date, last_modified_date, version)
SELECT t.utility_type,
       t.manufacturer,
       t.prefix || '-' || LPAD(n::TEXT, 5, '0'),
       MD5(t.utility_type || '-' || n || '-' || RANDOM()::TEXT),
       DATE '2034-12-31' - ((n % 4) * 365),
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM (VALUES ('ELECTRICITY', 'Landis+Gyr', 'EL'),
             ('TAP_WATER', 'Zenner', 'TW'),
             ('INDUSTRIAL_WATER', 'Sensus', 'IW'),
             ('HOT_WATER', 'Itron', 'HW'),
             ('GAS', 'Elster', 'GS'),
             ('HEATING', 'Kamstrup', 'HT'))
         AS t(utility_type, manufacturer, prefix)
         CROSS JOIN GENERATE_SERIES(1, 50) AS n;

ALTER TABLE IF EXISTS meter ALTER COLUMN utility_type SET NOT NULL;
ALTER TABLE IF EXISTS contract ALTER COLUMN customer_id SET NOT NULL;
