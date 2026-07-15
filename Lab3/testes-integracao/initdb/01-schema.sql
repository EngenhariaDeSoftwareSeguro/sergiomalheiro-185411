-- Esquema para os testes de integração SQL (parte 2b).
-- Igual aos ficheiros em api/sql/, mas com ON DELETE CASCADE para que apagar um
-- utilizador (último passo da coleção Postman) respeite as chaves estrangeiras.

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE lists (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE todos (
    id SERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    list_id INTEGER NOT NULL REFERENCES lists(id) ON DELETE CASCADE
);
