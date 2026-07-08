package cncs.academy.ess.service;

import cncs.academy.ess.model.Cliente;
import cncs.academy.ess.repository.ClienteRepository;

import java.util.List;

public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente createCliente(String nome, String email, String telefone, int ownerId) {
        Cliente cliente = new Cliente(nome, email, telefone, ownerId);
        int id = clienteRepository.save(cliente);
        cliente.setId(id);
        return cliente;
    }

    public Cliente getCliente(int clienteId) {
        return clienteRepository.findById(clienteId);
    }

    public List<Cliente> getAllClientesByOwner(int ownerId) {
        return clienteRepository.findAllByOwnerId(ownerId);
    }

    public Cliente updateCliente(int clienteId, String nome, String email, String telefone) {
        Cliente cliente = clienteRepository.findById(clienteId);
        if (cliente == null) {
            return null;
        }
        if (nome != null) {
            cliente.setNome(nome);
        }
        if (email != null) {
            cliente.setEmail(email);
        }
        if (telefone != null) {
            cliente.setTelefone(telefone);
        }
        clienteRepository.update(cliente);
        return cliente;
    }

    public boolean deleteCliente(int clienteId) {
        return clienteRepository.deleteById(clienteId);
    }
}
