package cncs.academy.ess.repository.memory;

import cncs.academy.ess.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários de {@link InMemoryUserRepository} (Lab3 - parte 1).
 * Exercita todos os métodos públicos do repositório de forma isolada.
 */
class InMemoryUserRepositoryTest {

    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
    }

    private User newUser(String username) {
        return new User(username, "pw-" + username);
    }

    @Test
    @DisplayName("save() atribui um id > 0 a um utilizador novo")
    void saveAssignsGeneratedId() {
        int id = repository.save(newUser("alice"));
        assertTrue(id > 0);
    }

    @Test
    @DisplayName("save() gera ids distintos e incrementais")
    void saveGeneratesDistinctIds() {
        int first = repository.save(newUser("alice"));
        int second = repository.save(newUser("bob"));
        assertNotEquals(first, second);
        assertEquals(first + 1, second);
    }

    @Test
    @DisplayName("save() respeita um id já atribuído (id != 0)")
    void savePreservesExistingId() {
        User user = new User(42, "carol", "pw");
        int id = repository.save(user);
        assertEquals(42, id);
        assertEquals("carol", repository.findById(42).getUsername());
    }

    @Test
    @DisplayName("findById() devolve o utilizador guardado")
    void findByIdReturnsSaved() {
        int id = repository.save(newUser("alice"));
        assertEquals("alice", repository.findById(id).getUsername());
    }

    @Test
    @DisplayName("findById() devolve null para id inexistente")
    void findByIdReturnsNullWhenMissing() {
        assertNull(repository.findById(999));
    }

    @Test
    @DisplayName("findAll() devolve todos os utilizadores")
    void findAllReturnsEverything() {
        repository.save(newUser("alice"));
        repository.save(newUser("bob"));
        List<User> all = repository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("findAll() vazio num repositório novo")
    void findAllEmptyByDefault() {
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    @DisplayName("findByUsername() encontra pelo nome")
    void findByUsernameReturnsMatch() {
        repository.save(newUser("alice"));
        assertEquals("alice", repository.findByUsername("alice").getUsername());
    }

    @Test
    @DisplayName("findByUsername() devolve null quando não existe")
    void findByUsernameReturnsNullWhenMissing() {
        repository.save(newUser("alice"));
        assertNull(repository.findByUsername("ghost"));
    }

    @Test
    @DisplayName("deleteById() remove o utilizador")
    void deleteByIdRemoves() {
        int id = repository.save(newUser("alice"));
        repository.deleteById(id);
        assertNull(repository.findById(id));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    @DisplayName("deleteById() de id inexistente não altera o estado")
    void deleteByIdIgnoresMissing() {
        int id = repository.save(newUser("alice"));
        repository.deleteById(12345);
        assertEquals(1, repository.findAll().size());
        assertEquals("alice", repository.findById(id).getUsername());
    }
}
