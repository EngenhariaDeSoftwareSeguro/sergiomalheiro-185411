package cncs.academy.ess.repository;

import cncs.academy.ess.model.Cliente;

import java.util.List;

public interface ClienteRepository {
    Cliente findById(int clienteId);
    List<Cliente> findAll();
    List<Cliente> findAllByOwnerId(int ownerId);
    int save(Cliente cliente);
    void update(Cliente cliente);
    boolean deleteById(int clienteId);
}
