package cncs.academy.ess.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Foto {
    /** The id of the photo */
    private int id;
    /** The id of the repair this photo belongs to */
    private int reparoId;
    /** Whether this photo documents the damage or the finished repair */
    private FaseFoto fase;
    /** The original file name of the uploaded photo */
    private String nomeFicheiro;
    /** The MIME content type of the photo (e.g. image/jpeg) */
    private String contentType;
    /** An optional caption describing the photo */
    private String descricao;
    /** The raw bytes of the image (not serialized to JSON) */
    @JsonIgnore
    private byte[] dados;

    public Foto(int reparoId, FaseFoto fase, String nomeFicheiro, String contentType,
                String descricao, byte[] dados) {
        this.reparoId = reparoId;
        this.fase = fase;
        this.nomeFicheiro = nomeFicheiro;
        this.contentType = contentType;
        this.descricao = descricao;
        this.dados = dados;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getReparoId() {
        return reparoId;
    }

    public FaseFoto getFase() {
        return fase;
    }

    public String getNomeFicheiro() {
        return nomeFicheiro;
    }

    public String getContentType() {
        return contentType;
    }

    public String getDescricao() {
        return descricao;
    }

    /** The size of the stored image in bytes. */
    public int getTamanho() {
        return dados == null ? 0 : dados.length;
    }

    @JsonIgnore
    public byte[] getDados() {
        return dados;
    }
}
