# Lab3 — Testes e aplicação multiplataforma

Engenharia de Software Seguro · Sérgio Malheiro · nº 185411

Parte da API Todo dos laboratórios anteriores (`Lab1_withSQL`) e acrescenta testes
unitários com cobertura, testes de integração com Postman/Newman (memória e SQL) e um
cliente multiplataforma Flutter com a funcionalidade de marcar tarefas como completas.

## Organização (por pastas)

| Pasta | Conteúdo | Parte |
|-------|----------|-------|
| [`api/`](api/) | Projeto Maven da API + testes unitários (`src/test`) | 1 |
| [`testes-integracao/`](testes-integracao/) | Coleção Postman + Newman + Postgres (Docker) | 2 e 2b |
| [`todoapp/`](todoapp/) | Cliente Flutter multiplataforma | 3 |

> Os testes unitários vivem dentro do projeto Maven (`api/src/test`) porque o `mvn test`
> exige que estejam junto do código que testam — não é possível pô-los numa pasta física
> separada. A integração (Postman) e a app (Flutter) são artefactos independentes, cada um
> na sua pasta.

## Requisitos

JDK 17+ (testado com 25), Maven 3.9+, Docker (Newman + PostgreSQL) e Flutter (parte 3).

## Parte 1 — Testes unitários e cobertura

Classes-alvo: `InMemoryUserRepository` e `TodoUserService`.

```bash
cd api
mvn test
open target/site/jacoco/index.html   # cobertura (JaCoCo)
```
Resultado: **19 testes verdes**, cobertura de **100 %** de linhas em ambas as classes.

## Parte 2 — Testes de integração

Ver [`testes-integracao/README.md`](testes-integracao/README.md). Resumo:

```bash
# 2a — repositório em memória
cd api && REPO=memory mvn exec:java          # terminal 1
cd testes-integracao
docker run --rm -v "${PWD}":/etc/newman -t postman/newman run postman.json

# 2b — repositório SQL (PostgreSQL)
cd testes-integracao && docker compose up -d # arranca o Postgres
cd ../api && REPO=sql mvn exec:java           # terminal 1
cd ../testes-integracao
docker run --rm -v "${PWD}":/etc/newman -t postman/newman run postman.json
```
Resultado nos dois modos: **11 pedidos, 19 asserções, 0 falhas**.

## Parte 3 — Cliente Flutter

Ver [`todoapp/README.md`](todoapp/README.md). Login, listas, tarefas e marcar completa.
A funcionalidade nova (parte 3b) foi adicionada na API (`PUT .../complete`) e no cliente.

---

## O que preciso de instalar no Mac (VS Code)

Já instalado: **JDK 25, Maven, Docker, VS Code**. Falta:

**Extensões do VS Code:**
- `Extension Pack for Java` (Microsoft) — compilar/correr/debug Java + Maven
- `Flutter` (Dart-Code) — inclui o Dart

**Flutter + Android (parte 3):**
```bash
brew install --cask flutter
brew install --cask android-studio   # Android SDK + emulador
xcode-select --install               # compilar o desktop macOS
flutter doctor                        # valida e indica o que falta
```
No Android Studio: *More Actions → Virtual Device Manager* para criar um emulador.
