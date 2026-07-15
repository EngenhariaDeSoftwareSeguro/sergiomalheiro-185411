# Relatório — Laboratório 3
## Testes e aplicação multiplataforma

**Disciplina:** Engenharia de Software Seguro (Nível D) — C-Academy / CNCS
**Formando:** Sérgio Malheiro **Nº de inscrição:** 185411
**Data:** 16 de julho de 2026
**Repositório GitHub:** _(colar aqui o URL após o push)_

---

## Índice

1. [Introdução e objetivos](#1-introdução-e-objetivos)
2. [Estrutura da entrega](#2-estrutura-da-entrega)
3. [Parte 1 — Testes unitários](#3-parte-1--testes-unitários)
4. [Parte 2 — Testes de integração](#4-parte-2--testes-de-integração)
5. [Parte 3 — Aplicação cliente multiplataforma](#5-parte-3--aplicação-cliente-multiplataforma)
6. [Conclusão](#6-conclusão)

---

## 1. Introdução e objetivos

Este laboratório tem três objetivos, partindo da API "Todo List" desenvolvida nos
laboratórios anteriores (versão com suporte SQL, `Lab1_withSQL`):

- Desenvolver **testes unitários** para a API e analisar a sua **cobertura**;
- Desenvolver **testes de integração** executados com Postman/Newman, contra o repositório
  em memória e contra o repositório SQL;
- Utilizar (e estender) uma **aplicação cliente multiplataforma** em Flutter para aceder à API.

A API é um serviço REST em **Javalin** (Java), com três entidades — `User`, `TodoList` e
`Todo` — e dois backends de persistência: em memória e PostgreSQL. A autenticação é
propositadamente simples (herdada do Lab1): o `/login` devolve um token no formato
`Bearer <username>` que é validado em cada pedido pelo `AuthorizationMiddleware`.

### Alterações feitas à base para o Lab3

| Alteração | Motivo |
|-----------|--------|
| `pom.xml`: JUnit 3.8.1 → **JUnit 5.11.4** + `maven-surefire-plugin` + `jacoco-maven-plugin` | A base trazia JUnit 3; o enunciado pede testes JUnit5 e análise de cobertura |
| `App.java`: seleção de repositório por variável `REPO` (`memory`/`sql`) | Permitir correr a mesma coleção de integração em memória (2a) e SQL (2b) sem alterar código |
| Novo endpoint `PUT /todo/{listId}/tasks/{taskId}/complete` (e `/incomplete`) | Parte 3b — marcar tarefas como completas (API) |
| `TodoService.setCompleted(...)` + `TodoController.completeTodoItem(...)` | Suporte da funcionalidade acima, nos repositórios em memória e SQL |

> A segurança da base **não** foi alterada (password em claro, token `Bearer <username>`),
> por ser intencionalmente simples e porque endurecê-la quebraria a coleção Postman fornecida.

---

## 2. Estrutura da entrega

O repositório está organizado por pastas, uma por componente do laboratório:

```
Lab3/
├── api/                  Projeto Maven da API + testes unitários (src/test)   → Parte 1
├── testes-integracao/    Coleção Postman + Newman + PostgreSQL (Docker)        → Parte 2
└── todoapp/              Cliente Flutter multiplataforma                       → Parte 3
```

Os testes unitários vivem dentro do projeto Maven (`api/src/test`) porque o `mvn test` exige
que estejam junto do código que testam; a integração (Postman) e a aplicação (Flutter) são
artefactos independentes, cada um na sua pasta.

---

## 3. Parte 1 — Testes unitários

### 3.1. Classes testadas

Foram desenvolvidos testes para as duas classes indicadas no enunciado:

| Classe | Responsabilidade | Métodos testados |
|--------|------------------|------------------|
| `InMemoryUserRepository` | Persistência de utilizadores em memória | `save`, `findById`, `findAll`, `findByUsername`, `deleteById` |
| `TodoUserService` | Lógica de negócio de utilizadores (registo, autenticação) | `addUser`, `getUser`, `getAllUsers`, `deleteUser`, `login` |

### 3.2. Abordagem

Utilizou-se **JUnit 5** (Jupiter). Os testes do `TodoUserService` usam um
`InMemoryUserRepository` real como colaborador — sendo este determinístico e sem dependências
externas, não foi necessária qualquer framework de *mocking*. Cada teste tem um nome descritivo
(`@DisplayName`) que documenta o comportamento verificado.

### 3.3. Casos de teste

**`InMemoryUserRepositoryTest`** (11 testes):

| Método testado | Casos verificados |
|----------------|-------------------|
| `save` | atribui id > 0 a utilizador novo; gera ids incrementais distintos; preserva id já atribuído (id ≠ 0) |
| `findById` | devolve o utilizador guardado; devolve `null` para id inexistente |
| `findAll` | devolve todos os utilizadores; devolve lista vazia num repositório novo |
| `findByUsername` | encontra pelo nome; devolve `null` quando não existe |
| `deleteById` | remove o utilizador; ignora id inexistente sem alterar o estado |

**`TodoUserServiceTest`** (8 testes):

| Método testado | Casos verificados |
|----------------|-------------------|
| `addUser` | cria utilizador com id atribuído |
| `getUser` | devolve o utilizador criado; devolve `null` para id inexistente |
| `getAllUsers` | reflete os utilizadores criados (0 → 2) |
| `deleteUser` | remove o utilizador |
| `login` | credenciais corretas → token `Bearer <username>`; password errada → `null`; utilizador inexistente → `null` |

### 3.4. a) Execução dos testes

Comando: `mvn test` (na pasta `api/`).

```
[INFO]  T E S T S
[INFO] Running cncs.academy.ess.repository.memory.InMemoryUserRepositoryTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running cncs.academy.ess.service.TodoUserServiceTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] Results:
[INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Resultado: 19 testes, 0 falhas.**

![Execução dos testes unitários (mvn test)](docs/img/01-testes.png)

### 3.5. b) Cobertura de testes

A cobertura é gerada automaticamente pelo JaCoCo em `target/site/jacoco/index.html`.

| Classe | Instruções | Linhas | Cobertura de linhas |
|--------|-----------|--------|---------------------|
| `InMemoryUserRepository` | 100 % | 16 / 16 | **100 %** |
| `TodoUserService` | 100 % | 18 / 18 | **100 %** |

**Análise:** a cobertura de 100 % nas classes-alvo confirma que todos os caminhos relevantes
são exercitados — incluindo os ramos condicionais: em `save`, o caso `id == 0` (gerar id) e
`id != 0` (preservar); em `login` e `findByUsername`, os caminhos "encontrado" e "não
encontrado"; e em `login`, password correta vs. incorreta. As restantes classes do projeto
(controladores, repositórios SQL) não são alvo destes testes unitários e por isso aparecem com
cobertura inferior no relatório global — o que é esperado.

![Cobertura JaCoCo — InMemoryUserRepository (100%)](docs/img/02-cobertura-repo.png)

![Cobertura JaCoCo — TodoUserService (100%)](docs/img/03-cobertura-service.png)

---

## 4. Parte 2 — Testes de integração

### 4.1. Coleção Postman

A coleção fornecida no enunciado (`TodoList-phase1.original.json`, mantida no repositório para
referência) apresentava três problemas que impediam a execução automática:

1. As rotas dos itens estavam dessincronizadas da API (`/todolist/item` em vez de `/todo/item`);
2. Usava `localhost`, que dentro do contentor Newman refere o próprio contentor;
3. Não capturava o token de autenticação para reutilização.

Foi por isso criada uma coleção corrigida e estendida, `postman.json`, que:

- Usa `host.docker.internal:7100` (o anfitrião visto de dentro do contentor);
- **Captura o token** com `pm.response.text()` — o `/login` devolve a string `Bearer <username>`
  em cru (não é JSON), guardada na variável de coleção `userToken` e reutilizada no header
  `Authorization` dos pedidos seguintes;
- Contém **asserções** (`pm.test`) em cada pedido (código de estado e conteúdo);
- Cobre o fluxo completo, incluindo a funcionalidade nova de marcar-completo.

**Fluxo (11 pedidos):** criar utilizador → login → obter utilizador → criar lista → criar item
→ listar itens → **marcar item completo** → confirmar completo → pedido sem token (401) →
apagar item → apagar utilizador.

### 4.2. a) Repositório em memória

```bash
# Terminal 1 — API em memória
cd api && REPO=memory mvn exec:java

# Terminal 2 — Newman via Docker
cd testes-integracao
docker run --rm -v "${PWD}":/etc/newman -t postman/newman run postman.json
```

**Resultado: 11 pedidos, 19 asserções, 0 falhas.**

![Newman — repositório em memória](docs/img/04-newman-memoria.png)

### 4.3. b) Repositório SQL (PostgreSQL)

Para o modo SQL usa-se um PostgreSQL em Docker (`docker-compose.yml`), cujo esquema
(`initdb/01-schema.sql`) é aplicado automaticamente no primeiro arranque. O esquema inclui
`ON DELETE CASCADE` nas chaves estrangeiras, para que o último passo (apagar o utilizador)
respeite as tabelas dependentes (listas e itens).

```bash
# Terminal 2 — arrancar o PostgreSQL
cd testes-integracao && docker compose up -d

# Terminal 1 — API em modo SQL
cd api && REPO=sql mvn exec:java

# Terminal 2 — a mesma coleção
docker run --rm -v "${PWD}":/etc/newman -t postman/newman run postman.json

# Limpeza
docker compose down -v
```

**Resultado: idêntico — 11 pedidos, 19 asserções, 0 falhas**, agora persistindo em PostgreSQL.
O facto de a mesma coleção passar sem alterações nos dois backends demonstra que a camada de
repositório está corretamente abstraída.

![Newman — repositório SQL (PostgreSQL)](docs/img/05-newman-sql.png)

---

## 5. Parte 3 — Aplicação cliente multiplataforma

### 5.1. Arquitetura do cliente (Flutter)

O cliente `todoapp/` está organizado em:

| Ficheiro | Responsabilidade |
|----------|------------------|
| `lib/models.dart` | Modelos `TodoList` e `Todo` (desserialização do JSON da API) |
| `lib/api_client.dart` | Cliente HTTP: `login`, `getLists`, `createList`, `getTodos`, `createTodo`, `setCompleted`, `deleteTodo` |
| `lib/main.dart` | Interface: ecrãs de Login → Listas → Tarefas |

O endereço da API adapta-se à plataforma: `http://localhost:7100` no Desktop e
`http://10.0.2.2:7100` no emulador Android (onde `localhost` seria o próprio emulador). O token
`Bearer <username>` devolvido pelo login é enviado tal e qual no header `Authorization`.

### 5.2. a) Execução em Android e Desktop

Preparação (uma vez): `flutter pub get` e `flutter create --platforms=android,macos .`.
Execução: `flutter run -d macos` (Desktop) ou `flutter run -d <emulador>` (Android).

**Notas de configuração encontradas e resolvidas:**
- **macOS:** a aplicação corre em *sandbox* e bloqueia ligações de saída por omissão. Foi
  adicionada a *entitlement* `com.apple.security.network.client` em
  `macos/Runner/DebugProfile.entitlements` e `Release.entitlements`.
- **Android:** como a API de teste é HTTP (não HTTPS), é necessário
  `android:usesCleartextTraffic="true"` no `AndroidManifest.xml`.

![Aplicação em Desktop (macOS)](docs/img/06-flutter-desktop.png)

![Aplicação em Android](docs/img/07-flutter-android.png)

### 5.3. b) Marcar tarefas como completas (API + cliente)

A funcionalidade foi implementada em toda a stack:

- **API:** novos endpoints `PUT /todo/{listId}/tasks/{taskId}/complete` e `/incomplete`. O
  `TodoController` verifica a posse da lista, e o `TodoService.setCompleted(...)` altera o campo
  `completed` do item e persiste-o via `TodoRepository.update(...)` (implementado tanto no
  repositório em memória como no SQL).
- **Cliente:** cada tarefa é apresentada com uma *checkbox* (`CheckboxListTile`); ao alterá-la,
  o `ApiClient.setCompleted(...)` é chamado e a lista é recarregada, mostrando o item riscado
  quando completo.

![Tarefa marcada como completa no cliente](docs/img/08-flutter-completa.png)

---

## 6. Conclusão

Todos os objetivos do laboratório foram cumpridos:

- **Parte 1:** 19 testes unitários JUnit5 para `InMemoryUserRepository` e `TodoUserService`,
  com **100 % de cobertura de linhas** em ambas as classes.
- **Parte 2:** coleção Postman executada com Newman, com **19 asserções sem falhas** tanto no
  repositório em memória como em PostgreSQL.
- **Parte 3:** cliente Flutter a correr em Desktop e Android, estendido com a funcionalidade de
  marcar tarefas como completas (API + cliente).

O código, a coleção de testes e o cliente estão no repositório GitHub indicado no topo.
