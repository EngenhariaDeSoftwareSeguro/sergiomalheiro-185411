package cncs.academy.ess.repository;

import cncs.academy.ess.model.User;

import java.util.List;

public interface UserRepository {
    User findById(int userId);
    List<User> findAll();
    int save(User user);
    void update(User user);
    void deleteById(int userId);

    User findByUsername(String username);
}
