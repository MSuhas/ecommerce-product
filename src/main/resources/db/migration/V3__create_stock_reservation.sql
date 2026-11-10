CREATE TABLE stock_reservation
(
    id             BIGSERIAL PRIMARY KEY,
    reservation_id VARCHAR(100)             NOT NULL UNIQUE,
    product_id     BIGINT                   NOT NULL,
    quantity       INTEGER                  NOT NULL,
    status         VARCHAR(30)              NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL
);