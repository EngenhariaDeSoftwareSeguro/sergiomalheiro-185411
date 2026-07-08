package cncs.academy.ess.model;

public class Reparo {
    /** The id of the repair */
    private int id;
    /** A description of the damage / work to be done */
    private String descricao;
    /** The current state of the repair */
    private EstadoReparo estado;
    /** The estimated / final cost of the repair */
    private double custo;
    /** The id of the surfboard being repaired */
    private int pranchaId;

    public Reparo(int id, String descricao, EstadoReparo estado, double custo, int pranchaId) {
        this.id = id;
        this.descricao = descricao;
        this.estado = estado;
        this.custo = custo;
        this.pranchaId = pranchaId;
    }

    public Reparo(String descricao, double custo, int pranchaId) {
        this.descricao = descricao;
        this.estado = EstadoReparo.PENDENTE;
        this.custo = custo;
        this.pranchaId = pranchaId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public EstadoReparo getEstado() {
        return estado;
    }

    public void setEstado(EstadoReparo estado) {
        this.estado = estado;
    }

    public double getCusto() {
        return custo;
    }

    public void setCusto(double custo) {
        this.custo = custo;
    }

    public int getPranchaId() {
        return pranchaId;
    }
}
