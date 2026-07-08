package cncs.academy.ess.service;

import cncs.academy.ess.model.EstadoReparo;
import cncs.academy.ess.model.Reparo;
import cncs.academy.ess.repository.PranchaRepository;
import cncs.academy.ess.repository.ReparoRepository;

import java.util.List;

public class ReparoService {
    private final ReparoRepository reparoRepository;
    private final PranchaRepository pranchaRepository;

    public ReparoService(ReparoRepository reparoRepository, PranchaRepository pranchaRepository) {
        this.reparoRepository = reparoRepository;
        this.pranchaRepository = pranchaRepository;
    }

    public Reparo createReparo(String descricao, double custo, int pranchaId) {
        if (pranchaRepository.findById(pranchaId) == null) {
            throw new IllegalArgumentException("Prancha not found");
        }
        Reparo reparo = new Reparo(descricao, custo, pranchaId);
        int id = reparoRepository.save(reparo);
        reparo.setId(id);
        return reparo;
    }

    public Reparo getReparo(int reparoId) {
        return reparoRepository.findById(reparoId);
    }

    public List<Reparo> getAllReparosByPrancha(int pranchaId) {
        return reparoRepository.findAllByPranchaId(pranchaId);
    }

    public Reparo updateReparo(int reparoId, String descricao, EstadoReparo estado, Double custo) {
        Reparo reparo = reparoRepository.findById(reparoId);
        if (reparo == null) {
            return null;
        }
        if (descricao != null) {
            reparo.setDescricao(descricao);
        }
        if (estado != null) {
            reparo.setEstado(estado);
        }
        if (custo != null) {
            reparo.setCusto(custo);
        }
        reparoRepository.update(reparo);
        return reparo;
    }

    public boolean deleteReparo(int reparoId) {
        return reparoRepository.deleteById(reparoId);
    }
}
