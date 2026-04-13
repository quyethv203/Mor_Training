ALTER TABLE categories ADD created_at DATETIME;
ALTER TABLE categories ADD modified_at DATETIME;

ALTER TABLE products ADD created_at DATETIME;
ALTER TABLE products ADD modified_at DATETIME;

ALTER TABLE orders ADD created_at DATETIME;
ALTER TABLE orders ADD modified_at DATETIME;

ALTER TABLE order_items ADD created_at DATETIME;
ALTER TABLE order_items ADD modified_at DATETIME;