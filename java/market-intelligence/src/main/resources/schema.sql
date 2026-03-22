CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    warehouse_location VARCHAR(255)
);

-- Delete existing to avoid duplicates in this demo run
DELETE FROM inventory;

INSERT INTO inventory (product_id, quantity, warehouse_location) VALUES (1, 50, 'Almacén Central Madrid');
INSERT INTO inventory (product_id, quantity, warehouse_location) VALUES (2, 5, 'Sede Secundaria Barcelona');
INSERT INTO inventory (product_id, quantity, warehouse_location) VALUES (14, 100, 'Distribuidor Norte High Value');
