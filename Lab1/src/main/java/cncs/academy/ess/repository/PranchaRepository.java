package cncs.academy.ess.repository;

import cncs.academy.ess.model.Prancha;

import java.util.List;

public interface PranchaRepository {
    Prancha findById(int pranchaId);
    List<Prancha> findAll();
    List<Prancha> findAllByClienteId(int clienteId);
    int save(Prancha prancha);
    void update(Prancha prancha);
    boolean deleteById(int pranchaId);
}
