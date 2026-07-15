# testes-integracao — Postman/Newman (Lab3, parte 2)

Coleção de integração da Todo API. Fluxo com captura de token em variável de coleção e
asserções em cada passo: criar utilizador → login → obter utilizador → criar lista →
criar item → listar → **marcar completo** → confirmar → 401 sem token → apagar item →
apagar utilizador.

- `postman.json` — coleção corrigida e estendida (usada nos testes). Usa
  `host.docker.internal:7100` (dentro do contentor Newman, `localhost` seria o próprio
  contentor).
- `TodoList-phase1.original.json` — coleção fornecida no enunciado, mantida como referência.
  Foi corrigida no `postman.json`: rotas dos itens (`/todo/item`, `/todo/{listId}/tasks`),
  captura do token com `pm.response.text()` (o `/login` devolve a string em cru, não JSON),
  e `host.docker.internal`.
- `docker-compose.yml` + `initdb/` — PostgreSQL para a parte 2b.

## 2a — repositório em memória

```bash
# terminal 1: API em memória
cd ../api && REPO=memory mvn exec:java

# terminal 2: correr a coleção
docker run --rm -v "${PWD}":/etc/newman -t postman/newman run postman.json
```

## 2b — repositório SQL (PostgreSQL)

```bash
# 1. arrancar o Postgres (esquema aplicado automaticamente)
docker compose up -d

# 2. API em modo SQL
cd ../api && REPO=sql mvn exec:java

# 3. mesma coleção
docker run --rm -v "${PWD}":/etc/newman -t postman/newman run postman.json

# 4. no fim
docker compose down -v
```

Resultado esperado (ambos os modos): **11 requests, 19 assertions, 0 failed**.
