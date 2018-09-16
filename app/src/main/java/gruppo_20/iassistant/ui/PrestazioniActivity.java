package gruppo_20.iassistant.ui;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    private String idPaziente;

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

    private static BluetoothAdapter bluetoothAdapter;
    private static BluetoothDevice[] btArray;
    private static ArrayList<String> dati;


    private static SendReceive sendReceive;
    private static ClientClass clientClass;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;

    int REQUEST_ENABLE_BLUETOOTH=1;

    private static final String APP_NAME = "iAssistant";
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
    private static boolean dato_arrivato = false;

    private static Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what)
            {
                case STATE_LISTENING:
                    stato.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    stato.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    stato.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    stato.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    if (tempMsg.equals("-0101")) {
                        dato_arrivato = true;
                    }else{
                        valorOttenutoBlu.setText(tempMsg);
                        dati.add(tempMsg);
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

        // Inizializzazione bluetooth
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        //Verifica delle presenza del bluetooth
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
        }

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
                            Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
                            String[] strings=new String[bt.size()];
                            btArray=new BluetoothDevice[bt.size()];
                            int index=0;

                            if( bt.size()>0)
                            {
                                for(BluetoothDevice device : bt)
                                {
                                    btArray[index]= device;
                                    strings[index]=device.getName();
                                    index++;
                                }
                                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(v.getContext(),android.R.layout.simple_list_item_1,strings);
                                listaDispositivi.setAdapter(arrayAdapter);
                            }

                            listaDispositivi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                                    stato = (TextView) blueDialogList.findViewById(R.id.stato);
                                    stato.setText("Connessione");


                                        blueDialogList.cancel();
                                        inserimentoBlueBialog = new Dialog(view.getContext());
                                        inserimentoBlueBialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        inserimentoBlueBialog.setTitle("Bluetooth");
                                        inserimentoBlueBialog.setContentView(R.layout.inserimento_dati_bluetooth);
                                        stato = (TextView) blueDialogList.findViewById(R.id.stato);

                                        final Button conferma = (Button) inserimentoBlueBialog.findViewById(R.id.button_conferma_blu);
                                        Button annulla = (Button) inserimentoBlueBialog.findViewById(R.id.button_riesegui);
                                        valorOttenutoBlu = (TextView) inserimentoBlueBialog.findViewById(R.id.valorOttenutoBlu);
                                        EditText noteInserite = (EditText) inserimentoBlueBialog.findViewById(R.id.noteInserite);

                                        //TODO ANGELO
                                        conferma.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(dato_arrivato == true){
                                                    dato_arrivato = false;
                                                    Toast.makeText(v.getContext(),"dati salvati correttamente",Toast.LENGTH_LONG).show();
                                                    clientClass.cancel();

                                                    inserimentoBlueBialog.cancel();
                                                }else{
                                                    Toast.makeText(v.getContext(),"dati in ricezione, ATTENDERE",Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        });
                                        annulla.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                inserimentoBlueBialog.cancel();
                                            }
                                        });
                                        clientClass=new ClientClass(btArray[position]);
                                        clientClass.start();
                                        dati = new ArrayList<>();
                                        stato.setText("Connesione");
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
                            TextView dato = (TextView)  inserimentoDialog.findViewById(R.id.datoInserito);
                            EditText noteInserite = (EditText)  inserimentoDialog.findViewById(R.id.noteInserite);

                            //TODO ANGELO
                            conferma.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // salvare nel database il valore contenuto nella TextView dato, e nella EditText noteInserite controlllare se sono presenti
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


    private static class ClientClass extends Thread
    {
        private BluetoothDevice device;
        private BluetoothSocket socket;

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

    private static  class SendReceive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
            outputStream=tempOut;
        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (true)
            {
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

