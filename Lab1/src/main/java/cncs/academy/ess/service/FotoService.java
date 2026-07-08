package cncs.academy.ess.service;

import cncs.academy.ess.model.FaseFoto;
import cncs.academy.ess.model.Foto;
import cncs.academy.ess.repository.FotoRepository;
import cncs.academy.ess.repository.ReparoRepository;

import java.util.List;

public class FotoService {
    private final FotoRepository fotoRepository;
    private final ReparoRepository reparoRepository;

    public FotoService(FotoRepository fotoRepository, ReparoRepository reparoRepository) {
        this.fotoRepository = fotoRepository;
        this.reparoRepository = reparoRepository;
    }

    public Foto addFoto(int reparoId, FaseFoto fase, String nomeFicheiro, String contentType,
                        String descricao, byte[] dados) {
        if (reparoRepository.findById(reparoId) == null) {
            throw new IllegalArgumentException("Reparo not found");
        }
        Foto foto = new Foto(reparoId, fase, nomeFicheiro, contentType, descricao, dados);
        int id = fotoRepository.save(foto);
        foto.setId(id);
        return foto;
    }

    public Foto getFoto(int fotoId) {
        return fotoRepository.findById(fotoId);
    }

    public List<Foto> getAllFotosByReparo(int reparoId) {
        return fotoRepository.findAllByReparoId(reparoId);
    }

    public boolean deleteFoto(int fotoId) {
        return fotoRepository.deleteById(fotoId);
    }
}
