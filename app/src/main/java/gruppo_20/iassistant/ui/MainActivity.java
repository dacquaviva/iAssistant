package gruppo_20.iassistant.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gruppo_20.iassistant.R;
import gruppo_20.iassistant.model.Operatore;
import gruppo_20.iassistant.model.Paziente;

import gruppo_20.iassistant.model.Visita;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,DatePickerDialog.OnDateSetListener {


    // Variabili per Firebase
    private final String idOperatore = FirebaseAuth.getInstance().getUid();
    private String eMailOperatore = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private final DatabaseReference dbRefOperatore = FirebaseDatabase.getInstance().getReference().child("operatori").child(idOperatore);
    private final DatabaseReference dbRefVisite = dbRefOperatore.child("visite");
    private static DatabaseReference dbRefPazienti = FirebaseDatabase.getInstance().getReference().child("pazienti");

    private ComplexRecyclerViewAdapter recycleViewLista;
    private String dataSelezionata;
    RecyclerView recyclerView;

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

        navigationView.setNavigationItemSelectedListener(this);

        //caricamento visite successive alla data odierna
        List<Object> lista = new ArrayList<>();
        dataSelezionata = setCalendarToString(Calendar.getInstance());
        caricaProssimiTreGiorni(lista, dataSelezionata,4,7);

    }


    //Riempimento dati della lista delle pianificazioni
    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Object> lista) { //@NonNull specifica che il metodo non potrà mai restituire null
        recycleViewLista = new ComplexRecyclerViewAdapter(lista);
        recyclerView.setAdapter(recycleViewLista);
    }

    @Override
   public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        List<Object> lista = new ArrayList<>();
        dataSelezionata = setCalendarToString(calendar);
        caricaProssimiTreGiorni(lista, dataSelezionata,4,7);

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

    // Gestione dell' apertura del data picker al click sull'icona nell'action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Calendar calendarIstanceOfToday = Calendar.getInstance();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.menu_item_data_picker) {
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
            startActivity(new Intent(MainActivity.this, InfoOperatoreActivity.class));
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
            RecyclerView.ViewHolder specificHolder = ( RecyclerView.ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (specificHolder instanceof VisiteHolder) {
                if (i != position && ((VisiteHolder)specificHolder).isExpand()) {
                    ((VisiteHolder)specificHolder).getmNPrestazioni().setVisibility(View.GONE);
                    ((VisiteHolder)specificHolder).getmCall().setVisibility(View.GONE);
                    ((VisiteHolder)specificHolder).getmMapp().setVisibility(View.GONE);
                    ((VisiteHolder)specificHolder).getmIndirizzo().setVisibility(View.GONE);
                    ((VisiteHolder)specificHolder).getmTelefono().setVisibility(View.GONE);
                    ((VisiteHolder)specificHolder).getmFreccia().animate().rotation(((VisiteHolder)specificHolder).getmFreccia().getRotation() + 180);
                    ((VisiteHolder)specificHolder).setExpand(false);
                }
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


    private void caricaProssimiTreGiorni(final List<Object> lista, final String dataVisita, final int numGiorni,final int numMaxGiorni){

        dbRefVisite.child(dataVisita).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Object> listaRiempita =  lista;
                String nextDate=dataVisita;
                int giorniLettiPieni = numGiorni;
                int giorniLettiVuoti = numMaxGiorni;
                if(giorniLettiPieni > 0 && giorniLettiVuoti > 0) {


                    if (dataSnapshot.getValue() == null) {
                        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        final Date date;

                        try {
                            date = format.parse(dataVisita);
                            final Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                            nextDate = format.format(calendar.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        giorniLettiVuoti--;
                        caricaProssimiTreGiorni(listaRiempita,nextDate, giorniLettiPieni,giorniLettiVuoti);

                    } else {
                        listaRiempita.add(dataVisita);
                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            Visita visita = data.getValue(Visita.class);
                            visita.setData(dataVisita);
                            visita.setOrario(data.getKey());
                            listaRiempita.add(visita);
                        }
                        giorniLettiPieni--;
                        giorniLettiVuoti--;
                        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        final Date date;

                        try {
                            date = format.parse(dataVisita);
                            final Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                            nextDate = format.format(calendar.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        caricaProssimiTreGiorni(listaRiempita,nextDate, giorniLettiPieni,giorniLettiVuoti);
                    }
                }else{
                    recyclerView = (RecyclerView) findViewById(R.id.item_list);
                    assert recyclerView != null;
                    setupRecyclerView(recyclerView, lista);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class ComplexRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Object> items;
        private String ultimaData; //salva un riferimento all'ultima data prelevata dalla Lista

        public ComplexRecyclerViewAdapter(List<Object> items) {
            this.items = items;
        }

        private void azzeraRecycleView(){
            this.items = null;
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            if (holder instanceof VisiteHolder) {
                if (((VisiteHolder) holder).isExpand()) {
                    ((VisiteHolder) holder).getmNPrestazioni().setVisibility(View.GONE);
                    ((VisiteHolder) holder).getmCall().setVisibility(View.GONE);
                    ((VisiteHolder) holder).getmMapp().setVisibility(View.GONE);
                    ((VisiteHolder) holder).getmIndirizzo().setVisibility(View.GONE);
                    ((VisiteHolder) holder).getmTelefono().setVisibility(View.GONE);
                    ((VisiteHolder) holder).getmFreccia().animate().rotation(((VisiteHolder) holder).getmFreccia().getRotation() + 180);
                    ((VisiteHolder) holder).setExpand(false);
                }
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            RecyclerView.ViewHolder viewHolder = null;
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            switch (viewType) {
                case 0:
                    View v1 = layoutInflater.inflate(R.layout.item_data,viewGroup,false);
                    viewHolder = new DataHolder(v1);
                    break;
                case 1:
                    View v2 = layoutInflater.inflate(R.layout.item_list_content,viewGroup,false);
                    viewHolder = new VisiteHolder(v2);
                    break;
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            switch (viewHolder.getItemViewType()) {
                case 0:
                    DataHolder dataHolder = (DataHolder) viewHolder;
                    cofigureDataHolder(dataHolder, position);
                    break;
                case 1:
                    VisiteHolder visiteHolder = (VisiteHolder) viewHolder;
                    cofigureVisiteHolder(visiteHolder, position);
                    break;

            }
        }

        private void cofigureVisiteHolder(final VisiteHolder visiteHolder, final int position) {

            dbRefPazienti.child(((Visita)items.get(position)).getIdPaziente()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Paziente paziente = dataSnapshot.getValue(Paziente.class);
                    visiteHolder.getmNomePaziente().setText(paziente.getCognome() + " " + paziente.getNome());
                    visiteHolder.getmTelefono().setText(paziente.getTelefono());
                    visiteHolder.getmIndirizzo().setText(paziente.getResidenza());
                    visiteHolder.getmLayout().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(view.getContext(), PrestazioniActivity.class);
                            intent.putExtra("cognomeNomePaziente", paziente.getCognome() + " " + paziente.getNome());
                            intent.putExtra("idPaziente", ((Visita)items.get(position)).getIdPaziente());
                            intent.putExtra("dataVisita", ((Visita)items.get(position)).getData());
                            intent.putExtra("orarioVisita", ((Visita)items.get(position)).getOrario());
                            view.getContext().startActivity(intent);

                        }
                    });

                    visiteHolder.getmMapp().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String strUri = "geo:0,0?q=" + paziente.getResidenza() + " " + paziente.getCittaResidenza();
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            view.getContext().startActivity(intent);
                        }
                    });

                    visiteHolder.getmCall().setOnClickListener(new View.OnClickListener() {
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
            switch (((Visita)items.get(position)).getStato()) {
                case Pianificato:
                    visiteHolder.getmStato().setText("Pianificata");
                    visiteHolder.getmStatoImage().setImageResource(R.drawable.ic_book_black_24dp);
                    break;
                case InCorso:
                    visiteHolder.getmStato().setText("In Corso");
                    visiteHolder.getmStatoImage().setImageResource(R.drawable.ic_more_horiz_black_24dp);
                    break;
                case Terminato:
                    visiteHolder.getmStato().setText("Effettuata");
                    visiteHolder.getmStatoImage().setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
                    break;
            }
            visiteHolder.getmOrario().setText((((Visita) items.get(position)).getOrario()));
            int size = ((Visita) items.get(position)).getPrestazioni().size();
            String pluralsPrestazioni;
            if (size == 0) {
                pluralsPrestazioni = MainActivity.this.getResources().getString(R.string.nessunaPrestazione);
            } else {
                pluralsPrestazioni = MainActivity.this.getResources().getQuantityString(R.plurals.prestazioni, size, size);
            }
            visiteHolder.getmNPrestazioni().setText(pluralsPrestazioni);

            visiteHolder.getmFreccia().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    ridimensionaPianificazioni(position, MainActivity.this);

                    if (!visiteHolder.isExpand()) {
                        visiteHolder.getmFreccia().animate().rotation( visiteHolder.getmFreccia().getRotation() + 180);
                        visiteHolder.getmNPrestazioni().setVisibility(View.VISIBLE);
                        visiteHolder.getmCall().setVisibility(View.VISIBLE);
                        visiteHolder.getmMapp().setVisibility(View.VISIBLE);
                        visiteHolder.getmIndirizzo().setVisibility(View.VISIBLE);
                        visiteHolder.getmTelefono().setVisibility(View.VISIBLE);
                        visiteHolder.setExpand(true);
                    } else {
                        visiteHolder.getmNPrestazioni().setVisibility(View.GONE);
                        visiteHolder.getmCall().setVisibility(View.GONE);
                        visiteHolder.getmMapp().setVisibility(View.GONE);
                        visiteHolder.getmIndirizzo().setVisibility(View.GONE);
                        visiteHolder.getmTelefono().setVisibility(View.GONE);
                        visiteHolder.getmFreccia().animate().rotation(visiteHolder.getmFreccia().getRotation() + 180);
                        visiteHolder.setExpand(false);
                    }
                }
            });
        }

        private void cofigureDataHolder(DataHolder dataHolder, int position) {
            String date = (String) items.get(position);
            ultimaData = date;
            dataHolder.getmData().setText(date);
        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }

        @Override
        public int getItemViewType(int position) {

            if (items.get(position) instanceof Visita)
                return 1;
            else
                return 0;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recyclerView.setAdapter(null);
            caricaProssimiTreGiorni(new ArrayList<Object>(), dataSelezionata,4,7);

    }
}
