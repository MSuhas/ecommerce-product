ALTER TABLE product
    ADD COLUMN sku VARCHAR(100) NOT NULL;

ALTER TABLE product
    ADD CONSTRAINT uk_product_sku UNIQUE (sku);