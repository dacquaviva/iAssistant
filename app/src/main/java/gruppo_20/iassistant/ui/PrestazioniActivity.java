package gruppo_20.iassistant.ui;

import android.app.Dialog;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestazioni);

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
                startActivity(new Intent(PrestazioniActivity.this, MainActivity.class));
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
                    apriDialog(holder.blueDialog,"bluetooth",v, R.layout.bluetooth);
                }
            });

        }

        public void apriDialog(Dialog MyDialog, String title,View v, int layout){
            MyDialog = new Dialog(v.getContext());
            MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            MyDialog.setContentView(layout);
            MyDialog.setTitle(title);

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
            Dialog blueDialog;
            Dialog manualDialog;


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

