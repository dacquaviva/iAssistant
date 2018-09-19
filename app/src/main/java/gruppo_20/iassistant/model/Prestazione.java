package gruppo_20.iassistant.model;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Map;
//Classe che permette di implementare le singole prestazioni da far effettuare all'operatore sul
//paziente
public class Prestazione {
    private String nomePrestazione;
    private ArrayList<Entry> risultato;
    private String motivazione;
    private String datiOpzionali;

    public Prestazione() {
    }

    public Prestazione(String nomePrestazione){
        this.nomePrestazione = nomePrestazione;
    }

    //Getter
    public String getNomePrestazione() {
        return nomePrestazione;
    }

    public ArrayList<Entry> getRisultato() {
        return risultato;
    }

    public String getMotivazione() {
        return motivazione;
    }

    public String getDatiOpzionali() {
        return datiOpzionali;
    }

    //Setter
    public void setNomePrestazione(String nomePrestazione) {
        this.nomePrestazione = nomePrestazione;
    }

    public void setRisultato(ArrayList<Entry> risultato) {
        this.risultato = risultato;
    }

    //controllo se la prenotazione ha un risultato. True se possiete un risultato
    public boolean isEffectuated(){
        if (this.risultato == null || this.risultato.size()==0){
            return false;
        } else return true;
    }
}
