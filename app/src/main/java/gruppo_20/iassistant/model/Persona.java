package gruppo_20.iassistant.model;

//classe astratta che permette di ereditare proprietà comuni a Operatore e Paziente
public abstract class Persona {
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String dataNascita;
    private String luogoNascita;
    private String cittaResidenza;
    private String residenza;
    private String telefono;

    public Persona() {

    }
    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public String getLuogoNascita() { return luogoNascita;}

    public String getCittaResidenza() {
        return cittaResidenza;
    }

    public String getResidenza() {
        return residenza;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
