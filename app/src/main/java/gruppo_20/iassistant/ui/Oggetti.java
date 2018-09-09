package gruppo_20.iassistant.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vito on 07/09/2018.
 */
class Oggetti {
    public String nomePaziente;
    public String orario;
    public String nPrestazioni;
    public int stato;

    public static final Oggetti[] items = {new Oggetti("Bitonto Ernesto","ore: 10.00", "3 Prestazioni", 1),
                                           new Oggetti("Pippo Pippa","ore: 12.00", "2 Prestazioni", 0),
                                            new Oggetti("Geppetto","ore: 15.00", "1 Prestazione", 2)};

    public static final List<Oggetti> ITEMS = new ArrayList<>();

    static {
        // Add some sample items.
        for (int i = 0; i < items.length; i++) {
            addItem(items[i]);
        }
    }

    private static void addItem(Oggetti ogg) {
        ITEMS.add(ogg);
    }


    public Oggetti(String nomePaziente, String orario, String nPrestazioni, int stato) {
        this.nomePaziente = nomePaziente;
        this.orario = orario;
        this.nPrestazioni = nPrestazioni;
        this.stato = stato;
    }
}

