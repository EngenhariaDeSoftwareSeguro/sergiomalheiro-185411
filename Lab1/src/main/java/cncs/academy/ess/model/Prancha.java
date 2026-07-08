package cncs.academy.ess.model;

public class Prancha {
    /** The id of the surfboard */
    private int id;
    /** The brand of the surfboard */
    private String marca;
    /** The model of the surfboard */
    private String modelo;
    /** The type of the surfboard (e.g. shortboard, longboard, fish) */
    private String tipo;
    /** The dimensions of the surfboard (e.g. 6'2" x 19" x 2 1/2") */
    private String dimensoes;
    /** The id of the client that owns this surfboard */
    private int clienteId;

    public Prancha(int id, String marca, String modelo, String tipo, String dimensoes, int clienteId) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.tipo = tipo;
        this.dimensoes = dimensoes;
        this.clienteId = clienteId;
    }

    public Prancha(String marca, String modelo, String tipo, String dimensoes, int clienteId) {
        this.marca = marca;
        this.modelo = modelo;
        this.tipo = tipo;
        this.dimensoes = dimensoes;
        this.clienteId = clienteId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDimensoes() {
        return dimensoes;
    }

    public void setDimensoes(String dimensoes) {
        this.dimensoes = dimensoes;
    }

    public int getClienteId() {
        return clienteId;
    }
}
