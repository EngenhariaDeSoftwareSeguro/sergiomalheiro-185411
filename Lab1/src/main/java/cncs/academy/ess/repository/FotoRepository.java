package cncs.academy.ess.repository;

import cncs.academy.ess.model.Foto;

import java.util.List;

public interface FotoRepository {
    Foto findById(int fotoId);
    List<Foto> findAllByReparoId(int reparoId);
    int save(Foto foto);
    boolean deleteById(int fotoId);
}
