package gruppo_20.iassistant.model;

import java.util.ArrayList;

//Classe che permette di salvare le caratteristiche della cartella clinica del paziente
public class CartellaClinica {
    private String inizioTrattamento;
    private ArrayList<String> diagnosi;
    private ArrayList<String> allergie;
    private ArrayList<String> malattie;
    private ArrayList<String> trattFarmacologico;

    public CartellaClinica() {
    }

    public String getInizioTrattamento() {
        return inizioTrattamento;
    }

    public ArrayList<String> getDiagnosi() {
        return diagnosi;
    }

    public void setDiagnosi(ArrayList<String> diagnosi) {
        this.diagnosi = diagnosi;
    }

    public ArrayList<String> getAllergie() {
        return allergie;
    }

    public void setAllergie(ArrayList<String> allergie) {
        this.allergie = allergie;
    }

    public ArrayList<String> getMalattie() {
        return malattie;
    }

    public ArrayList<String> getTrattFarmacologico() {
        return trattFarmacologico;
    }


}
