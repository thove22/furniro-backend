
INSERT INTO categories (name, description, image_url) VALUES
      ('Dining', 'Dining room furniture', null),
      ('Living', 'Living room furniture', null),
      ('Bedroom', 'Bedroom furniture', null);

INSERT INTO products (category_id, name, description, price, stock) VALUES
    (1, 'Syltherine', 'Stylish cafe chair', 2500000.00, 10),
    (1, 'Leviosa', 'Stylish cafe chair', 2500000.00, 8),
    (2, 'Lolito', 'Luxury big sofa', 7000000.00, 5),
    (2, 'Respira', 'Outdoor bar table and stool', 500000.00, 15),
    (3, 'Grifo', 'Night lamp', 1500000.00, 20),
    (3, 'Muggo', 'Small mug', 150000.00, 50),
    (2, 'Pingky', 'Cute bed set', 7000000.00, 3),
    (3, 'Potty', 'Minimalist flower pot', 500000.00, 25);