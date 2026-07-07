
CREATE TABLE users (
        id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        email       VARCHAR(255) NOT NULL UNIQUE,
        password    VARCHAR(255) NOT NULL,
        first_name  VARCHAR(100),
       last_name   VARCHAR(100),
       role        VARCHAR(20)  NOT NULL DEFAULT 'USER'
           CHECK (role IN ('USER', 'ADMIN')),
       created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);