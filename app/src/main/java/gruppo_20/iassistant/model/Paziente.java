package gruppo_20.iassistant.model;

import java.util.ArrayList;

//classe che permette di implementare il paziente
public class Paziente extends Persona {
    private String medicoCurante;
    private ArrayList<String> note;
    private CartellaClinica cartellaClinica;

    public Paziente(){
        super();
    }

    public String getMedicoCurante() {
        return medicoCurante;
    }

    public ArrayList<String> getNote() {
        return note;
    }

    public void setNote(ArrayList<String> note) {
        this.note = note;
    }

    public CartellaClinica getCartellaClinica() {
        return cartellaClinica;
    }

}
