package cncs.academy.ess.repository.memory;

import cncs.academy.ess.model.Cliente;
import cncs.academy.ess.repository.ClienteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryClienteRepository implements ClienteRepository {
    private final ConcurrentHashMap<Integer, Cliente> clientes = new ConcurrentHashMap<>();
    private final AtomicInteger currentId = new AtomicInteger(0);

    @Override
    public Cliente findById(int clienteId) {
        return clientes.get(clienteId);
    }

    @Override
    public List<Cliente> findAll() {
        return new ArrayList<>(clientes.values());
    }

    @Override
    public List<Cliente> findAllByOwnerId(int ownerId) {
        return clientes.values().stream()
                .filter(cliente -> cliente.getOwnerId() == ownerId)
                .toList();
    }

    @Override
    public int save(Cliente cliente) {
        int id = cliente.getId();
        if (id == 0) {
            cliente.setId(id = currentId.incrementAndGet());
        }
        clientes.put(id, cliente);
        return id;
    }

    @Override
    public void update(Cliente cliente) {
        clientes.put(cliente.getId(), cliente);
    }

    @Override
    public boolean deleteById(int clienteId) {
        return clientes.remove(clienteId) != null;
    }
}
