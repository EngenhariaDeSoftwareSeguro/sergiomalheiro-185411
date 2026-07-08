# sergiomalheiro-185411

Projeto Maven — API REST de gestão de consertos de pranchas de surf.

## Ambiente

- Apache Maven: 3.9.16
- Maven (modelVersion do POM): 4.0.0
- Java: 17
- Framework web: [Javalin](https://javalin.io/) 6.4.0
- JSON: Jackson 2.17

## Domínio

Uma oficina (`User`, com autenticação) gere:
User (oficina) 1 -> N Cliente 1 -> N Prancha 1 -> N Reparo 1 -> N Foto

- Cliente - nome, email, telefone. Um cliente pode ter (N) pranchas.
- Prancha — marca, modelo, tipo, dimensões. Uma prancha pode ter N reparos.
- Reparo — descrição do dano, estado (`PENDENTE`, `EM_CURSO`, `CONCLUIDO`), custo.
- Foto — carregam-se N fotos por reparo, marcadas com a fase:
  - `DANO` — fotos do dano antes do conserto;
  - `REPARO` — fotos do resultado depois do conserto.

Cada camada valida o acesso: um utilizador só acede a clientes/pranchas/reparos/fotos
que lhe pertencem (respostas `403` caso contrário, `401` sem token).

## Arquitetura

Camadas: `model` → `repository` (interface + implementação em memória) → `service`
→ `controller` (handlers Javalin) + DTOs em `controller/messages`. `App.java` monta
as rotas e injeta dados de exemplo.

## Como correr
```bash
cd Lab1
mvn clean package
java -jar target/surfrepair-phase1-1.0-SNAPSHOT-jar-with-dependencies.jar
```

O servidor arranca em `http://localhost:7100`. Utilizadores de exemplo:
`oficina1 / password1` e `oficina2 / password2`.

## Autenticação

```bash
# devolve um token "Bearer <username>" que deve ser enviado no header Authorization
curl -X POST http://localhost:7100/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"oficina1","password":"password1"}'
```

## Endpoints

Utilizadores / autenticação
- `POST /user` — Registar utilizador
- `POST /login` — Autenticar (devolve token)

Clientes
- `POST /cliente` — Criar cliente
- `GET /cliente` — Listar clientes do utilizador
- `GET /cliente/{id}` — Obter cliente
- `PUT /cliente/{id}` — Atualizar cliente
- `DELETE /cliente/{id}` — Apagar cliente
- `GET /cliente/{id}/pranchas` — Pranchas de um cliente

Pranchas
- `POST /prancha` — Criar prancha
- `GET /prancha/{id}` — Obter prancha
- `PUT /prancha/{id}` — Atualizar prancha
- `DELETE /prancha/{id}` — Apagar prancha
- `GET /prancha/{id}/reparos` — Reparos de uma prancha

Reparos
- `POST /reparo` — Criar reparo
- `GET /reparo/{id}` — Obter reparo
- `PUT /reparo/{id}` — Atualizar reparo (estado, custo, descrição)
- `DELETE /reparo/{id}` — Apagar reparo

Fotos
- `POST /reparo/{id}/fotos` — Carregar N fotos (`multipart`, `fase=DANO` ou `fase=REPARO`)
- `GET /reparo/{id}/fotos` — Listar fotos (metadados)
- `GET /reparo/{id}/fotos/{fotoId}` — Descarregar a imagem
- `DELETE /reparo/{id}/fotos/{fotoId}` — Apagar foto

### Exemplo — carregar fotos do dano

```bash
curl -X POST http://localhost:7100/reparo/1/fotos \
  -H 'Authorization: Bearer oficina1' \
  -F 'fase=DANO' \
  -F 'descricao=Fissura no nose' \
  -F 'ficheiro=@dano1.jpg' \
  -F 'ficheiro=@dano2.jpg'
```
