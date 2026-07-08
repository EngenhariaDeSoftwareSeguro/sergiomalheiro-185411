package cncs.academy.ess.controller;

import cncs.academy.ess.controller.messages.ClienteRequest;
import cncs.academy.ess.controller.messages.ErrorMessage;
import cncs.academy.ess.model.Cliente;
import cncs.academy.ess.service.ClienteService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClienteController {
    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    public void createCliente(Context ctx) {
        ClienteRequest request = ctx.bodyAsClass(ClienteRequest.class);
        int userId = ctx.attribute("userId");
        log.info("Create cliente '{}' for user {}", request.nome, userId);
        Cliente cliente = clienteService.createCliente(request.nome, request.email, request.telefone, userId);
        ctx.status(201).json(cliente);
    }

    public void getCliente(Context ctx) {
        int clienteId = Integer.parseInt(ctx.pathParam("clienteId"));
        Cliente cliente = requireOwnedCliente(ctx, clienteId);
        if (cliente == null) {
            return;
        }
        ctx.status(200).json(cliente);
    }

    public void getAllClientes(Context ctx) {
        int userId = ctx.attribute("userId");
        ctx.status(200).json(clienteService.getAllClientesByOwner(userId));
    }

    public void updateCliente(Context ctx) {
        int clienteId = Integer.parseInt(ctx.pathParam("clienteId"));
        if (requireOwnedCliente(ctx, clienteId) == null) {
            return;
        }
        ClienteRequest request = ctx.bodyAsClass(ClienteRequest.class);
        Cliente cliente = clienteService.updateCliente(clienteId, request.nome, request.email, request.telefone);
        ctx.status(200).json(cliente);
    }

    public void deleteCliente(Context ctx) {
        int clienteId = Integer.parseInt(ctx.pathParam("clienteId"));
        if (requireOwnedCliente(ctx, clienteId) == null) {
            return;
        }
        clienteService.deleteCliente(clienteId);
        ctx.status(204);
    }

    /**
     * Fetches the client and verifies it belongs to the authenticated user.
     * Writes the proper 404/403 response and returns null when access is denied.
     */
    private Cliente requireOwnedCliente(Context ctx, int clienteId) {
        int userId = ctx.attribute("userId");
        Cliente cliente = clienteService.getCliente(clienteId);
        if (cliente == null) {
            ctx.status(404).json(new ErrorMessage("Cliente not found"));
            return null;
        }
        if (cliente.getOwnerId() != userId) {
            log.error("User {} is not owner of cliente {}", userId, clienteId);
            ctx.status(403).json(new ErrorMessage("User not owner of cliente"));
            return null;
        }
        return cliente;
    }
}
