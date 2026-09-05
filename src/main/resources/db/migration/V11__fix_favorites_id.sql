
ALTER TABLE favorites DROP CONSTRAINT favorites_pkey;

ALTER TABLE favorites ADD COLUMN id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY;

ALTER TABLE favorites ADD CONSTRAINT uq_favorites_user_product UNIQUE (user_id, product_id);