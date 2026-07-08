package cncs.academy.ess.repository.memory;

import cncs.academy.ess.model.Foto;
import cncs.academy.ess.repository.FotoRepository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryFotoRepository implements FotoRepository {
    private final ConcurrentHashMap<Integer, Foto> fotos = new ConcurrentHashMap<>();
    private final AtomicInteger currentId = new AtomicInteger(0);

    @Override
    public Foto findById(int fotoId) {
        return fotos.get(fotoId);
    }

    @Override
    public List<Foto> findAllByReparoId(int reparoId) {
        return fotos.values().stream()
                .filter(foto -> foto.getReparoId() == reparoId)
                .toList();
    }

    @Override
    public int save(Foto foto) {
        int id = foto.getId();
        if (id == 0) {
            foto.setId(id = currentId.incrementAndGet());
        }
        fotos.put(id, foto);
        return id;
    }

    @Override
    public boolean deleteById(int fotoId) {
        return fotos.remove(fotoId) != null;
    }
}
