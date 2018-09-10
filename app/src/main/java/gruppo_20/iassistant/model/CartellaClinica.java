package gruppo_20.iassistant.model;

import java.util.ArrayList;

public class CartellaClinica {
    private String inizioTrattamento;
    private ArrayList<String> diagnosi;
    private ArrayList<String> allergie;
    private ArrayList<String> malattie;
    private ArrayList<String> trattFarmacologico;

    public CartellaClinica() {
    }

    public CartellaClinica(String inizioTrattamento, ArrayList<String> diagnosi, ArrayList<String> allergie, ArrayList<String> malattie, ArrayList<String> trattFarmacologico) {
        this.inizioTrattamento = inizioTrattamento;
        this.diagnosi = diagnosi;
        this.allergie = allergie;
        this.malattie = malattie;
        this.trattFarmacologico = trattFarmacologico;
    }

    public String getInizioTrattamento() {
        return inizioTrattamento;
    }

    public void setInizioTrattamento(String inizioTrattamento) {
        this.inizioTrattamento = inizioTrattamento;
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

    public void addAllergie(String allergia) {
        if (this.allergie == null){
            this.allergie = new ArrayList<String>();
        }
        this.allergie.add(allergia);
    }
}
