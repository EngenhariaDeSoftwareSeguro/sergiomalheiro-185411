import 'dart:convert';

import 'package:http/http.dart' as http;

import 'models.dart';

/// Cliente da Todo API do Lab3.
///
/// O token devolvido pelo /login é a string "Bearer <username>" já pronta a ser
/// colocada no header Authorization tal como está.
///
/// baseUrl depende do sítio onde a app corre:
///  - Desktop (macOS/Windows/Linux): http://localhost:7100
///  - Emulador Android: http://10.0.2.2:7100 (o host visto de dentro do emulador)
class ApiClient {
  ApiClient(this.baseUrl);

  final String baseUrl;
  String? _token;

  bool get isLoggedIn => _token != null;

  Uri _uri(String path) => Uri.parse('$baseUrl$path');

  Map<String, String> _authHeaders({bool json = false}) {
    final headers = <String, String>{};
    if (json) headers['Content-Type'] = 'application/json';
    if (_token != null) headers['Authorization'] = _token!;
    return headers;
  }

  /// POST /login -> guarda o token cru ("Bearer <username>"). Devolve true em sucesso.
  Future<bool> login(String username, String password) async {
    final response = await http.post(
      _uri('/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'username': username, 'password': password}),
    );
    if (response.statusCode == 200) {
      _token = response.body.trim(); // corpo é a string "Bearer <username>"
      return true;
    }
    return false;
  }

  void logout() => _token = null;

  /// GET /todolist -> as listas do utilizador.
  Future<List<TodoList>> getLists() async {
    final response = await http.get(_uri('/todolist'), headers: _authHeaders());
    if (response.statusCode != 200) {
      throw Exception('Falha ao obter listas (${response.statusCode})');
    }
    final data = jsonDecode(response.body) as List<dynamic>;
    return data.map((e) => TodoList.fromJson(e as Map<String, dynamic>)).toList();
  }

  /// POST /todolist -> cria uma lista.
  Future<TodoList> createList(String name) async {
    final response = await http.post(
      _uri('/todolist'),
      headers: _authHeaders(json: true),
      body: jsonEncode({'listName': name}),
    );
    if (response.statusCode != 201) {
      throw Exception('Falha ao criar lista (${response.statusCode})');
    }
    return TodoList.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  /// GET /todo/{listId}/tasks -> os itens de uma lista.
  Future<List<Todo>> getTodos(int listId) async {
    final response =
        await http.get(_uri('/todo/$listId/tasks'), headers: _authHeaders());
    if (response.statusCode != 200) {
      throw Exception('Falha ao obter tarefas (${response.statusCode})');
    }
    final data = jsonDecode(response.body) as List<dynamic>;
    return data.map((e) => Todo.fromJson(e as Map<String, dynamic>)).toList();
  }

  /// POST /todo/item -> cria um item.
  Future<Todo> createTodo(int listId, String description) async {
    final response = await http.post(
      _uri('/todo/item'),
      headers: _authHeaders(json: true),
      body: jsonEncode({'description': description, 'listId': listId}),
    );
    if (response.statusCode != 200) {
      throw Exception('Falha ao criar tarefa (${response.statusCode})');
    }
    return Todo.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  /// PUT /todo/{listId}/tasks/{taskId}/complete|incomplete -> marca completo (parte 3b).
  Future<Todo> setCompleted(int listId, int todoId, bool completed) async {
    final suffix = completed ? 'complete' : 'incomplete';
    final response = await http.put(
      _uri('/todo/$listId/tasks/$todoId/$suffix'),
      headers: _authHeaders(),
    );
    if (response.statusCode != 200) {
      throw Exception('Falha ao atualizar tarefa (${response.statusCode})');
    }
    return Todo.fromJson(jsonDecode(response.body) as Map<String, dynamic>);
  }

  /// DELETE /todo/{listId}/tasks/{taskId}
  Future<void> deleteTodo(int listId, int todoId) async {
    final response = await http.delete(
      _uri('/todo/$listId/tasks/$todoId'),
      headers: _authHeaders(),
    );
    if (response.statusCode != 203) {
      throw Exception('Falha ao apagar tarefa (${response.statusCode})');
    }
  }
}
