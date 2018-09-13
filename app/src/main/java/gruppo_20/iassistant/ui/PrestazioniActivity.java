package gruppo_20.iassistant.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.chip.Chip;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import gruppo_20.iassistant.R;
import gruppo_20.iassistant.model.Prestazione;
import gruppo_20.iassistant.model.Visita;

public class PrestazioniActivity extends AppCompatActivity {

    private static FloatingActionButton fab;
    private static RecyclerView prestazioniList;
    private static android.support.design.bottomappbar.BottomAppBar but;

    private String dataVisita;
    private String orarioVisita;
    private String cognomeNomePaziente;

    private DatabaseReference dbRefVisita;
    private String idOperatore = FirebaseAuth.getInstance().getUid();;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestazioni);

        dataVisita = getIntent().getExtras().getString("dataVisita");
        orarioVisita = getIntent().getExtras().getString("orarioVisita");
        cognomeNomePaziente = getIntent().getExtras().getString("cognomeNomePaziente");
        dbRefVisita = FirebaseDatabase.getInstance().getReference().child("operatori").child(idOperatore).child("visite").child(dataVisita).child(orarioVisita);
        prestazioniList = (RecyclerView) findViewById(R.id.prestazioni_list);

        //Riempimento Header con i dati del paziente
        TextView nomePaziente = (TextView)  findViewById(R.id.headPrestazioni_nomePazienteTextView);
        nomePaziente.setText(cognomeNomePaziente);
        final TextView numPrestazioniDaSvolgere = (TextView)  findViewById(R.id.headPrestazioni_numPrestazioniTextView);

        //Riempimento reciclerView con i dati delle prestazioni del DB
        dbRefVisita.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Visita visita = dataSnapshot.getValue(Visita.class);
                int prestazioniDaSvolgere = visita.contaPrestazioniDaSvolgere();
                if(prestazioniDaSvolgere == 0){
                    numPrestazioniDaSvolgere.setText(getResources().getString(R.string.nessunaPrestazione) + " da svolgere");
                }else {
                    numPrestazioniDaSvolgere.setText(getResources().getQuantityString(R.plurals.prestazioni, prestazioniDaSvolgere, prestazioniDaSvolgere) + " da svolgere");
                }

                assert prestazioniList != null;
                setupRecyclerView(prestazioniList, visita.getPrestazioni());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        but = (android.support.design.bottomappbar.BottomAppBar) findViewById(R.id.bottom_app_bar);

        // metodo per animare il FAB
        prestazioniList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling up
                    but = (android.support.design.bottomappbar.BottomAppBar) findViewById(R.id.bottom_app_bar);
                    but.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                } else {
                    // Scrolling down
                    but.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                }
            }
        });

        but.inflateMenu(R.menu.prestazioni_menu);

        //gestione del pulsante di back
        but.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                finish();
                return false;
            }
        });
    }

    //definizione del metodo per il riempimento della lista
    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Prestazione> itemPrestazioni) { //@NonNull specifica che il metodo non potrà mai restituire null
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, itemPrestazioni));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final PrestazioniActivity mParentActivity;
        private final List<Prestazione> mValues;

        /**
         * COSTRUTTORE
         */
        SimpleItemRecyclerViewAdapter(PrestazioniActivity parent, List<Prestazione> items) {
            mValues = items;
            mParentActivity = parent;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.prestazione_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, final int position) {
            if(mValues.get(position).isEffectuated()){
                holder.mNumPrestazione.setBackgroundResource(R.drawable.oval_button_green);
            }
            holder.mNomePrestazione.setText(mValues.get(position).getNomePrestazione());
            holder.mNumPrestazione.setText("" + (position + 1));
            holder.mNomePrestazione.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.modalitaInserimentoDialog = new Dialog(v.getContext());
                    holder.modalitaInserimentoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    holder.modalitaInserimentoDialog.setContentView(R.layout.modalita_misurazione);
                    holder.modalitaInserimentoDialog.setTitle("Scelta tipo di misurazione");
                    Chip cBlu = (Chip) holder.modalitaInserimentoDialog.findViewById(R.id.chipBlu);
                    Chip cMan = (Chip) holder.modalitaInserimentoDialog.findViewById(R.id.chipManuale);

                    cBlu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.modalitaInserimentoDialog.cancel();
                            apriDialog(holder.blueDialog,"bluetooth",v.getContext(), R.layout.bluetooth);
                        }
                    });

                    cMan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.modalitaInserimentoDialog.cancel();
                            apriDialog(holder.inserimentoDialog,"Inserimento Dati",v.getContext(), R.layout.inserimento_dati);
                        }
                    });
                    holder.modalitaInserimentoDialog.show();
                }
            });
        }

        public void apriDialog(AlertDialog.Builder MyDialog, String title, Context c, int layout){
            MyDialog = new AlertDialog.Builder(c);
            MyDialog.setTitle(title);
            MyDialog.setView(layout);

            if(R.layout.bluetooth == layout){
                MyDialog.setPositiveButton("Connetti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog d = (Dialog) dialog;
                        // Apri l'altro dialog quando hai ricevuto il dato
                        apriDialog(new AlertDialog.Builder(d.getContext()),"Risultato misurazione",d.getContext(), R.layout.inserimento_dati_bluetooth);
                    }
                });
            }
               if(layout==R.layout.inserimento_dati || layout==R.layout.inserimento_dati_bluetooth) {
                   MyDialog.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                       }
                   });
                   if((R.layout.inserimento_dati == layout)){
                       MyDialog.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                           }
                       });
                   } else {
                       MyDialog.setNegativeButton("Riesegui", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                           }
                       });
                   }
               }
            MyDialog.show();
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final Button mNumPrestazione;
            final TextView mNomePrestazione;
            AlertDialog.Builder blueDialog ;
            AlertDialog.Builder inserimentoDialog;
            Dialog modalitaInserimentoDialog;

            ViewHolder(View view) {
                super(view);
                mNumPrestazione = (Button) view.findViewById(R.id.numeroPrestazione);
                mNomePrestazione = (TextView) view.findViewById(R.id.nomePrestazione);
            }
        }
    }
}

