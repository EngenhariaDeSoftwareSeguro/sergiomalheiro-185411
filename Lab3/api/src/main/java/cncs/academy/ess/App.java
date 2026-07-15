package cncs.academy.ess;

import cncs.academy.ess.controller.AuthorizationMiddleware;
import cncs.academy.ess.controller.TodoController;
import cncs.academy.ess.controller.TodoListController;
import cncs.academy.ess.controller.UserController;
import cncs.academy.ess.repository.TodoListsRepository;
import cncs.academy.ess.repository.TodoRepository;
import cncs.academy.ess.repository.UserRepository;
import cncs.academy.ess.repository.memory.InMemoryTodoListsRepository;
import cncs.academy.ess.repository.memory.InMemoryTodoRepository;
import cncs.academy.ess.repository.memory.InMemoryUserRepository;
import cncs.academy.ess.repository.sql.SQLTodoListsRepository;
import cncs.academy.ess.repository.sql.SQLTodoRespository;
import cncs.academy.ess.repository.sql.SQLUserRepository;
import cncs.academy.ess.service.TodoListsService;
import cncs.academy.ess.service.TodoUserService;
import cncs.academy.ess.service.TodoService;
import io.javalin.Javalin;
import org.apache.commons.dbcp2.BasicDataSource;

import java.security.NoSuchAlgorithmException;

public class App {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
        }).start(7100);

        // Repository backend: in-memory by default, or SQL (PostgreSQL) when REPO=sql.
        // In-memory keeps parte 2a runnable with no external database; parte 2b uses REPO=sql.
        boolean useSql = "sql".equalsIgnoreCase(env("REPO", "memory"));

        UserRepository userRepository;
        TodoListsRepository listsRepository;
        TodoRepository todoRepository;
        if (useSql) {
            BasicDataSource ds = buildDataSource();
            userRepository = new SQLUserRepository(ds);
            listsRepository = new SQLTodoListsRepository(ds);
            todoRepository = new SQLTodoRespository(ds);
            System.out.println("[App] Using SQL (PostgreSQL) repositories");
        } else {
            userRepository = new InMemoryUserRepository();
            listsRepository = new InMemoryTodoListsRepository();
            todoRepository = new InMemoryTodoRepository();
            System.out.println("[App] Using in-memory repositories");
        }

        TodoUserService userService = new TodoUserService(userRepository);
        UserController userController = new UserController(userService);

        TodoListsService toDoListService = new TodoListsService(listsRepository);
        TodoListController todoListController = new TodoListController(toDoListService);

        TodoService todoService = new TodoService(todoRepository, listsRepository);
        TodoController todoController = new TodoController(todoService, toDoListService);

        AuthorizationMiddleware authMiddleware = new AuthorizationMiddleware(userRepository);

        // CORS
        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "*");
        });
        // Authorization middleware
        app.before(authMiddleware::handle);

        // User management
        app.post("/user", userController::createUser);
        app.get("/user/{userId}", userController::getUser);
        app.delete("/user/{userId}", userController::deleteUser);
        app.post("/login", userController::loginUser);

        // "To do" lists management
        app.post("/todolist", todoListController::createTodoList);
        app.get("/todolist", todoListController::getAllTodoLists);
        app.get("/todolist/{listId}", todoListController::getTodoList);

        // "To do" list items management
        app.post("/todo/item", todoController::createTodoItem);
        app.get("/todo/{listId}/tasks", todoController::getAllTodoItems);
        app.get("/todo/{listId}/tasks/{taskId}", todoController::getTodoItem);
        app.delete("/todo/{listId}/tasks/{taskId}", todoController::deleteTodoItem);
        // Lab3 parte 3b: marcar tarefas como completas / reabrir
        app.put("/todo/{listId}/tasks/{taskId}/complete", todoController::completeTodoItem);
        app.put("/todo/{listId}/tasks/{taskId}/incomplete", todoController::reopenTodoItem);

        // Seed sample data only when the store is empty (idempotent across SQL restarts).
        if (userService.getAllUsers().isEmpty()) {
            fillDummyData(userService, toDoListService, todoService);
        }
    }

    private static String env(String key, String def) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? def : value;
    }

    private static BasicDataSource buildDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        String url = String.format("jdbc:postgresql://%s:%s/%s?user=%s&password=%s",
                env("DB_HOST", "localhost"),
                env("DB_PORT", "5432"),
                env("DB_NAME", "postgres"),
                env("DB_USER", "postgres"),
                env("DB_PASSWORD", "changeit"));
        ds.setUrl(url);
        return ds;
    }

    private static void fillDummyData(
            TodoUserService userService,
            TodoListsService toDoListService,
            TodoService todoService) throws NoSuchAlgorithmException {
        userService.addUser("user1", "password1");
        userService.addUser("user2", "password2");
        toDoListService.createTodoListItem("Shopping list", 1);
        toDoListService.createTodoListItem("Other", 1);
        todoService.createTodoItem("Bread", 1);
        todoService.createTodoItem("Milk", 1);
        todoService.createTodoItem("Eggs", 1);
        todoService.createTodoItem("Cheese", 1);
        todoService.createTodoItem("Butter", 1);
    }
}
