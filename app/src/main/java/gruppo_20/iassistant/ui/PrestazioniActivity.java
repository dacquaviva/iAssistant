package gruppo_20.iassistant.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import gruppo_20.iassistant.R;

public class PrestazioniActivity extends AppCompatActivity {

    private static FloatingActionButton fab;
    private static RecyclerView prestazioniList;
    private static android.support.design.bottomappbar.BottomAppBar but;

    private String dataVisita;
    private String orarioVisita;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestazioni);
        dataVisita = getIntent().getExtras().getString("dataVisita");
        orarioVisita = getIntent().getExtras().getString("orarioVisita");
        prestazioniList = (RecyclerView) findViewById(R.id.prestazioni_list);

        //chiamata del metodo per il riempimento della lista
        but = (android.support.design.bottomappbar.BottomAppBar) findViewById(R.id.bottom_app_bar);
        assert prestazioniList != null;
        setupRecyclerView(prestazioniList);

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
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) { //@NonNull specifica che il metodo non potrà mai restituire null
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, Prestazioni.ITEMS));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final PrestazioniActivity mParentActivity;
        private final List<Prestazioni> mValues;

        /**
         * COSTRUTTORE
         */
        SimpleItemRecyclerViewAdapter(PrestazioniActivity parent, List<Prestazioni> items) {
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
            holder.mNomePrestazione.setText(mValues.get(position).nomePrestazione);
            holder.mNumPrestazione.setText(mValues.get(position).numeroPrestazione);
            holder.mBluetooth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    apriDialog(holder.blueDialog,"bluetooth",v.getContext(), R.layout.bluetooth);
                }
            });

            holder.mManuale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    apriDialog(holder.inserimentoDialog,"Inserimento Dati",v.getContext(), R.layout.inserimento_dati);
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
            }else{
                MyDialog.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                MyDialog.setNegativeButton("Riesegui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


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
            final FloatingActionButton mBluetooth;
            final FloatingActionButton mManuale;
            AlertDialog.Builder blueDialog ;
            AlertDialog.Builder inserimentoDialog;



            ViewHolder(View view) {
                super(view);
                mNumPrestazione = (Button) view.findViewById(R.id.numeroPrestazioni);
                mNomePrestazione = (TextView) view.findViewById(R.id.nomePrestazione);
                mBluetooth = (FloatingActionButton) view.findViewById(R.id.bluetoothFab);
                mManuale = (FloatingActionButton) view.findViewById(R.id.manualFab);
            }
        }
    }
}

