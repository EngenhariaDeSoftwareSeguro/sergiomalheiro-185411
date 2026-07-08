package cncs.academy.ess.controller;

import cncs.academy.ess.controller.messages.ErrorMessage;
import cncs.academy.ess.controller.messages.PranchaRequest;
import cncs.academy.ess.model.Cliente;
import cncs.academy.ess.model.Prancha;
import cncs.academy.ess.service.ClienteService;
import cncs.academy.ess.service.PranchaService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PranchaController {
    private static final Logger log = LoggerFactory.getLogger(PranchaController.class);
    private final PranchaService pranchaService;
    private final ClienteService clienteService;

    public PranchaController(PranchaService pranchaService, ClienteService clienteService) {
        this.pranchaService = pranchaService;
        this.clienteService = clienteService;
    }

    public void createPrancha(Context ctx) {
        PranchaRequest request = ctx.bodyAsClass(PranchaRequest.class);
        if (!ownsCliente(ctx, request.clienteId)) {
            return;
        }
        Prancha prancha = pranchaService.createPrancha(
                request.marca, request.modelo, request.tipo, request.dimensoes, request.clienteId);
        ctx.status(201).json(prancha);
    }

    public void getPrancha(Context ctx) {
        int pranchaId = Integer.parseInt(ctx.pathParam("pranchaId"));
        Prancha prancha = requireOwnedPrancha(ctx, pranchaId);
        if (prancha == null) {
            return;
        }
        ctx.status(200).json(prancha);
    }

    public void getAllPranchasByCliente(Context ctx) {
        int clienteId = Integer.parseInt(ctx.pathParam("clienteId"));
        if (!ownsCliente(ctx, clienteId)) {
            return;
        }
        ctx.status(200).json(pranchaService.getAllPranchasByCliente(clienteId));
    }

    public void updatePrancha(Context ctx) {
        int pranchaId = Integer.parseInt(ctx.pathParam("pranchaId"));
        if (requireOwnedPrancha(ctx, pranchaId) == null) {
            return;
        }
        PranchaRequest request = ctx.bodyAsClass(PranchaRequest.class);
        Prancha prancha = pranchaService.updatePrancha(
                pranchaId, request.marca, request.modelo, request.tipo, request.dimensoes);
        ctx.status(200).json(prancha);
    }

    public void deletePrancha(Context ctx) {
        int pranchaId = Integer.parseInt(ctx.pathParam("pranchaId"));
        if (requireOwnedPrancha(ctx, pranchaId) == null) {
            return;
        }
        pranchaService.deletePrancha(pranchaId);
        ctx.status(204);
    }

    /** Verifies the given client exists and belongs to the authenticated user. */
    private boolean ownsCliente(Context ctx, int clienteId) {
        int userId = ctx.attribute("userId");
        Cliente cliente = clienteService.getCliente(clienteId);
        if (cliente == null) {
            ctx.status(404).json(new ErrorMessage("Cliente not found"));
            return false;
        }
        if (cliente.getOwnerId() != userId) {
            log.error("User {} is not owner of cliente {}", userId, clienteId);
            ctx.status(403).json(new ErrorMessage("User not owner of cliente"));
            return false;
        }
        return true;
    }

    /**
     * Fetches the surfboard and verifies (through its client) that it belongs to
     * the authenticated user. Writes the proper 404/403 and returns null on denial.
     */
    private Prancha requireOwnedPrancha(Context ctx, int pranchaId) {
        Prancha prancha = pranchaService.getPrancha(pranchaId);
        if (prancha == null) {
            ctx.status(404).json(new ErrorMessage("Prancha not found"));
            return null;
        }
        if (!ownsCliente(ctx, prancha.getClienteId())) {
            return null;
        }
        return prancha;
    }
}
