package gruppo_20.iassistant.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gruppo_20.iassistant.R;
import gruppo_20.iassistant.model.Operatore;
import gruppo_20.iassistant.model.Paziente;

import gruppo_20.iassistant.model.Visita;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {


    private final Calendar calendarIstanceOfToday = Calendar.getInstance();;


    // Variabili per Firebase
    private final String idOperatore = FirebaseAuth.getInstance().getUid();
    private String eMailOperatore = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private final DatabaseReference dbRefOperatore = FirebaseDatabase.getInstance().getReference().child("operatori").child(idOperatore);
    private final DatabaseReference dbRefVisite = dbRefOperatore.child("visite");
    private static DatabaseReference dbRefPazienti = FirebaseDatabase.getInstance().getReference().child("pazienti");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Inizializzata Navigation View
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerNavigationView = navigationView.getHeaderView(0);
        final TextView eMailOperatoreTextView = (TextView) headerNavigationView.findViewById(R.id.nav_eMailTextView);
        eMailOperatoreTextView.setText(eMailOperatore);

        final TextView cognomeNomeOperatoreTextView = (TextView) headerNavigationView.findViewById(R.id.nav_cognomeNomeOperatoreTextView);
        dbRefOperatore.child("anagrafica").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Operatore operatore = dataSnapshot.getValue(Operatore.class);
                cognomeNomeOperatoreTextView.setText(operatore.getCognome() + " " + operatore.getNome());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Gestione floating button del calendario
        FloatingActionButton calendarioButton = (FloatingActionButton)  findViewById(R.id.calendarioActionButton);
        calendarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dataDialog = DatePickerDialog.newInstance( MainActivity.this,
                        calendarIstanceOfToday.get(Calendar.YEAR),
                        calendarIstanceOfToday.get(Calendar.MONTH),
                        calendarIstanceOfToday.get(Calendar.DAY_OF_MONTH));
                dataDialog.setVersion(DatePickerDialog.Version.VERSION_1);
                dataDialog.setAccentColor(getResources().getColor(R.color.colorPrimary));
                dataDialog.setCancelColor(getResources().getColor(R.color.colorMappButton));
                dataDialog.setOkText(getResources().getString(R.string.visualizza));
                dataDialog.setOkColor(getResources().getColor(R.color.colorCallButton));
                dataDialog.show(getFragmentManager(),"datepickerdialog");
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        //inizializzato calendario alla data odierna
        Calendar calendar = Calendar.getInstance();
        String data = setCalendarToString(calendarIstanceOfToday);

        aggiornaVisiteSingolaGiornata(data);

    }

    //Riempimento dati della lista delle pianificazioni
    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Visita> visite, List<String> orari, String dataToString) { //@NonNull specifica che il metodo non potrà mai restituire null
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, visite, orari, dataToString));
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        aggiornaVisiteSingolaGiornata(setCalendarToString(calendar));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final MainActivity mParentActivity;
        private final List<Visita> mValuesViste;
        private final List<String> mValuesOrari;
        private final String mStringData;


        /**
         * COSTRUTTORE
         */
        SimpleItemRecyclerViewAdapter(MainActivity parent, List<Visita> itemsVisite, List<String> itemsOrari, String stringData) {
            mValuesViste = itemsVisite;
            mValuesOrari = itemsOrari ;
            mParentActivity = parent;
            mStringData = stringData;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            //Lettura dei dati del paziente dal DB
            dbRefPazienti.child(mValuesViste.get(position).getIdPaziente()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Paziente paziente = dataSnapshot.getValue(Paziente.class);
                    holder.mNomePaziente.setText(paziente.getCognome() + " " + paziente.getNome());
                    holder.mTelefono.setText(paziente.getTelefono());
                    holder.mIndirizzo.setText(paziente.getResidenza());
                    holder.mNomePaziente.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(view.getContext(), PrestazioniActivity.class);
                            intent.putExtra("cognomeNomePaziente",paziente.getCognome() + " " + paziente.getNome());
                            intent.putExtra("idPaziente",mValuesViste.get(position).getIdPaziente());
                            intent.putExtra("dataVisita", mStringData);
                            intent.putExtra("orarioVisita",mValuesOrari.get(position));
                            view.getContext().startActivity(intent);
                        }
                    });

                    holder.mMapp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String strUri = "geo:0,0?q=" + paziente.getResidenza() + " " + paziente.getCittaResidenza();
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            view.getContext().startActivity(intent);
                        }
                    });

                    holder.mCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + paziente.getTelefono()));
                            view.getContext().startActivity(intent);
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //settaggio dati sullo stato della visita
            switch (mValuesViste.get(position).getStato()) {
                case Pianificato:
                    holder.mStato.setText("Pianificata");
                    holder.mStatoImage.setImageResource(R.drawable.ic_book_black_24dp);
                    break;
                case InCorso:
                    holder.mStato.setText("In Corso");
                    holder.mStatoImage.setImageResource(R.drawable.ic_more_horiz_black_24dp);
                    break;
                case Terminato:
                    holder.mStato.setText("Effettuata");
                    holder.mStatoImage.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
                    break;
            }
            holder.mOrario.setText(mValuesOrari.get(position));
            int size = mValuesViste.get(position).getPrestazioni().size();
            String pluralsPrestazioni;
            if(size == 0){
                pluralsPrestazioni = mParentActivity.getResources().getString(R.string.nessunaPrestazione);
            } else{
                pluralsPrestazioni = mParentActivity.getResources().getQuantityString(R.plurals.prestazioni,size,size);
            }
            holder.mNPrestazioni.setText(pluralsPrestazioni);

            holder.mFreccia.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //metodo per chiudere tutte le pianificazioni aperte quando si scrolla verso il basso
                    ridimensionaPianificazioni(position , mParentActivity);

                    if (!holder.expand) {
                        holder.mFreccia.animate().rotation(holder.mFreccia.getRotation() + 180);
                        holder.mNPrestazioni.setVisibility(View.VISIBLE);
                        holder.mCall.setVisibility(View.VISIBLE);
                        holder.mMapp.setVisibility(View.VISIBLE);
                        holder.mIndirizzo.setVisibility(View.VISIBLE);
                        holder.mTelefono.setVisibility(View.VISIBLE);
                        holder.expand = true;
                    } else {
                        holder.mNPrestazioni.setVisibility(View.GONE);
                        holder.mCall.setVisibility(View.GONE);
                        holder.mMapp.setVisibility(View.GONE);
                        holder.mIndirizzo.setVisibility(View.GONE);
                        holder.mTelefono.setVisibility(View.GONE);
                        holder.mFreccia.animate().rotation(holder.mFreccia.getRotation() + 180);
                        holder.expand = false;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValuesViste.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            boolean expand = false;
            final TextView mNomePaziente;
            final TextView mStato;
            final ImageView mStatoImage;
            final TextView mOrario;

            final TextView mNPrestazioni;
            final TextView mIndirizzo;
            final TextView mTelefono;
            final FloatingActionButton mMapp;
            final FloatingActionButton mCall;
            final ImageButton mFreccia;

            final RelativeLayout mLayout;

            ViewHolder(View view) {
                super(view);
                mNomePaziente = (TextView) view.findViewById(R.id.nomePaziente);
                mStato = (TextView) view.findViewById(R.id.stato);
                mStatoImage = (ImageView) view.findViewById(R.id.statoImage);
                mNPrestazioni = (TextView) view.findViewById(R.id.numPrestazioni);
                mOrario = (TextView) view.findViewById(R.id.orario);
                mIndirizzo = (TextView) view.findViewById(R.id.indirizzo);
                mTelefono = (TextView) view.findViewById(R.id.numero);
                mMapp = (FloatingActionButton) view.findViewById(R.id.mappButton);
                mCall = (FloatingActionButton) view.findViewById(R.id.callButton);
                mFreccia = (ImageButton) view.findViewById(R.id.freccia);
                mLayout = (RelativeLayout) view.findViewById(R.id.layout);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_declaresUnavailability) {
            // Handle the camera action
        } else if (id == R.id.nav_pianifications) {

        } else if (id == R.id.nav_messages) {

        } else if (id == R.id.nav_helpline) {
            FirebaseDatabase.getInstance().getReference().child("helpline").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String numHelpline = dataSnapshot.getValue(String.class);
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + numHelpline));
                    startActivity(intent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else if (id == R.id.nav_myAccount) {

        } else if (id == R.id.nav_associatedDevices) {

        } else if (id == R.id.nav_logOut) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private static void ridimensionaPianificazioni (int position, MainActivity parent) {
        RecyclerView recyclerView = (RecyclerView) parent.findViewById(R.id.item_list);
        int size = recyclerView.getAdapter().getItemCount();
        for (int i = 0; i < size; i++) {
            SimpleItemRecyclerViewAdapter.ViewHolder specificHolder = ( SimpleItemRecyclerViewAdapter.ViewHolder)recyclerView.findViewHolderForAdapterPosition(i);
            if (i != position && specificHolder.expand) {
                specificHolder.mNPrestazioni.setVisibility(View.GONE);
                specificHolder.mCall.setVisibility(View.GONE);
                specificHolder.mMapp.setVisibility(View.GONE);
                specificHolder.mIndirizzo.setVisibility(View.GONE);
                specificHolder.mTelefono.setVisibility(View.GONE);
                specificHolder.mFreccia.animate().rotation(specificHolder.mFreccia.getRotation() + 180);
                specificHolder.expand = false;
            }
        }
    }

    private String setCalendarToString(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        SimpleDateFormat simpleData = new SimpleDateFormat("dd-MM-yyyy");
        calendar.set(year, month , day);
        return simpleData.format(calendar.getTime());
    }

    //
    private void aggiornaVisiteSingolaGiornata(final String dataVisita){
        dbRefVisite.child(dataVisita).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> orariList = new ArrayList<String>();
                List<Visita> visiteList = new ArrayList<Visita>();
                List<Paziente> pazientiList = new ArrayList<Paziente>();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    orariList.add(data.getKey());
                    Visita visita = data.getValue(Visita.class);
                    visiteList.add(visita);
                }

                final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.item_list);
                assert recyclerView != null;
                setupRecyclerView(recyclerView, visiteList, orariList, dataVisita);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
