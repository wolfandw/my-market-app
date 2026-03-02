CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    total_sum NUMERIC DEFAULT 0 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    count INTEGER DEFAULT 1 NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(id)
);

CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_item_id ON order_items(item_id);