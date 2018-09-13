package gruppo_20.iassistant.model;

import java.util.ArrayList;

public class Prestazione {
    private String nomePrestazione;
    private String risultato;
    private String motivazione;
    private String datiOpzionali;

    public Prestazione() {
    }

    public Prestazione(String nomePrestazione, String risultato, String motivazione, String datiOpzionali) {
        this.nomePrestazione = nomePrestazione;
        this.risultato = risultato;
        this.motivazione = motivazione;
        this.datiOpzionali = datiOpzionali;
    }

    public Prestazione(String nomePrestazione){
        this.nomePrestazione = nomePrestazione;
    }

    //Getter
    public String getNomePrestazione() {
        return nomePrestazione;
    }

    public String getRisultato() {
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

    public void setRisultato(String risultato) {
        this.risultato = risultato;
    }

    public void setMotivazione(String motivazione) {
        this.motivazione = motivazione;
    }

    public void setDatiOpzionali(String datiOpzionali) {
        this.datiOpzionali = datiOpzionali;
    }

    public boolean isEffectuated(){
        if (this.risultato != null){
            return true;
        } else return false;
    }
}
