package gruppo_20.iassistant.ui;

import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import gruppo_20.iassistant.R;

public class PrestazioniActivity extends AppCompatActivity {

    private static FloatingActionButton fab;
    private RecyclerView prestazioniList;
    private MaterialCardView prestazione;
    private static android.support.design.bottomappbar.BottomAppBar but;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestazioni);


        prestazioniList = (RecyclerView) findViewById(R.id.prestazioni_list);
        but = (android.support.design.bottomappbar.BottomAppBar) findViewById(R.id.bottom_app_bar);
        assert prestazioniList != null;
        setupRecyclerView(prestazioniList);


    }




        private void setupRecyclerView (@NonNull RecyclerView recyclerView)
        { //@NonNull specifica che il metodo non potrà mai restituire null
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
                    mNumPrestazione = (Button) view.findViewById(R.id.numeroPrestazioni);
                    mNomePrestazione = (TextView) view.findViewById(R.id.nomePrestazione);
                }
            }
        }
    }

