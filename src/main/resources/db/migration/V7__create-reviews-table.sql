CREATE TABLE reviews (
     id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
     product_id BIGINT  NOT NULL,
     user_id    BIGINT  NOT NULL,
     rating     INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
     comment    TEXT,
     created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

     CONSTRAINT fk_reviews_product
         FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
     CONSTRAINT fk_reviews_user
         FOREIGN KEY (user_id)    REFERENCES users (id)    ON DELETE CASCADE,

     CONSTRAINT uq_review_user_product UNIQUE (user_id, product_id)
);