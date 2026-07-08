package cncs.academy.ess.model;

public class Cliente {
    /** The id of the client */
    private int id;
    /** The name of the client */
    private String nome;
    /** The email of the client */
    private String email;
    /** The phone number of the client */
    private String telefone;
    /** The id of the user (workshop account) that owns this client */
    private int ownerId;

    public Cliente(int id, String nome, String email, String telefone, int ownerId) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.ownerId = ownerId;
    }

    public Cliente(String nome, String email, String telefone, int ownerId) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.ownerId = ownerId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getOwnerId() {
        return ownerId;
    }
}
