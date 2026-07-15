# todoapp — cliente Flutter multiplataforma (Lab3, parte 3)

Cliente para a Todo API do Lab3: faz login, mostra as listas do utilizador, abre uma
lista, adiciona tarefas e **marca-as como completas** (checkbox).

Só o código-fonte (`lib/`, `pubspec.yaml`) está no repositório. As pastas de cada
plataforma (`android/`, `macos/`, ...) são geradas pelo Flutter — passo 2.

## Pré-requisitos

- Flutter SDK (`flutter --version`). Ver o guia no README principal do Lab3.
- API do Lab3 a correr em `http://localhost:7100`:
  ```bash
  cd ../api
  REPO=memory mvn exec:java
  ```

## 1. Dependências

```bash
cd todoapp
flutter pub get
```

## 2. Gerar as pastas de plataforma (uma vez)

`flutter create` preserva o `lib/` e o `pubspec.yaml` e só acrescenta os runners nativos:

```bash
flutter create --platforms=android,macos,windows,linux .
```

## 3. Correr

```bash
flutter devices
flutter run -d macos          # Desktop -> usa http://localhost:7100
flutter run -d emulator-5554  # Android -> usa http://10.0.2.2:7100
```

O campo "Servidor" no ecrã de login permite alterar o endereço.

## Credenciais de teste (dados semente da API)

| Utilizador | Password  |
|------------|-----------|
| user1      | password1 |
| user2      | password2 |

O utilizador `user1` já tem a lista "Shopping list" com alguns itens.

## Funcionalidade acrescentada (parte 3b): marcar tarefas como completas

- **API:** `PUT /todo/{listId}/tasks/{taskId}/complete` e `/incomplete`
  (`TodoController.completeTodoItem` / `TodoService.setCompleted`).
- **Cliente:** a checkbox de cada tarefa chama `ApiClient.setCompleted(...)` e recarrega.

## Nota Android — HTTP em claro

A API de teste corre em HTTP. Depois do `flutter create`, adiciona ao
`android/app/src/main/AndroidManifest.xml`, na tag `<application ...>`:

```xml
android:usesCleartextTraffic="true"
```
(Só para testes locais.)
