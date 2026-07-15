# Relatório — Laboratório 3 (Testes e Multiplataforma)

**Nome:** Sérgio Malheiro  **Nº de inscrição:** 185411
**Repositório GitHub:** _(colar aqui o URL após o push)_

> Substituir os `![...]` por capturas de ecrã. Os resultados numéricos abaixo já
> refletem a execução real neste projeto.

---

## 1. Testes unitários

Testes JUnit 5 para as classes desenvolvidas nos labs anteriores:

- `InMemoryUserRepository` — `api/src/test/.../repository/memory/InMemoryUserRepositoryTest.java` (11 testes)
- `TodoUserService` — `api/src/test/.../service/TodoUserServiceTest.java` (8 testes)

### a) Execução (`mvn test` / IntelliJ)

```
Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```
![captura: testes verdes](docs/img/testes.png)

### b) Cobertura (JaCoCo / plugin do IntelliJ)

Relatório em `api/target/site/jacoco/index.html`.

| Classe | Linhas | Cobertura |
|--------|--------|-----------|
| `InMemoryUserRepository` | 16 / 16 | 100 % |
| `TodoUserService`        | 18 / 18 | 100 % |

![captura: cobertura](docs/img/cobertura.png)

---

## 2. Testes de integração (Postman/Newman)

Coleção `testes-integracao/postman.json`. Token guardado na variável de coleção
`userToken` (via `pm.response.text()`) e reutilizado. URL com `host.docker.internal`.

### a) Repositório em memória

```bash
cd api && REPO=memory mvn exec:java
cd testes-integracao && docker run --rm -v "${PWD}":/etc/newman -t postman/newman run postman.json
```
Resultado: **11 requests, 19 assertions, 0 failed.**

![captura: Newman memória](docs/img/newman-memoria.png)

### b) Repositório SQL (PostgreSQL)

```bash
cd testes-integracao && docker compose up -d
cd ../api && REPO=sql mvn exec:java
cd ../testes-integracao && docker run --rm -v "${PWD}":/etc/newman -t postman/newman run postman.json
```
Resultado: idêntico (11 requests, 19 assertions, 0 failed).

![captura: Newman SQL](docs/img/newman-sql.png)

---

## 3. Aplicação cliente multiplataforma (Flutter)

Cliente `todoapp/` — login, listas, tarefas e marcar como completa.

### a) Execução em Android e Desktop

![captura: Desktop (macOS)](docs/img/flutter-desktop.png)
![captura: Android](docs/img/flutter-android.png)

### b) Marcar tarefas como completas (API + cliente)

- **API:** `PUT /todo/{listId}/tasks/{taskId}/complete` e `/incomplete`
  (`TodoController.completeTodoItem` + `TodoService.setCompleted`), a funcionar nos
  repositórios em memória e SQL.
- **Cliente:** a checkbox de cada tarefa chama `ApiClient.setCompleted(...)`.

![captura: tarefa marcada como completa](docs/img/flutter-completa.png)
