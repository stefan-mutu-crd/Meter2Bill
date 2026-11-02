INSERT INTO contract_template (contractor_id, utility_id, price, payment_plan_type, duration_in_months,
                               automatic_renewal, creation_date, last_modified_date, version)
SELECT c.id, u.id, t.price, t.payment_plan_type, t.duration_in_months, t.automatic_renewal,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM (VALUES ('1002600004004', 'ELECTRICITY', 2.89, 'MONTHLY', 12, TRUE),
             ('1002600004004', 'ELECTRICITY', 2.75, 'YEARLY', 24, FALSE),
             ('1003600003003', 'TAP_WATER', 21.94, 'MONTHLY', 12, TRUE),
             ('1003600003003', 'TAP_WATER', 20.90, 'QUARTERLY', 24, FALSE),
             ('1003600003003', 'INDUSTRIAL_WATER', 35.50, 'MONTHLY', 12, TRUE),
             ('1003600003003', 'INDUSTRIAL_WATER', 33.80, 'HALF_YEARLY', 36, FALSE),
             ('1002600002002', 'HOT_WATER', 75.00, 'MONTHLY', 12, TRUE),
             ('1002600002002', 'HEATING', 1785.00, 'MONTHLY', 12, TRUE),
             ('1002600002002', 'HEATING', 1720.00, 'YEARLY', 24, FALSE),
             ('1002600001001', 'GAS', 12.53, 'MONTHLY', 12, TRUE),
             ('1002600001001', 'GAS', 12.10, 'YEARLY', 24, FALSE))
         AS t(fiscal_code, utility_type, price, payment_plan_type, duration_in_months, automatic_renewal)
         JOIN contractor c ON c.fiscal_code = t.fiscal_code
         JOIN utility u ON u.contractor_id = c.id AND u.type = t.utility_type;
