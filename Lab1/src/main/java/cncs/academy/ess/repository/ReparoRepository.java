package cncs.academy.ess.repository;

import cncs.academy.ess.model.Reparo;

import java.util.List;

public interface ReparoRepository {
    Reparo findById(int reparoId);
    List<Reparo> findAll();
    List<Reparo> findAllByPranchaId(int pranchaId);
    int save(Reparo reparo);
    void update(Reparo reparo);
    boolean deleteById(int reparoId);
}
