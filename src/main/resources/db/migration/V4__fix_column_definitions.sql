ALTER TABLE  products MODIFY is_deleted BOOLEAN NOT NULL DEFAULT false;
UPDATE products SET is_deleted = false WHERE is_deleted = NULL;

ALTER TABLE products MODIFY price DECIMAL(10, 2) NOT NULL ;
ALTER TABLE orders MODIFY totalAmount DECIMAL(12, 2);
ALTER TABLE order_items MODIFY unitPrice DECIMAL(10, 2) NOT NULL;