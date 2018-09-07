package gruppo_20.iassistant.model;

import java.util.ArrayList;

public class Prestazione {
    private boolean isBlue;
    private String prestazione;
    private ArrayList<String> datiOpzionali;

    public Prestazione(String prestazione) {
        this.isBlue = false;
        this.prestazione = prestazione;
    }

    public Prestazione(boolean isBlue, String prestazione, ArrayList<String> datiOpzionali) {
        this.isBlue = isBlue;
        this.prestazione = prestazione;
        this.datiOpzionali = datiOpzionali;
    }

    public boolean isBlue() {
        return isBlue;
    }

    public void setBlue(boolean blue) {
        isBlue = blue;
    }

    public String getPrestazione() {
        return prestazione;
    }

    public void setPrestazione(String prestazione) {
        this.prestazione = prestazione;
    }

    public ArrayList<String> getDatiOpzionali() {
        return datiOpzionali;
    }

    public void setDatiOpzionali(ArrayList<String> datiOpzionali) {
        this.datiOpzionali = datiOpzionali;
    }
}
