package cncs.academy.ess.repository.memory;

import cncs.academy.ess.model.Reparo;
import cncs.academy.ess.repository.ReparoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryReparoRepository implements ReparoRepository {
    private final ConcurrentHashMap<Integer, Reparo> reparos = new ConcurrentHashMap<>();
    private final AtomicInteger currentId = new AtomicInteger(0);

    @Override
    public Reparo findById(int reparoId) {
        return reparos.get(reparoId);
    }

    @Override
    public List<Reparo> findAll() {
        return new ArrayList<>(reparos.values());
    }

    @Override
    public List<Reparo> findAllByPranchaId(int pranchaId) {
        return reparos.values().stream()
                .filter(reparo -> reparo.getPranchaId() == pranchaId)
                .toList();
    }

    @Override
    public int save(Reparo reparo) {
        int id = reparo.getId();
        if (id == 0) {
            reparo.setId(id = currentId.incrementAndGet());
        }
        reparos.put(id, reparo);
        return id;
    }

    @Override
    public void update(Reparo reparo) {
        reparos.put(reparo.getId(), reparo);
    }

    @Override
    public boolean deleteById(int reparoId) {
        return reparos.remove(reparoId) != null;
    }
}
