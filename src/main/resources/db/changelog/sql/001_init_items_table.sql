CREATE TABLE IF NOT EXISTS items (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1024),
    img_path VARCHAR(255),
    price NUMERIC DEFAULT 0 NOT NULL);

CREATE INDEX idx_items_title ON items(title);
CREATE INDEX idx_items_description ON items(description);