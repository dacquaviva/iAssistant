package gruppo_20.iassistant.model;

public class Operatore extends Persona{
    private String professione;

    public Operatore(String nome, String cognome, String codice_fiscale, String data_nascita, String residenza, String telefono, String professione) {
        super(nome, cognome, codice_fiscale, data_nascita, residenza, telefono);
        this.professione = professione;
    }

    public void setProfessione(String professione) {
        this.professione = professione;
    }

    public String getProfessione() {

        return professione;
    }
}
