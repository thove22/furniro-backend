
CREATE TABLE product_images (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id BIGINT       NOT NULL,
    url        VARCHAR(500) NOT NULL,
    position   INTEGER      NOT NULL DEFAULT 0,

    CONSTRAINT fk_images_product
        FOREIGN KEY (product_id) REFERENCES products (id)
            ON DELETE CASCADE
);