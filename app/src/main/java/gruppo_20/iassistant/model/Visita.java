package gruppo_20.iassistant.model;

import java.util.ArrayList;

//Classe che permette di implementare la Visita dell'operatore al paziente.
public class Visita {
    private String idPaziente;
    private ArrayList <Prestazione> prestazioni;
    private Stato stato;
    private String orario;
    private String data;

    public Visita() {
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOrario() {
        return orario;
    }

    public void setOrario(String orario) {
        this.orario = orario;
    }


    public String getIdPaziente() {
        return idPaziente;
    }

    public ArrayList<Prestazione> getPrestazioni() {
        return prestazioni;
    }

    public Stato getStato() {
        return stato;
    }

    public void setStato(Stato stato) {
        this.stato = stato;
    }

    //Conta il numero di prestazioni da svolgere ancora all'interno della visita
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
