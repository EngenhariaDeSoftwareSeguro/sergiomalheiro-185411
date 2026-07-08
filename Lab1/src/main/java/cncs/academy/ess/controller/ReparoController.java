package cncs.academy.ess.controller;

import cncs.academy.ess.controller.messages.ErrorMessage;
import cncs.academy.ess.controller.messages.ReparoRequest;
import cncs.academy.ess.model.Cliente;
import cncs.academy.ess.model.Prancha;
import cncs.academy.ess.model.Reparo;
import cncs.academy.ess.service.ClienteService;
import cncs.academy.ess.service.PranchaService;
import cncs.academy.ess.service.ReparoService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReparoController {
    private static final Logger log = LoggerFactory.getLogger(ReparoController.class);
    private final ReparoService reparoService;
    private final PranchaService pranchaService;
    private final ClienteService clienteService;

    public ReparoController(ReparoService reparoService,
                            PranchaService pranchaService,
                            ClienteService clienteService) {
        this.reparoService = reparoService;
        this.pranchaService = pranchaService;
        this.clienteService = clienteService;
    }

    public void createReparo(Context ctx) {
        ReparoRequest request = ctx.bodyAsClass(ReparoRequest.class);
        if (!ownsPrancha(ctx, request.pranchaId)) {
            return;
        }
        double custo = request.custo == null ? 0.0 : request.custo;
        Reparo reparo = reparoService.createReparo(request.descricao, custo, request.pranchaId);
        ctx.status(201).json(reparo);
    }

    public void getReparo(Context ctx) {
        int reparoId = Integer.parseInt(ctx.pathParam("reparoId"));
        Reparo reparo = requireOwnedReparo(ctx, reparoId);
        if (reparo == null) {
            return;
        }
        ctx.status(200).json(reparo);
    }

    public void getAllReparosByPrancha(Context ctx) {
        int pranchaId = Integer.parseInt(ctx.pathParam("pranchaId"));
        if (!ownsPrancha(ctx, pranchaId)) {
            return;
        }
        ctx.status(200).json(reparoService.getAllReparosByPrancha(pranchaId));
    }

    public void updateReparo(Context ctx) {
        int reparoId = Integer.parseInt(ctx.pathParam("reparoId"));
        if (requireOwnedReparo(ctx, reparoId) == null) {
            return;
        }
        ReparoRequest request = ctx.bodyAsClass(ReparoRequest.class);
        Reparo reparo = reparoService.updateReparo(reparoId, request.descricao, request.estado, request.custo);
        ctx.status(200).json(reparo);
    }

    public void deleteReparo(Context ctx) {
        int reparoId = Integer.parseInt(ctx.pathParam("reparoId"));
        if (requireOwnedReparo(ctx, reparoId) == null) {
            return;
        }
        reparoService.deleteReparo(reparoId);
        ctx.status(204);
    }

    /** Verifies the surfboard exists and (through its client) belongs to the user. */
    private boolean ownsPrancha(Context ctx, int pranchaId) {
        int userId = ctx.attribute("userId");
        Prancha prancha = pranchaService.getPrancha(pranchaId);
        if (prancha == null) {
            ctx.status(404).json(new ErrorMessage("Prancha not found"));
            return false;
        }
        Cliente cliente = clienteService.getCliente(prancha.getClienteId());
        if (cliente == null || cliente.getOwnerId() != userId) {
            log.error("User {} is not owner of prancha {}", userId, pranchaId);
            ctx.status(403).json(new ErrorMessage("User not owner of prancha"));
            return false;
        }
        return true;
    }

    /**
     * Fetches the repair and verifies (through surfboard -> client) that it belongs
     * to the authenticated user. Writes the proper 404/403 and returns null on denial.
     */
    private Reparo requireOwnedReparo(Context ctx, int reparoId) {
        Reparo reparo = reparoService.getReparo(reparoId);
        if (reparo == null) {
            ctx.status(404).json(new ErrorMessage("Reparo not found"));
            return null;
        }
        if (!ownsPrancha(ctx, reparo.getPranchaId())) {
            return null;
        }
        return reparo;
    }
}
