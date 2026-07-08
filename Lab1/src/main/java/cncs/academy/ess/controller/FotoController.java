package cncs.academy.ess.controller;

import cncs.academy.ess.controller.messages.ErrorMessage;
import cncs.academy.ess.model.Cliente;
import cncs.academy.ess.model.FaseFoto;
import cncs.academy.ess.model.Foto;
import cncs.academy.ess.model.Prancha;
import cncs.academy.ess.model.Reparo;
import cncs.academy.ess.service.ClienteService;
import cncs.academy.ess.service.FotoService;
import cncs.academy.ess.service.PranchaService;
import cncs.academy.ess.service.ReparoService;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FotoController {
    private static final Logger log = LoggerFactory.getLogger(FotoController.class);

    private final FotoService fotoService;
    private final ReparoService reparoService;
    private final PranchaService pranchaService;
    private final ClienteService clienteService;

    public FotoController(FotoService fotoService,
                          ReparoService reparoService,
                          PranchaService pranchaService,
                          ClienteService clienteService) {
        this.fotoService = fotoService;
        this.reparoService = reparoService;
        this.pranchaService = pranchaService;
        this.clienteService = clienteService;
    }

    /**
     * Uploads one or more photos (multipart/form-data) documenting either the
     * damage ("fase=DANO") or the finished repair ("fase=REPARO").
     * All uploaded files in the request are stored under the given repair.
     */
    public void uploadFotos(Context ctx) {
        int reparoId = Integer.parseInt(ctx.pathParam("reparoId"));
        if (!ownsReparo(ctx, reparoId)) {
            return;
        }

        FaseFoto fase = parseFase(ctx.formParam("fase"));
        if (fase == null) {
            ctx.status(400).json(new ErrorMessage("Invalid 'fase' (expected DANO or REPARO)"));
            return;
        }
        String descricao = ctx.formParam("descricao");

        List<UploadedFile> ficheiros = ctx.uploadedFiles();
        if (ficheiros.isEmpty()) {
            ctx.status(400).json(new ErrorMessage("No photos uploaded"));
            return;
        }

        List<Foto> guardadas = new ArrayList<>();
        for (UploadedFile ficheiro : ficheiros) {
            try {
                byte[] dados = ficheiro.content().readAllBytes();
                Foto foto = fotoService.addFoto(
                        reparoId, fase, ficheiro.filename(), ficheiro.contentType(), descricao, dados);
                guardadas.add(foto);
            } catch (IOException e) {
                log.error("Failed to read uploaded photo '{}'", ficheiro.filename(), e);
                ctx.status(500).json(new ErrorMessage("Failed to read uploaded photo"));
                return;
            }
        }
        log.info("Stored {} photo(s) for reparo {} (fase {})", guardadas.size(), reparoId, fase);
        ctx.status(201).json(guardadas);
    }

    /** Lists the metadata of every photo attached to a repair (bytes not included). */
    public void getFotosByReparo(Context ctx) {
        int reparoId = Integer.parseInt(ctx.pathParam("reparoId"));
        if (!ownsReparo(ctx, reparoId)) {
            return;
        }
        ctx.status(200).json(fotoService.getAllFotosByReparo(reparoId));
    }

    /** Streams the raw image bytes of a single photo. */
    public void getFotoConteudo(Context ctx) {
        int reparoId = Integer.parseInt(ctx.pathParam("reparoId"));
        int fotoId = Integer.parseInt(ctx.pathParam("fotoId"));
        if (!ownsReparo(ctx, reparoId)) {
            return;
        }
        Foto foto = fotoService.getFoto(fotoId);
        if (foto == null || foto.getReparoId() != reparoId) {
            ctx.status(404).json(new ErrorMessage("Foto not found"));
            return;
        }
        if (foto.getContentType() != null) {
            ctx.contentType(foto.getContentType());
        }
        ctx.status(200).result(foto.getDados());
    }

    public void deleteFoto(Context ctx) {
        int reparoId = Integer.parseInt(ctx.pathParam("reparoId"));
        int fotoId = Integer.parseInt(ctx.pathParam("fotoId"));
        if (!ownsReparo(ctx, reparoId)) {
            return;
        }
        Foto foto = fotoService.getFoto(fotoId);
        if (foto == null || foto.getReparoId() != reparoId) {
            ctx.status(404).json(new ErrorMessage("Foto not found"));
            return;
        }
        fotoService.deleteFoto(fotoId);
        ctx.status(204);
    }

    private FaseFoto parseFase(String value) {
        if (value == null) {
            return FaseFoto.DANO; // default: documenting the damage
        }
        try {
            return FaseFoto.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Verifies the repair exists and (through surfboard -> client) belongs to the
     * authenticated user. Writes the proper 404/403 and returns false on denial.
     */
    private boolean ownsReparo(Context ctx, int reparoId) {
        int userId = ctx.attribute("userId");
        Reparo reparo = reparoService.getReparo(reparoId);
        if (reparo == null) {
            ctx.status(404).json(new ErrorMessage("Reparo not found"));
            return false;
        }
        Prancha prancha = pranchaService.getPrancha(reparo.getPranchaId());
        if (prancha == null) {
            ctx.status(404).json(new ErrorMessage("Prancha not found"));
            return false;
        }
        Cliente cliente = clienteService.getCliente(prancha.getClienteId());
        if (cliente == null || cliente.getOwnerId() != userId) {
            log.error("User {} is not owner of reparo {}", userId, reparoId);
            ctx.status(403).json(new ErrorMessage("User not owner of reparo"));
            return false;
        }
        return true;
    }
}
