package cncs.academy.ess.repository.memory;

import cncs.academy.ess.model.Prancha;
import cncs.academy.ess.repository.PranchaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryPranchaRepository implements PranchaRepository {
    private final ConcurrentHashMap<Integer, Prancha> pranchas = new ConcurrentHashMap<>();
    private final AtomicInteger currentId = new AtomicInteger(0);

    @Override
    public Prancha findById(int pranchaId) {
        return pranchas.get(pranchaId);
    }

    @Override
    public List<Prancha> findAll() {
        return new ArrayList<>(pranchas.values());
    }

    @Override
    public List<Prancha> findAllByClienteId(int clienteId) {
        return pranchas.values().stream()
                .filter(prancha -> prancha.getClienteId() == clienteId)
                .toList();
    }

    @Override
    public int save(Prancha prancha) {
        int id = prancha.getId();
        if (id == 0) {
            prancha.setId(id = currentId.incrementAndGet());
        }
        pranchas.put(id, prancha);
        return id;
    }

    @Override
    public void update(Prancha prancha) {
        pranchas.put(prancha.getId(), prancha);
    }

    @Override
    public boolean deleteById(int pranchaId) {
        return pranchas.remove(pranchaId) != null;
    }
}
