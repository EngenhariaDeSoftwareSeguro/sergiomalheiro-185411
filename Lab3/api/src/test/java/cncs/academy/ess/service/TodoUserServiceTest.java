package cncs.academy.ess.service;

import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.memory.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários de {@link TodoUserService} (Lab3 - parte 1, o "ToDoUserService" do enunciado).
 * Usa um {@link InMemoryUserRepository} real como colaborador (determinístico, sem dependências).
 */
class TodoUserServiceTest {

    private TodoUserService userService;

    @BeforeEach
    void setUp() {
        userService = new TodoUserService(new InMemoryUserRepository());
    }

    @Test
    @DisplayName("addUser() cria utilizador com id atribuído")
    void addUserAssignsId() throws NoSuchAlgorithmException {
        User user = userService.addUser("alice", "s3cret");
        assertTrue(user.getId() > 0);
        assertEquals("alice", user.getUsername());
    }

    @Test
    @DisplayName("getUser() devolve o utilizador criado")
    void getUserReturnsCreated() throws NoSuchAlgorithmException {
        User created = userService.addUser("alice", "s3cret");
        User found = userService.getUser(created.getId());
        assertNotNull(found);
        assertEquals("alice", found.getUsername());
    }

    @Test
    @DisplayName("getUser() devolve null para id inexistente")
    void getUserReturnsNullWhenMissing() {
        assertNull(userService.getUser(404));
    }

    @Test
    @DisplayName("getAllUsers() reflete os utilizadores criados")
    void getAllUsersReflectsState() throws NoSuchAlgorithmException {
        assertTrue(userService.getAllUsers().isEmpty());
        userService.addUser("alice", "a");
        userService.addUser("bob", "b");
        assertEquals(2, userService.getAllUsers().size());
    }

    @Test
    @DisplayName("deleteUser() remove o utilizador")
    void deleteUserRemoves() throws NoSuchAlgorithmException {
        User created = userService.addUser("alice", "s3cret");
        userService.deleteUser(created.getId());
        assertNull(userService.getUser(created.getId()));
    }

    @Test
    @DisplayName("login() com credenciais corretas devolve token 'Bearer <username>'")
    void loginValidReturnsToken() throws NoSuchAlgorithmException {
        userService.addUser("alice", "s3cret");
        String token = userService.login("alice", "s3cret");
        assertEquals("Bearer alice", token);
    }

    @Test
    @DisplayName("login() com password errada devolve null")
    void loginWrongPasswordReturnsNull() throws NoSuchAlgorithmException {
        userService.addUser("alice", "s3cret");
        assertNull(userService.login("alice", "errada"));
    }

    @Test
    @DisplayName("login() de utilizador inexistente devolve null")
    void loginUnknownUserReturnsNull() throws NoSuchAlgorithmException {
        assertNull(userService.login("ghost", "whatever"));
    }
}
