package gruppo_20.iassistant.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gruppo_20.iassistant.R;
import gruppo_20.iassistant.model.Operatore;

public class InfoOperatoreActivity extends AppCompatActivity {

    private final String idOperatore = FirebaseAuth.getInstance().getUid();
    private final DatabaseReference dbRefAnagraficaOperatore = FirebaseDatabase.getInstance().getReference().child("operatori").child(idOperatore).child("anagrafica");
    private final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_operatore);

        final TextView cognomeNomeOperatore = (TextView) findViewById(R.id.nome_operatore);
        final TextView emailOperatore = (TextView) findViewById(R.id.email_operatore);
        final TextView professioneOperatore = (TextView) findViewById(R.id.infoOperatore_professione);
        final TextView numTelefonoOperatore = (TextView) findViewById(R.id.infoOperatore_numTelefono);
        final TextView cittaResidenzaOperatore = (TextView) findViewById(R.id.infoOperatore_cittaResidenza);
        final TextView indirizzoOperatore = (TextView) findViewById(R.id.infoOperatore_indirizzo);
        final TextView codiceFiscaleOperatore = (TextView) findViewById(R.id.infoOperatore_codiceFiscale);
        final TextView dataNascitaOperatore = (TextView) findViewById(R.id.infoOperatore_dataNascita);
        final TextView cittaNascitaOperatore = (TextView) findViewById(R.id.infoOperatore_cittaNascita);

        emailOperatore.setText(email);

        dbRefAnagraficaOperatore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Operatore operatore = dataSnapshot.getValue(Operatore.class);

                cognomeNomeOperatore.setText(operatore.getCognome() + " " + operatore.getNome());
                professioneOperatore.setText(operatore.getProfessione());
                numTelefonoOperatore.setText(operatore.getTelefono());
                cittaResidenzaOperatore.setText(operatore.getCittaResidenza());
                indirizzoOperatore.setText(operatore.getResidenza());
                codiceFiscaleOperatore.setText(operatore.getCodiceFiscale());
                dataNascitaOperatore.setText(operatore.getDataNascita());
                cittaNascitaOperatore.setText(operatore.getLuogoNascita());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
