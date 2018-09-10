package gruppo_20.iassistant.model;

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

    public Persona(String nome, String cognome, String codiceFiscale, String dataNascita, String luogoNascita,String cittaResidenza, String residenza, String telefono) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.dataNascita = dataNascita;
        this.luogoNascita = luogoNascita;
        this.cittaResidenza = cittaResidenza;
        this.residenza = residenza;
        this.telefono = telefono;
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

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setCodiceFiscale(String codice_fiscale) {
        this.codiceFiscale = codice_fiscale;
    }

    public void setDataNascita(String dataNascita) {
        this.dataNascita = dataNascita;
    }

    public void setLuogoNascita(String luogoNascita){
        this.luogoNascita = luogoNascita;
    }

    public void setCittaResidenza(String cittaResidenza){
        this.cittaResidenza = cittaResidenza;
    }

    public void setResidenza(String residenza) {
        this.residenza = residenza;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
