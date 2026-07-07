create table promotions(
   id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   name   VARCHAR(50) NOT NULL,
   discount_percent INT NOT NULL,
   start_date DATE NOT NULL,
   end_date DATE NOT NULL,
   created_at TIMESTAMPTZ NOT NULL DEFAULT now()
   CONSTRAINT chk_dates CHECK (end_date >= start_date)
)