/// Uma lista de tarefas (GET /todolist).
class TodoList {
  final int listId;
  final String name;

  TodoList({required this.listId, required this.name});

  factory TodoList.fromJson(Map<String, dynamic> json) {
    return TodoList(
      listId: json['listId'] as int,
      name: json['name'] as String,
    );
  }
}

/// Um item de tarefa (GET /todo/{listId}/tasks).
class Todo {
  final int id;
  final String description;
  final bool completed;
  final int listId;

  Todo({
    required this.id,
    required this.description,
    required this.completed,
    required this.listId,
  });

  factory Todo.fromJson(Map<String, dynamic> json) {
    return Todo(
      id: json['id'] as int,
      description: json['description'] as String,
      completed: json['completed'] as bool,
      listId: json['listId'] as int,
    );
  }
}
