package cncs.academy.ess;

import cncs.academy.ess.controller.AuthorizationMiddleware;
import cncs.academy.ess.controller.ClienteController;
import cncs.academy.ess.controller.FotoController;
import cncs.academy.ess.controller.PranchaController;
import cncs.academy.ess.controller.ReparoController;
import cncs.academy.ess.controller.UserController;
import cncs.academy.ess.model.Cliente;
import cncs.academy.ess.model.Prancha;
import cncs.academy.ess.repository.memory.InMemoryClienteRepository;
import cncs.academy.ess.repository.memory.InMemoryFotoRepository;
import cncs.academy.ess.repository.memory.InMemoryPranchaRepository;
import cncs.academy.ess.repository.memory.InMemoryReparoRepository;
import cncs.academy.ess.repository.memory.InMemoryUserRepository;
import cncs.academy.ess.service.ClienteService;
import cncs.academy.ess.service.FotoService;
import cncs.academy.ess.service.PranchaService;
import cncs.academy.ess.service.ReparoService;
import cncs.academy.ess.service.UserService;
import io.javalin.Javalin;

import java.security.NoSuchAlgorithmException;

/**
 * Surfboard repair management API.
 *
 * Domain model: a User (workshop account) owns many Clientes; a Cliente owns
 * many Pranchas (surfboards); a Prancha has many Reparos (repairs); and each
 * Reparo can have many Fotos documenting the damage (DANO) and the finished
 * work (REPARO).
 */
public class App {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
        }).start(7100);

        // Repositories (in-memory)
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        InMemoryClienteRepository clienteRepository = new InMemoryClienteRepository();
        InMemoryPranchaRepository pranchaRepository = new InMemoryPranchaRepository();
        InMemoryReparoRepository reparoRepository = new InMemoryReparoRepository();
        InMemoryFotoRepository fotoRepository = new InMemoryFotoRepository();

        // Services
        UserService userService = new UserService(userRepository);
        ClienteService clienteService = new ClienteService(clienteRepository);
        PranchaService pranchaService = new PranchaService(pranchaRepository, clienteRepository);
        ReparoService reparoService = new ReparoService(reparoRepository, pranchaRepository);
        FotoService fotoService = new FotoService(fotoRepository, reparoRepository);

        // Controllers
        UserController userController = new UserController(userService);
        ClienteController clienteController = new ClienteController(clienteService);
        PranchaController pranchaController = new PranchaController(pranchaService, clienteService);
        ReparoController reparoController = new ReparoController(reparoService, pranchaService, clienteService);
        FotoController fotoController =
                new FotoController(fotoService, reparoService, pranchaService, clienteService);

        AuthorizationMiddleware authMiddleware = new AuthorizationMiddleware(userRepository);

        // CORS
        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "*");
        });
        // Authorization middleware
        app.before(authMiddleware::handle);

        // ---- User management ----
        app.post("/user", userController::createUser);
        app.get("/user/{userId}", userController::getUser);
        app.delete("/user/{userId}", userController::deleteUser);
        app.post("/login", userController::loginUser);

        // ---- Clientes ----
        /* POST /cliente { "nome": "...", "email": "...", "telefone": "..." } */
        app.post("/cliente", clienteController::createCliente);
        app.get("/cliente", clienteController::getAllClientes);
        app.get("/cliente/{clienteId}", clienteController::getCliente);
        app.put("/cliente/{clienteId}", clienteController::updateCliente);
        app.delete("/cliente/{clienteId}", clienteController::deleteCliente);

        // ---- Pranchas (surfboards) ----
        /* POST /prancha { "marca":"...", "modelo":"...", "tipo":"...", "dimensoes":"...", "clienteId":1 } */
        app.post("/prancha", pranchaController::createPrancha);
        app.get("/cliente/{clienteId}/pranchas", pranchaController::getAllPranchasByCliente);
        app.get("/prancha/{pranchaId}", pranchaController::getPrancha);
        app.put("/prancha/{pranchaId}", pranchaController::updatePrancha);
        app.delete("/prancha/{pranchaId}", pranchaController::deletePrancha);

        // ---- Reparos (repairs) ----
        /* POST /reparo { "descricao":"...", "custo":120.0, "pranchaId":1 } */
        app.post("/reparo", reparoController::createReparo);
        app.get("/prancha/{pranchaId}/reparos", reparoController::getAllReparosByPrancha);
        app.get("/reparo/{reparoId}", reparoController::getReparo);
        /* PUT /reparo/{id} { "estado":"EM_CURSO", "custo":150.0 } */
        app.put("/reparo/{reparoId}", reparoController::updateReparo);
        app.delete("/reparo/{reparoId}", reparoController::deleteReparo);

        // ---- Fotos (damage / repair pictures) ----
        /* POST /reparo/{reparoId}/fotos  (multipart/form-data)
           fields: fase=DANO|REPARO, descricao=..., one or more files */
        app.post("/reparo/{reparoId}/fotos", fotoController::uploadFotos);
        app.get("/reparo/{reparoId}/fotos", fotoController::getFotosByReparo);
        app.get("/reparo/{reparoId}/fotos/{fotoId}", fotoController::getFotoConteudo);
        app.delete("/reparo/{reparoId}/fotos/{fotoId}", fotoController::deleteFoto);

        fillDummyData(userService, clienteService, pranchaService, reparoService);
    }

    private static void fillDummyData(
            UserService userService,
            ClienteService clienteService,
            PranchaService pranchaService,
            ReparoService reparoService) throws NoSuchAlgorithmException {
        userService.addUser("oficina1", "password1");
        userService.addUser("oficina2", "password2");

        Cliente joao = clienteService.createCliente("João Silva", "joao@example.com", "912345678", 1);
        Cliente maria = clienteService.createCliente("Maria Costa", "maria@example.com", "934567890", 1);

        Prancha p1 = pranchaService.createPrancha(
                "Al Merrick", "Fever", "shortboard", "6'0\" x 18.75\" x 2.31\"", joao.getId());
        Prancha p2 = pranchaService.createPrancha(
                "Firewire", "Seaside", "fish", "5'6\" x 20.5\" x 2.5\"", joao.getId());
        Prancha p3 = pranchaService.createPrancha(
                "Torq", "Longboard", "longboard", "9'0\" x 22.75\" x 3\"", maria.getId());

        reparoService.createReparo("Ding no nose, entrada de água", 45.0, p1.getId());
        reparoService.createReparo("Quilha central partida", 60.0, p1.getId());
        reparoService.createReparo("Fissura na rail direita", 35.0, p2.getId());
        reparoService.createReparo("Repintura e restauro do deck", 120.0, p3.getId());
    }
}
