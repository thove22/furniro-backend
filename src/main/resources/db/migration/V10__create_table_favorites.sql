CREATE TABLE favorites(
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES  products(id) ON DELETE CASCADE,
    primary key (user_id, product_id)
)