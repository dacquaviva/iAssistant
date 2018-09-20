package gruppo_20.iassistant.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import gruppo_20.iassistant.R;
import gruppo_20.iassistant.model.CartellaClinica;
import gruppo_20.iassistant.model.Paziente;

/*
Classe che implementa l'activity Anagrafica del Paziente comprendente sia i dati personali sia
la cartella clinica
 */
public class AnagraficaActivity extends AppCompatActivity {

    private String idPaziente;
    //impostiamo i riferimenti al database del ramo pazienti
    private final DatabaseReference dbRefPazienti = FirebaseDatabase.getInstance().getReference().child("pazienti");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anagrafica);
        //il riferimento al paziente viene passato tramite intent lanciato dal bottone di PrestazioniActivity
        idPaziente = getIntent().getExtras().getString("idPaziente");

        final TextView nomePaziente =(TextView)  findViewById(R.id.anagraficaPaziente_nome);
        final TextView cognomePaziente =(TextView)  findViewById(R.id.anagraficaPaziente_cognome);
        final TextView codiceFiscalePaziente =(TextView)  findViewById(R.id.anagraficaPaziente_codiceFiscale);
        final TextView cittaNascitaPaziente = (TextView)  findViewById(R.id.anagraficaPaziente_luogoNascita);
        final TextView dataNascitaPaziente =(TextView)  findViewById(R.id.anagraficaPaziente_dataNascita);
        final TextView indirizzoPaziente =(TextView)  findViewById(R.id.anagraficaPaziente_indirizzo);
        final TextView cittaResidenzaPaziente =(TextView)  findViewById(R.id.anagraficaPaziente_cittaResidenza);
        final TextView telefonoPaziente =(TextView)  findViewById(R.id.anagraficaPaziente_telefono);
        final TextView medicoCurantePaziente=(TextView)  findViewById(R.id.anagraficaPaziente_medicoCurante);

        final TextView inizioRaccoltaDati =(TextView)  findViewById(R.id.anagraficaPaziente_inizioRaccoltaDati);
        final TextView diagnosiPaziente =(TextView)  findViewById(R.id.anagraficaPaziente_Diagnosi);
        final TextView allergiePaziente =(TextView)  findViewById(R.id.anagraficaPaziente_Allergie);
        final TextView malattieConcomitantiPaziente =(TextView)  findViewById(R.id.anagraficaPaziente_MalattieConcomitanti);
        final TextView trattamentoFarmacologicoPaziente =(TextView)  findViewById(R.id.anagraficaPaziente_TrattamentoFarmacologico);




        // Lettura dei dati del paziente dal DB
        dbRefPazienti.child(idPaziente).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Paziente paziente = dataSnapshot.getValue(Paziente.class);
                nomePaziente.setText(paziente.getNome());
                cognomePaziente.setText(paziente.getCognome());
                cittaNascitaPaziente.setText(paziente.getLuogoNascita());
                codiceFiscalePaziente.setText(paziente.getCodiceFiscale());
                dataNascitaPaziente.setText(paziente.getDataNascita());
                indirizzoPaziente.setText(paziente.getResidenza());
                cittaResidenzaPaziente.setText(paziente.getCittaResidenza());
                telefonoPaziente.setText(paziente.getTelefono());
                medicoCurantePaziente.setText(paziente.getMedicoCurante());

                CartellaClinica cartellaClinica = paziente.getCartellaClinica();
                inizioRaccoltaDati.setText(cartellaClinica.getInizioTrattamento());
                diagnosiPaziente.setText(concatenaArrayString(cartellaClinica.getDiagnosi()));
                allergiePaziente.setText(concatenaArrayString(cartellaClinica.getAllergie()));
                malattieConcomitantiPaziente.setText(concatenaArrayString(cartellaClinica.getMalattie()));
                trattamentoFarmacologicoPaziente.setText(concatenaArrayString(cartellaClinica.getTrattFarmacologico()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String concatenaArrayString(ArrayList<String> array){
        String string = array.get(0);
        for (int c = 1; c < array.size() ; c++){
            string = string + "\n" + array.get(c);
        }
        return string;
    }
}
