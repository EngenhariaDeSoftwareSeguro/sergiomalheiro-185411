# api — Todo API (Lab3)

API Javalin (base `Lab1_withSQL`) com testes unitários JUnit5 e seleção de repositório.

## Correr os testes (parte 1)

```bash
mvn test
open target/site/jacoco/index.html   # cobertura
```

## Correr a aplicação

Porta 7100. O backend de dados escolhe-se por variável de ambiente `REPO`:

```bash
REPO=memory mvn exec:java     # repositórios em memória (por omissão)
REPO=sql    mvn exec:java     # repositórios SQL (PostgreSQL)
```

Para `REPO=sql`, ligar a um PostgreSQL (ver `../testes-integracao/docker-compose.yml`).
Ligação configurável por ambiente:

| Variável | Omissão |
|----------|---------|
| `DB_HOST` | localhost |
| `DB_PORT` | 5432 |
| `DB_NAME` | postgres |
| `DB_USER` | postgres |
| `DB_PASSWORD` | changeit |

Esquema SQL em [`sql/`](sql/) (também aplicado automaticamente pelo compose de integração).

## Endpoints

- `POST /user`, `POST /login`, `GET /user/{id}`, `DELETE /user/{id}`
- `POST /todolist`, `GET /todolist`, `GET /todolist/{listId}`
- `POST /todo/item`, `GET /todo/{listId}/tasks`, `GET /todo/{listId}/tasks/{taskId}`, `DELETE /todo/{listId}/tasks/{taskId}`
- **`PUT /todo/{listId}/tasks/{taskId}/complete`** e **`/incomplete`** — marcar completo (parte 3b)

Autenticação: o `/login` devolve o token `Bearer <username>`, enviado tal e qual no header
`Authorization`. (Base intencionalmente simples — password em claro — herdada do Lab1.)
