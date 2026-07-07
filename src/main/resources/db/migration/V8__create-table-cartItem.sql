
CREATE TABLE cart_items (
        id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        cart_id    BIGINT  NOT NULL,
        product_id BIGINT  NOT NULL,
        quantity   INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),

        CONSTRAINT fk_cartitems_cart
            FOREIGN KEY (cart_id)    REFERENCES carts (id)    ON DELETE CASCADE,
        CONSTRAINT fk_cartitems_product
            FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,


        CONSTRAINT uq_cart_product UNIQUE (cart_id, product_id)
);