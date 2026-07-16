import 'package:flutter/foundation.dart' show defaultTargetPlatform, TargetPlatform;
import 'package:flutter/material.dart';

import 'api_client.dart';
import 'models.dart';

void main() => runApp(const TodoApp());

/// Emulador Android chega ao host em 10.0.2.2; desktop usa localhost.
String defaultBaseUrl() {
  if (defaultTargetPlatform == TargetPlatform.android) {
    return 'http://10.0.2.2:7100';
  }
  return 'http://localhost:7100';
}

class TodoApp extends StatelessWidget {
  const TodoApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Lab3 Todo',
      theme: ThemeData(colorSchemeSeed: Colors.indigo, useMaterial3: true),
      home: const LoginScreen(),
    );
  }
}

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _host = TextEditingController(text: defaultBaseUrl());
  final _username = TextEditingController(text: 'user1');
  final _password = TextEditingController(text: 'password1');
  bool _busy = false;
  String? _error;

  Future<void> _login() async {
    setState(() {
      _busy = true;
      _error = null;
    });
    final api = ApiClient(_host.text.trim());
    try {
      final ok = await api.login(_username.text.trim(), _password.text);
      if (!mounted) return;
      if (ok) {
        Navigator.of(context).push(
          MaterialPageRoute(builder: (_) => ListsScreen(api: api)),
        );
      } else {
        setState(() => _error = 'Credenciais inválidas');
      }
    } catch (e) {
      setState(() => _error = 'Erro de ligação: $e');
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Lab3 Todo — Login')),
      body: Center(
        child: ConstrainedBox(
          constraints: const BoxConstraints(maxWidth: 380),
          child: Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextField(controller: _host, decoration: const InputDecoration(labelText: 'Servidor')),
                TextField(controller: _username, decoration: const InputDecoration(labelText: 'Utilizador')),
                TextField(
                  controller: _password,
                  decoration: const InputDecoration(labelText: 'Password'),
                  obscureText: true,
                  onSubmitted: (_) => _login(),
                ),
                const SizedBox(height: 16),
                if (_error != null)
                  Padding(
                    padding: const EdgeInsets.only(bottom: 8),
                    child: Text(_error!, style: const TextStyle(color: Colors.red)),
                  ),
                SizedBox(
                  width: double.infinity,
                  child: FilledButton(
                    onPressed: _busy ? null : _login,
                    child: _busy
                        ? const SizedBox(height: 18, width: 18, child: CircularProgressIndicator(strokeWidth: 2))
                        : const Text('Entrar'),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class ListsScreen extends StatefulWidget {
  const ListsScreen({super.key, required this.api});

  final ApiClient api;

  @override
  State<ListsScreen> createState() => _ListsScreenState();
}

class _ListsScreenState extends State<ListsScreen> {
  late Future<List<TodoList>> _lists;

  @override
  void initState() {
    super.initState();
    _reload();
  }

  void _reload() {
    setState(() {
      _lists = widget.api.getLists();
    });
  }

  Future<void> _addListDialog() async {
    final name = await _promptText(context, 'Nova lista');
    if (name != null && name.trim().isNotEmpty) {
      await widget.api.createList(name.trim());
      _reload();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('As minhas listas'),
        actions: [IconButton(icon: const Icon(Icons.refresh), onPressed: _reload)],
      ),
      floatingActionButton: FloatingActionButton(onPressed: _addListDialog, child: const Icon(Icons.add)),
      body: FutureBuilder<List<TodoList>>(
        future: _lists,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }
          if (snapshot.hasError) return Center(child: Text('${snapshot.error}'));
          final lists = snapshot.data ?? [];
          if (lists.isEmpty) {
            return const Center(child: Text('Sem listas. Toca em + para criar.'));
          }
          return ListView.separated(
            itemCount: lists.length,
            separatorBuilder: (_, __) => const Divider(height: 1),
            itemBuilder: (context, i) {
              final list = lists[i];
              return ListTile(
                leading: const Icon(Icons.list_alt),
                title: Text(list.name),
                trailing: const Icon(Icons.chevron_right),
                onTap: () => Navigator.of(context).push(
                  MaterialPageRoute(builder: (_) => TodosScreen(api: widget.api, list: list)),
                ),
              );
            },
          );
        },
      ),
    );
  }
}

class TodosScreen extends StatefulWidget {
  const TodosScreen({super.key, required this.api, required this.list});

  final ApiClient api;
  final TodoList list;

  @override
  State<TodosScreen> createState() => _TodosScreenState();
}

class _TodosScreenState extends State<TodosScreen> {
  late Future<List<Todo>> _todos;

  @override
  void initState() {
    super.initState();
    _reload();
  }

  void _reload() {
    setState(() {
      _todos = widget.api.getTodos(widget.list.listId);
    });
  }

  Future<void> _toggle(Todo todo, bool completed) async {
    await widget.api.setCompleted(widget.list.listId, todo.id, completed);
    _reload();
  }

  Future<void> _delete(Todo todo) async {
    await widget.api.deleteTodo(widget.list.listId, todo.id);
    _reload();
  }

  Future<void> _addTodoDialog() async {
    final desc = await _promptText(context, 'Nova tarefa');
    if (desc != null && desc.trim().isNotEmpty) {
      await widget.api.createTodo(widget.list.listId, desc.trim());
      _reload();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.list.name),
        actions: [IconButton(icon: const Icon(Icons.refresh), onPressed: _reload)],
      ),
      floatingActionButton: FloatingActionButton(onPressed: _addTodoDialog, child: const Icon(Icons.add)),
      body: FutureBuilder<List<Todo>>(
        future: _todos,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }
          if (snapshot.hasError) return Center(child: Text('${snapshot.error}'));
          final todos = snapshot.data ?? [];
          if (todos.isEmpty) {
            return const Center(child: Text('Sem tarefas. Toca em + para adicionar.'));
          }
          return ListView.separated(
            itemCount: todos.length,
            separatorBuilder: (_, __) => const Divider(height: 1),
            itemBuilder: (context, i) {
              final todo = todos[i];
              return CheckboxListTile(
                value: todo.completed,
                onChanged: (v) => _toggle(todo, v ?? false),
                title: Text(
                  todo.description,
                  style: TextStyle(
                    decoration: todo.completed ? TextDecoration.lineThrough : null,
                  ),
                ),
                secondary: IconButton(
                  icon: const Icon(Icons.delete_outline),
                  onPressed: () => _delete(todo),
                ),
              );
            },
          );
        },
      ),
    );
  }
}

/// Diálogo simples de entrada de texto reutilizado por listas e tarefas.
Future<String?> _promptText(BuildContext context, String hint) {
  final controller = TextEditingController();
  return showDialog<String>(
    context: context,
    builder: (ctx) => AlertDialog(
      title: Text(hint),
      content: TextField(
        controller: controller,
        autofocus: true,
        onSubmitted: (v) => Navigator.of(ctx).pop(v),
      ),
      actions: [
        TextButton(onPressed: () => Navigator.of(ctx).pop(), child: const Text('Cancelar')),
        FilledButton(onPressed: () => Navigator.of(ctx).pop(controller.text), child: const Text('OK')),
      ],
    ),
  );
}
