package gruppo_20.iassistant.model;

import java.util.ArrayList;

public class Visita {
    private String idPaziente;
    private ArrayList <Prestazione> prestazioni;
    private Stato stato;

    public Visita() {
    }

    public Visita(String idPaziente, ArrayList<Prestazione> prestazioni, Stato stato) {
        this.idPaziente = idPaziente;
        this.prestazioni = prestazioni;
        this.stato = stato;
    }

    public String getIdPaziente() {
        return idPaziente;
    }

    public void setPaziente(String idPaziente) {
        this.idPaziente = idPaziente;
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

    public int contaPrestazioniDaSvolgere(){
        int count =0;
        for (Prestazione prestazione : this.prestazioni){
            if(! prestazione.isEffectuated()) {
                count++;
            }
        }
        return count;
    }
}
