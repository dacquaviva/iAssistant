package gruppo_20.iassistant.model;

import java.util.ArrayList;

public class Paziente extends Persona {
    private String luogoNascita;
    private String indirizzo;
    private int civico;
    private String medicoCurante;
    private ArrayList<String> note;
    private CartellaClinica cartella;

    public Paziente(String nome, String cognome, String codice_fiscale, String data_nascita, String residenza, String telefono, String luogoNascita, String indirizzo, int civico, String medicoCurante, ArrayList<String> note, CartellaClinica cartella) {
        super(nome, cognome, codice_fiscale, data_nascita, residenza, telefono);
        this.luogoNascita = luogoNascita;
        this.indirizzo = indirizzo;
        this.civico = civico;
        this.medicoCurante = medicoCurante;
        this.note = note;
        this.cartella = cartella;
    }

    public String getLuogoNascita() {
        return luogoNascita;
    }

    public void setLuogoNascita(String luogo_nascita) {
        this.luogoNascita = luogo_nascita;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public int getCivico() {
        return civico;
    }

    public void setCivico(int civico) {
        this.civico = civico;
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

    public CartellaClinica getCartella() {
        return cartella;
    }

    public void setCartella(CartellaClinica cartella) {
        this.cartella = cartella;
    }
}
