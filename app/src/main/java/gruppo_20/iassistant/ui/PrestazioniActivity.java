package gruppo_20.iassistant.ui;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.chip.Chip;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

    private static Chip cBlu;
    private static Chip cMan;
    private static Dialog blueDialogList;
    private static Dialog inserimentoDialog;
    private static Dialog modalitaInserimentoDialog;
    private static ListView listaDispositivi;
    private static Dialog inserimentoBlueBialog;

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
            holder.mNomePrestazione.setText(mValues.get(position).getNomePrestazione());

            if(mValues.get(position).isEffectuated()){
                holder.mNumPrestazione.setBackgroundResource(R.drawable.oval_button_green);
            }
            holder.mNumPrestazione.setText("" + (position + 1));

            holder.mNomePrestazione.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modalitaInserimentoDialog = new Dialog(v.getContext());
                    modalitaInserimentoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    modalitaInserimentoDialog.setContentView(R.layout.modalita_misurazione);
                    modalitaInserimentoDialog.setTitle("Scelta tipo di misurazione");

                    cBlu = (Chip) modalitaInserimentoDialog.findViewById(R.id.chipBlu);
                    cMan = (Chip) modalitaInserimentoDialog.findViewById(R.id.chipManuale);

                    cBlu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            modalitaInserimentoDialog.cancel();
                            blueDialogList = new Dialog(v.getContext());
                            blueDialogList.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            blueDialogList.setTitle("Bluetooth");
                            blueDialogList.setContentView(R.layout.bluetooth);
                            listaDispositivi = (ListView) blueDialogList.findViewById(R.id.lista_dispositivi);
                            String[] dati = {"uno","due"};
                            ArrayAdapter<String> ciao = new ArrayAdapter<String>(v.getContext(),android.R.layout.simple_list_item_1,dati);
                            listaDispositivi.setAdapter(ciao);
                            listaDispositivi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    blueDialogList.cancel();
                                    inserimentoBlueBialog = new Dialog(view.getContext());
                                    inserimentoBlueBialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    inserimentoBlueBialog.setTitle("Bluetooth");
                                    inserimentoBlueBialog.setContentView(R.layout.inserimento_dati_bluetooth);

                                    Button conferma = (Button) inserimentoBlueBialog.findViewById(R.id.button_conferma_blu);
                                    Button riesegui = (Button) inserimentoBlueBialog.findViewById(R.id.button_riesegui);

                                    conferma.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //gestire conferma dati inseriti dall'operatore
                                        }
                                    });
                                    riesegui.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //gestire annulla
                                        }
                                    });
                                    inserimentoBlueBialog.show();
                                }
                            });

                            blueDialogList.show();
                        }
                    });

                    cMan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            modalitaInserimentoDialog.cancel();
                            inserimentoDialog = new Dialog(v.getContext());
                            inserimentoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            inserimentoDialog.setTitle("Inserisci Risultato");
                            inserimentoDialog.setContentView(R.layout.inserimento_dati);

                            Button conferma = (Button) inserimentoDialog.findViewById(R.id.conferma_button_manual);
                            Button annulla = (Button) inserimentoDialog.findViewById(R.id.button_annulla);

                            conferma.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //gestire conferma dati inseriti dall'operatore
                                }
                            });
                            annulla.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //gestire annulla
                                }
                            });
                            inserimentoDialog.show();
                        }
                    });
                    modalitaInserimentoDialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            final Button mNumPrestazione;
            final TextView mNomePrestazione;

            ViewHolder(View view) {
                super(view);
                mNumPrestazione = (Button) view.findViewById(R.id.numeroPrestazione);
                mNomePrestazione = (TextView) view.findViewById(R.id.nomePrestazione);
            }
        }
    }
}

