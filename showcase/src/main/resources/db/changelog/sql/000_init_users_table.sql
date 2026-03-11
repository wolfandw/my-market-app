CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(1024) NOT NULL,
    roles VARCHAR(1024) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

INSERT INTO users(id, username, password, roles)
VALUES
    (1L,'admin', '$2a$12$m5dnhoX3cf2zjEph.H/42e7lEqbd/Dmdiqg5R/kyWUTELuVdHHfIW', 'USER,ADMIN'),
    (2L,'user', '$2a$12$gSx1V/Q95xi8OjdfgrSx1.8bkYBjI75lKoY0SJZmMZbd0R6aY12Ky', 'USER');