package cncs.academy.ess.service;

import cncs.academy.ess.model.Prancha;
import cncs.academy.ess.repository.ClienteRepository;
import cncs.academy.ess.repository.PranchaRepository;

import java.util.List;

public class PranchaService {
    private final PranchaRepository pranchaRepository;
    private final ClienteRepository clienteRepository;

    public PranchaService(PranchaRepository pranchaRepository, ClienteRepository clienteRepository) {
        this.pranchaRepository = pranchaRepository;
        this.clienteRepository = clienteRepository;
    }

    public Prancha createPrancha(String marca, String modelo, String tipo, String dimensoes, int clienteId) {
        if (clienteRepository.findById(clienteId) == null) {
            throw new IllegalArgumentException("Cliente not found");
        }
        Prancha prancha = new Prancha(marca, modelo, tipo, dimensoes, clienteId);
        int id = pranchaRepository.save(prancha);
        prancha.setId(id);
        return prancha;
    }

    public Prancha getPrancha(int pranchaId) {
        return pranchaRepository.findById(pranchaId);
    }

    public List<Prancha> getAllPranchasByCliente(int clienteId) {
        return pranchaRepository.findAllByClienteId(clienteId);
    }

    public Prancha updatePrancha(int pranchaId, String marca, String modelo, String tipo, String dimensoes) {
        Prancha prancha = pranchaRepository.findById(pranchaId);
        if (prancha == null) {
            return null;
        }
        if (marca != null) {
            prancha.setMarca(marca);
        }
        if (modelo != null) {
            prancha.setModelo(modelo);
        }
        if (tipo != null) {
            prancha.setTipo(tipo);
        }
        if (dimensoes != null) {
            prancha.setDimensoes(dimensoes);
        }
        pranchaRepository.update(prancha);
        return prancha;
    }

    public boolean deletePrancha(int pranchaId) {
        return pranchaRepository.deleteById(pranchaId);
    }
}
