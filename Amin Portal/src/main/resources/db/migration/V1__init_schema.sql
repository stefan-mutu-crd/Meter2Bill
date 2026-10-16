-- Initial password for every user: ChangeMe123!

INSERT INTO admin_user
(username,
 password,
 enabled,
 full_name,
 role,
 creation_date,
 last_modified_date,
 version)
VALUES ('principal', '$2a$10$svd9Dgr.j80RcRDCEig3zOJy5nQckFDTsvYU00N/6dMIshmXBhZpW', TRUE,
        'Principal Admin', 'PRINCIPAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('maintainer', '$2a$10$kq/zvuATk.gqJeC/i3I02eh5R4hdT.csIu24AXH9SGNTXSJbwlWqG', TRUE,
        'Maintainer User', 'MAINTAINER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('audit', '$2a$10$B/xMO/WJIUpsQAwWmyM62OXYJ1vyLLtkuSpRCKho93UH4fIEJnZr6', TRUE,
        'Audit User', 'AUDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0),

       ('economist', '$2a$10$UfGATXHq0tQ/jqryGSWFc.ael1e1BPIlYgB5LliCKca0jwEnW9RX.', TRUE,
        'Economist User', 'ECONOMIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,0);