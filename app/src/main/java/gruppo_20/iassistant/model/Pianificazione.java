package gruppo_20.iassistant.model;

import java.util.ArrayList;

public class Pianificazione {
    private Paziente paziente;
    private Operatore operatore;
    private String data;
    private ArrayList <Prestazione> prestazioni;
    private Stato stato;

    public Pianificazione(Paziente paziente, Operatore operatore, String data, ArrayList<Prestazione> prestazioni, Stato stato) {
        this.paziente = paziente;
        this.operatore = operatore;
        this.data = data;
        this.prestazioni = prestazioni;
        this.stato = stato;
    }

    public Paziente getPaziente() {
        return paziente;
    }

    public void setPaziente(Paziente paziente) {
        this.paziente = paziente;
    }

    public Operatore getOperatore() {
        return operatore;
    }

    public void setOperatore(Operatore operatore) {
        this.operatore = operatore;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ArrayList<Prestazione> getPrestazioni() {
        return prestazioni;
    }

    public void setPrestazioni(ArrayList<Prestazione> prestazioni) {
        this.prestazioni = prestazioni;
    }

    public Stato getStato() {
        return stato;
    }

    public void setStato(Stato stato) {
        this.stato = stato;
    }
}
