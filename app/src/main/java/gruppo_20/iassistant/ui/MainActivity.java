package gruppo_20.iassistant.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
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
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;


import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gruppo_20.iassistant.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CalendarView calendarView;
    private int day;
    private int month;
    private int year;
    private int dayOfYear;

    private final static String[] MESI = {"Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio",
            "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //inizializzato calendario alla data odierna
        calendarView = (CalendarView) findViewById(R.id.main_calendarView);
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        aggiornaDataText();

        try {
            calendarView.setDate(calendar);

        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                day = eventDay.getCalendar().get(Calendar.DAY_OF_MONTH);
                month = eventDay.getCalendar().get(Calendar.MONTH);
                year = eventDay.getCalendar().get(Calendar.YEAR);
                dayOfYear = eventDay.getCalendar().get(Calendar.DAY_OF_YEAR);
                aggiornaDataText();
                aggiornaCalendarView();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);
        SlidingUpPanelLayout slidingPaneLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingPanel);
        slidingPaneLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.longCalendar);
                CalendarView calendar = (CalendarView) findViewById(R.id.main_calendarView);

                if (newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
                    calendar.animate().alpha(0).setDuration(100);
                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayout.animate().alpha(1.0f);
                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    linearLayout.animate().alpha(0.0f).setDuration(50);
                    linearLayout.setVisibility(View.GONE);
                    calendar.animate().alpha(1).setDuration(50);
                    aggiornaCalendarView();
                }
            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) { //@NonNull specifica che il metodo non potrà mai restituire null
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, Oggetti.ITEMS));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final MainActivity mParentActivity;
        private final List<Oggetti> mValues;

        /**
         * COSTRUTTORE
         */
        SimpleItemRecyclerViewAdapter(MainActivity parent, List<Oggetti> items) {
            mValues = items;
            mParentActivity = parent;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mNomePaziente.setText(mValues.get(position).nomePaziente);
            switch (mValues.get(position).stato) {
                case 0:
                    holder.mStato.setText("Pianificata");
                    holder.mStatoImage.setImageResource(R.drawable.ic_book_black_24dp);
                    break;
                case 1:
                    holder.mStato.setText("In Corso");
                    holder.mStatoImage.setImageResource(R.drawable.ic_more_horiz_black_24dp);
                    break;
                case 2:
                    holder.mStato.setText("Effettuata");
                    holder.mStatoImage.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
                    break;
            }
            holder.mOrario.setText(mValues.get(position).orario);
            holder.mNPrestazioni.setText(mValues.get(position).nPrestazioni);

            holder.mFreccia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
            return mValues.size();
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

    public void giornoPrecedente(View view) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, --dayOfYear);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        aggiornaDataText();
    }

    public void giornoSuccessivo(View view) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, ++dayOfYear);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        aggiornaDataText();
    }

    private void aggiornaDataText() {
        TextView dataText = (TextView) findViewById(R.id.dataText);
        dataText.setText(day + " " + MESI[month] + " " + year);
    }

    private void aggiornaCalendarView() {
        CalendarView calendarView = (CalendarView) findViewById(R.id.main_calendarView);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR , dayOfYear);
        try {
            calendarView.setDate(cal);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
    }
}
