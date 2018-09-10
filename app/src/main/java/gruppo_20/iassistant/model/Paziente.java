package gruppo_20.iassistant.model;

import java.util.ArrayList;

public class Paziente extends Persona {
    private String medicoCurante;
    private ArrayList<String> note;
    private CartellaClinica cartellaClinica;

    public Paziente(){
        super();
    }

    public Paziente(String nome, String cognome, String codiceFiscale, String dataNascita, String luogoNascita, String cittaResidenza, String residenza, String telefono, String medicoCurante, ArrayList<String> note, CartellaClinica cartellaClinica) {
        super(nome, cognome, codiceFiscale, dataNascita, luogoNascita, cittaResidenza, residenza, telefono);
        this.medicoCurante = medicoCurante;
        this.note = note;
        this.cartellaClinica = cartellaClinica;
    }

    public String getMedicoCurante() {
        return medicoCurante;
    }

    public void setMedicoCurante(String medico_curante) {
        this.medicoCurante = medico_curante;
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

    public void setCartellaClinica(CartellaClinica cartellaClinica) {
        this.cartellaClinica = cartellaClinica;
    }
}
