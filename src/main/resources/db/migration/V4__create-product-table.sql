CREATE TABLE products (
          id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
          category_id    BIGINT        NOT NULL,
          promotion_id   BIGINT,
          name           VARCHAR(200)  NOT NULL,
          description    TEXT,
          price          NUMERIC(12,2) NOT NULL CHECK (price >= 0),
          stock          INTEGER       NOT NULL DEFAULT 0 CHECK (stock >= 0),
          created_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),

          CONSTRAINT fk_products_category
              FOREIGN KEY (category_id) REFERENCES categories (id)
                  ON DELETE RESTRICT,

          CONSTRAINT fk_products_promotion
              FOREIGN KEY (promotion_id) REFERENCES promotions (id)
                  ON DELETE SET NULL
);
