package gruppo_20.iassistant.ui;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import gruppo_20.iassistant.R;
import gruppo_20.iassistant.model.Prestazione;
import gruppo_20.iassistant.model.Punto;
import gruppo_20.iassistant.model.Stato;
import gruppo_20.iassistant.model.Visita;

public class PrestazioniActivity extends AppCompatActivity {

    private static RecyclerView prestazioniList;
    private static android.support.design.bottomappbar.BottomAppBar but;

    private String dataVisita;
    private String orarioVisita;
    private String cognomeNomePaziente;
    private String idPaziente;
    private static Visita visita;

    private static Chip cBlu;
    private static Chip cMan;
    private static Dialog blueDialogList;
    private static Dialog inserimentoDialog;
    private static Dialog modalitaInserimentoDialog;
    private static ListView listaDispositivi;
    private static Dialog inserimentoBlueBialog;
    private static FloatingActionButton anagrafica;

    private DatabaseReference dbRefVisita;
    private String idOperatore = FirebaseAuth.getInstance().getUid();

    //Button listen,send, listDevices;
    private static TextView stato,valorOttenutoBlu;
    private static Chip terminaPianificazione;
    private static BluetoothAdapter bluetoothAdapter;
    private static ArrayList<BluetoothDevice> bluetoothDevicesTrovati;
    private ArrayList<String> devicesTrovati;
    private static ArrayList<Punto> dati;
    ArrayAdapter<String> arrayAdapter;


    private static SendReceive sendReceive;
    private static ClientClass clientClass;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;

    int REQUEST_ENABLE_BLUETOOTH=1;
    
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
    private static boolean dato_arrivato = false;
    private boolean connesso = false;

    private  Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what)
            {
                case STATE_LISTENING:
                    stato.setText("Listening");
                    connesso = false;
                    break;
                case STATE_CONNECTING:
                    stato.setText("Connecting");
                    connesso = false;
                    break;
                case STATE_CONNECTED:
                    stato.setText("Connected");
                    connesso = true;
                    break;
                case STATE_CONNECTION_FAILED:
                    connesso = false;;
                    break;
                case STATE_MESSAGE_RECEIVED:
                    connesso = true;
                    Punto dato  = (Punto) msg.obj;
                    LineChart graficoView = (LineChart) inserimentoBlueBialog.findViewById(R.id.graficoBluetooth);
                    valorOttenutoBlu = (TextView) inserimentoBlueBialog.findViewById(R.id.valorOttenutoBlu);
                    if (dato.isTerminatore()) {

                        ProgressBar progressBarBluetoooth = (ProgressBar) inserimentoBlueBialog.findViewById(R.id.progressBarBluetooth);
                        progressBarBluetoooth.setVisibility(View.GONE);
                        Button confermaBtn = (Button)  inserimentoBlueBialog.findViewById(R.id.button_conferma_blu);
                        confermaBtn.setTextColor(Color.rgb(55,207,221));
                        confermaBtn.setClickable(true);
                        dato_arrivato = true;
                        dati.add(dato);

                        ArrayList<Entry> datiBluetooth = new ArrayList<Entry>();

                        for(Punto punto : dati){
                            datiBluetooth.add(new Entry(punto.getX(),punto.getY()));

                        }


                        if(dato.isFlusso() == true ){
                            valorOttenutoBlu.setVisibility(View.GONE);
                            graficoView.setVisibility(View.VISIBLE);
                            LineDataSet lineData = new LineDataSet(datiBluetooth,"Risultato");
                            lineData.setLineWidth(2);
                            ArrayList<ILineDataSet> iLineDataSet = new ArrayList<>();
                            iLineDataSet.add(lineData);
                            LineData data = new LineData(iLineDataSet);
                            graficoView.setData(data);
                            Description description = new Description();
                            description.setEnabled(false);
                            graficoView.setDescription(description);
                            graficoView.invalidate();


                        }else{

                            valorOttenutoBlu.setText("" + dati.get(0).getY());

                            graficoView.setVisibility(View.GONE);
                            valorOttenutoBlu.setVisibility(View.VISIBLE);
                        }




                    }else if(dati.get(dati.size()-1).isTerminatore()){
                        dati.clear();

                        }else{
                            dati.add(dato);
                        }


                    break;
            }
            return true;
        }
    });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestazioni);

        anagrafica = findViewById(R.id.fab_anagrafica);
        anagrafica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrestazioniActivity.this, AnagraficaActivity.class);
                intent.putExtra("idPaziente", idPaziente);
                startActivity(intent);
            }
        });

        dataVisita = getIntent().getExtras().getString("dataVisita");
        orarioVisita = getIntent().getExtras().getString("orarioVisita");
        cognomeNomePaziente = getIntent().getExtras().getString("cognomeNomePaziente");
        idPaziente = getIntent().getExtras().getString("idPaziente");
        dbRefVisita = FirebaseDatabase.getInstance().getReference().child("operatori").child(idOperatore).child("visite").child(dataVisita).child(orarioVisita);
        prestazioniList = (RecyclerView) findViewById(R.id.prestazioni_list);




        //Gestione del tasto di termina Pianificazione
        terminaPianificazione = (Chip) findViewById(R.id.termina_pianificazione);
        terminaPianificazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRefVisita.child("stato").setValue(Stato.Terminato);
                Toast.makeText(PrestazioniActivity.this, getResources().getString(R.string.operazioneTerminataConSuccesso), Toast.LENGTH_LONG).show();
                terminaPianificazione.setVisibility(View.GONE);
            }
        });

        //Riempimento Header con i dati del paziente
        TextView nomePaziente = (TextView)  findViewById(R.id.headPrestazioni_nomePazienteTextView);
        nomePaziente.setText(cognomeNomePaziente);
        final TextView numPrestazioniDaSvolgere = (TextView)  findViewById(R.id.headPrestazioni_numPrestazioniTextView);

        //Riempimento recyclerView con i dati delle prestazioni del DB
        dbRefVisita.keepSynced(true);
        dbRefVisita.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                visita = dataSnapshot.getValue(Visita.class);
                int prestazioniDaSvolgere = visita.contaPrestazioniDaSvolgere();
                if(prestazioniDaSvolgere == 0){
                    numPrestazioniDaSvolgere.setText(getResources().getString(R.string.nessunaPrestazione) + " da svolgere");
                    if (visita.getStato() == Stato.InCorso) {
                        terminaPianificazione.setVisibility(View.VISIBLE);
                    }
                }else {
                    numPrestazioniDaSvolgere.setText(getResources().getQuantityString(R.plurals.prestazioni, prestazioniDaSvolgere, prestazioniDaSvolgere) + " da svolgere");
                }

                assert prestazioniList != null;
                setupRecyclerView(prestazioniList, visita.getPrestazioni(),visita.getStato());
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

        //but.inflateMenu(R.menu.prestazioni_menu);

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
    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Prestazione> itemPrestazioni, Stato statoVisita) { //@NonNull specifica che il metodo non potrà mai restituire null
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, itemPrestazioni,statoVisita));
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            ProgressBar progressBar = (ProgressBar) blueDialogList.findViewById(R.id.progressBar);
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                progressBar.setVisibility(View.VISIBLE);

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                progressBar.setVisibility(View.GONE);

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevicesTrovati.add(device);
                devicesTrovati.add(device.getName());
                arrayAdapter.notifyDataSetChanged();

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            unregisterReceiver(mReceiver);

        }catch (Exception e){

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        //Check what request we’re responding to//
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {

            //If the request was successful…//
            if (resultCode == Activity.RESULT_OK) {
                //...then display the following toast.//

                modalitaInserimentoDialog.cancel();
                blueDialogList = new Dialog(PrestazioniActivity.this);
                blueDialogList.requestWindowFeature(Window.FEATURE_NO_TITLE);
                blueDialogList.setTitle("Bluetooth");
                blueDialogList.setContentView(R.layout.bluetooth);
                listaDispositivi = (ListView) blueDialogList.findViewById(R.id.lista_dispositivi);
                //TODO controlla la progresBar il GONE
                ProgressBar progressBar = (ProgressBar) blueDialogList.findViewById(R.id.progressBar);
                devicesTrovati = new ArrayList<String>();
                bluetoothDevicesTrovati = new ArrayList<BluetoothDevice>();

                arrayAdapter = new ArrayAdapter<String>(PrestazioniActivity.this, android.R.layout.simple_list_item_1, devicesTrovati);
                listaDispositivi.setAdapter(arrayAdapter);
                progressBar.setVisibility(View.GONE);
                Button scan = (Button) blueDialogList.findViewById(R.id.scan);

                scan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            scan();

                        }
                    });



                listaDispositivi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                       if( clientClass!=null && clientClass.getDevice() != bluetoothDevicesTrovati.get(position)){
                           sendReceive.cancel();
                           clientClass.cancel();
                           connesso = false;
                           clientClass = new ClientClass(bluetoothDevicesTrovati.get(position));
                           clientClass.start();
                       }

                        if(connesso==true){
                            stato = (TextView) blueDialogList.findViewById(R.id.stato);
                            stato.setText("Connessione");


                            blueDialogList.cancel();
                            inserimentoBlueBialog = new Dialog(view.getContext());
                            inserimentoBlueBialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            inserimentoBlueBialog.setTitle("Bluetooth");
                            inserimentoBlueBialog.setContentView(R.layout.inserimento_dati_bluetooth);
                            final ProgressBar progressBarBluetoooth = (ProgressBar) inserimentoBlueBialog.findViewById(R.id.progressBarBluetooth);
                            LineChart graficoView = (LineChart) inserimentoBlueBialog.findViewById(R.id.graficoBluetooth);
                            TextView valoreOttenutoBlu = (TextView) inserimentoBlueBialog.findViewById(R.id.valorOttenutoBlu);
                            valoreOttenutoBlu.setVisibility(View.GONE);
                            graficoView.setVisibility(View.GONE);
                            stato = (TextView) blueDialogList.findViewById(R.id.stato);

                            Button conferma = (Button) inserimentoBlueBialog.findViewById(R.id.button_conferma_blu);
                            conferma.setTextColor(Color.GRAY);
                            conferma.setClickable(false);
                            Button annulla = (Button) inserimentoBlueBialog.findViewById(R.id.button_riesegui);
                            valorOttenutoBlu = (TextView) inserimentoBlueBialog.findViewById(R.id.valorOttenutoBlu);

                            EditText noteInserite = (EditText) inserimentoBlueBialog.findViewById(R.id.noteInserite);

                            conferma.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (dato_arrivato == true) {
                                        dato_arrivato = false;
                                        progressBarBluetoooth.setVisibility(View.GONE);
                                        Toast.makeText(v.getContext(), "dati salvati correttamente", Toast.LENGTH_LONG).show();
                                        ArrayList<Entry> datiBluetooth = new ArrayList<Entry>();


                                        for(Punto punto : dati){
                                            datiBluetooth.add(new Entry(punto.getX(),punto.getY()));

                                        }
                                        //TODO  SETTARE IL GIUSTO POSITION
                                        //    int pos = data.getIntExtra("posizione",0);
                                        dbRefVisita.child("prestazioni").child("" + position).child("risultato").setValue(datiBluetooth);

                                        dati.clear();
                                        inserimentoBlueBialog.cancel();
                                    } else {
                                        Toast.makeText(v.getContext(), "dati in ricezione, ATTENDERE", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                            annulla.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    inserimentoBlueBialog.cancel();
                                }
                            });

                            dati = new ArrayList<>();
                            stato.setText("Connesione");
                            inserimentoBlueBialog.show();
                        }



                    }
                });

                blueDialogList.show();
                scan();
            }

            //If the request was unsuccessful...//
            if(resultCode == RESULT_CANCELED){

                //...then display this alternative toast.//
                Toast.makeText(getApplicationContext(), "Errore nell'accensione del Bluetooth",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scan() {

        try{
            devicesTrovati.clear();
            bluetoothDevicesTrovati.clear();
            arrayAdapter.clear();
            bluetoothAdapter.cancelDiscovery();
            unregisterReceiver(mReceiver);



        }catch (Exception e){

        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        bluetoothAdapter.startDiscovery();
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final PrestazioniActivity mParentActivity;
        private final List<Prestazione> mValues;
        private final Stato mStatoVisita;


        SimpleItemRecyclerViewAdapter(PrestazioniActivity parent, List<Prestazione> items, Stato statoVisita) {
            mValues = items;
            mParentActivity = parent;
            mStatoVisita = statoVisita;
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
                holder.mNumPrestazione.setTextColor(Color.WHITE);
                holder.mNumPrestazione.setBackgroundResource(R.drawable.oval_button_green);
            }
            holder.mNumPrestazione.setText("" + (position + 1));

            holder.mlayoutPrestazine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mValues.get(position).isEffectuated() && dataVisita.equals(MainActivity.setCalendarToString(Calendar.getInstance()))){
                        modalitaInserimentoDialog = new Dialog(v.getContext());
                        modalitaInserimentoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        modalitaInserimentoDialog.setContentView(R.layout.modalita_misurazione);
                        modalitaInserimentoDialog.setTitle("Scelta tipo di misurazione");


                        cMan = (Chip) modalitaInserimentoDialog.findViewById(R.id.chipManuale);
                        cBlu = (Chip) modalitaInserimentoDialog.findViewById(R.id.chipBlu);
                        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (bluetoothAdapter != null) {


                            cBlu.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                   // enableIntent.putExtra("posizione", position);
                                    startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);


                                }
                            });
                        } else {
                            cBlu.setVisibility(View.GONE);
                            Toast.makeText(v.getContext(), "Il divice non e' dotato di bluetooth", Toast.LENGTH_LONG).show();
                            cMan.setPadding(5, 0, 5, 0);
                        }

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
                                final TextView dato = (TextView) inserimentoDialog.findViewById(R.id.datoInserito);
                                final EditText noteInserite = (EditText) inserimentoDialog.findViewById(R.id.noteInserite);


                                conferma.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(mStatoVisita.equals(Stato.Pianificato)){
                                            dbRefVisita.child("stato").setValue(Stato.InCorso);
                                        }
                                        if (dato.getText().toString().equals("")) {
                                            Toast.makeText(PrestazioniActivity.this, mParentActivity.getResources().getString(R.string.risultatoVuoto), Toast.LENGTH_LONG).show();
                                        } else {
                                            float f = Float.parseFloat(dato.getText().toString());
                                            Entry entry = new Entry(f, f);
                                            ArrayList<Entry> array = new ArrayList<Entry>();
                                            array.add(entry);
                                            Prestazione mValuesDaSalvare = mValues.get(position);
                                            mValuesDaSalvare.setRisultato(array);
                                            if (!noteInserite.getText().toString().equals("")) {
                                                mValuesDaSalvare.setDatiOpzionali(noteInserite.getText().toString());
                                            }
                                            dbRefVisita.child("prestazioni").child("" + position).setValue(mValuesDaSalvare);
                                            inserimentoDialog.cancel();
                                        }
                                    }
                                });
                                annulla.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        inserimentoDialog.cancel();
                                    }
                                });
                                inserimentoDialog.show();
                            }
                        });
                    modalitaInserimentoDialog.show();
                    } else if(mValues.get(position).isEffectuated()){
                        Dialog dialogRisultato =  new Dialog(v.getContext());
                        dialogRisultato.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialogRisultato.setContentView(R.layout.visualizza_risultato_dialog);
                        TextView note = (TextView) dialogRisultato.findViewById(R.id.note);
                        TextView risultatoTextView = (TextView) dialogRisultato.findViewById(R.id.risultato);
                        LineChart graficoView = (LineChart) dialogRisultato.findViewById(R.id.grafico);
                        if (mValues.get(position).getRisultato().size()>1){
                            risultatoTextView.setVisibility(View.GONE);
                            ArrayList<Entry> risultatiList = mValues.get(position).getRisultato();
                            LineDataSet lineData = new LineDataSet(risultatiList,"Risultato");
                            lineData.setLineWidth(2);
                            lineData.setCircleColor(getResources().getColor(R.color.colorPrimary));
                            ArrayList<ILineDataSet> iLineDataSet = new ArrayList<>();
                            iLineDataSet.add(lineData);
                            LineData data = new LineData(iLineDataSet);
                            graficoView.setData(data);
                            Description description = new Description();
                            description.setEnabled(false);
                            graficoView.setDescription(description);
                            graficoView.invalidate();
                        }else{
                            graficoView.setVisibility(View.GONE);
                            Float ris = mValues.get(position).getRisultato().get(0).getY();
                            risultatoTextView.setText(ris.toString());

                        }
                        if(mValues.get(position).getDatiOpzionali() != null && !mValues.get(position).getDatiOpzionali().equals("")){
                            note.setText(mValues.get(position).getDatiOpzionali());
                        }
                        dialogRisultato.show();
                    }
                }//fine metodo onClick

            });// fine onClickListener

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            final Button mNumPrestazione;
            final TextView mNomePrestazione;
            final LinearLayout mlayoutPrestazine;

            ViewHolder(View view) {
                super(view);
                mNumPrestazione = (Button) view.findViewById(R.id.numeroPrestazione);
                mNomePrestazione = (TextView) view.findViewById(R.id.nomePrestazione);
                mlayoutPrestazine = (LinearLayout) view.findViewById(R.id.pianificazioniListContent);
            }
        }
    }

    private  class ClientClass extends Thread
    {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public BluetoothDevice getDevice() {
            return device;
        }

        public ClientClass (BluetoothDevice device1)
        {
            device=device1;

            try {
                socket=device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            try {
                socket.connect();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive=new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    private class SendReceive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;

        public SendReceive (BluetoothSocket socket)
        {
            bluetoothSocket=socket;
        }

        public void run()
        {

            while (true)
            {
                try {
                    if(inputStream == null ){
                        inputStream=new ObjectInputStream(bluetoothSocket.getInputStream());
                    }

                    Object punto =  inputStream.readObject();

                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,punto).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            try {
                outputStream.close();
                bluetoothSocket.close();

            } catch (IOException e) {
            }
        }
    }
}

