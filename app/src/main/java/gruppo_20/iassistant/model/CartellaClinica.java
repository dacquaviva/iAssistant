package gruppo_20.iassistant.model;

import java.util.ArrayList;

public class CartellaClinica {
    private ArrayList<String> periodo;
    private ArrayList<String> diagnosi;
    private ArrayList<String> allergie;
    private ArrayList<String> malattie;
    private ArrayList<String> trattFarmacologico;

    public ArrayList<String> getPeriodo() {
        return periodo;
    }

    public void setPeriodo(ArrayList<String> periodo) {
        this.periodo = periodo;
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

    public void setMalattie(ArrayList<String> malattie) {
        this.malattie = malattie;
    }

    public ArrayList<String> getTrattFarmacologico() {
        return trattFarmacologico;
    }

    public void setTrattFarmacologico(ArrayList<String> trattFarmacologico) {
        this.trattFarmacologico = trattFarmacologico;
    }
}
