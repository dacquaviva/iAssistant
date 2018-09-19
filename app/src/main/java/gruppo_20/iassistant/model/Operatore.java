package gruppo_20.iassistant.model;

//Classe che permette di instanziare l'operatore sanitario
public class Operatore extends Persona{
    private String professione;

    public Operatore(){

    }

    public void setProfessione(String professione) {
        this.professione = professione;
    }

    public String getProfessione() {
        return professione;
    }
}
