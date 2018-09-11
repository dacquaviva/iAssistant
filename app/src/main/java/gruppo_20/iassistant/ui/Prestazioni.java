package gruppo_20.iassistant.ui;

import java.util.ArrayList;
import java.util.List;

class Prestazioni {
        public String nomePrestazione;
        public String numeroPrestazione;
        public boolean isEffettuata;

        public static final Prestazioni[] items = {new Prestazioni("scopare il paziente","1",true                                                                                                                                                                                                                                                                                              ),
                new Prestazioni("Pippo Pippa","2",false)};

        public static final List<Prestazioni> ITEMS = new ArrayList<>();

        static {
            // Add some sample items.
            for (int i = 0; i < items.length; i++) {
                addItem(items[i]);
            }
        }

        private static void addItem(Prestazioni ogg) {
            ITEMS.add(ogg);
        }


        public Prestazioni(String nomePrestazione, String numeroPrestazione, boolean isEffettuata) {
            this.nomePrestazione = nomePrestazione;
            this.numeroPrestazione = numeroPrestazione;
            this.isEffettuata = isEffettuata;
        }
}
