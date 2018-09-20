package gruppo_20.iassistant.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import gruppo_20.iassistant.R;
import gruppo_20.iassistant.model.Punto;

public class SimulaDispositiviActivity extends AppCompatActivity {
    private TextView stato;
    private  Button btAccendi;
    private  Button btGeneraDato;
    private Button btGeneraFlusso;

    private  Random rand = new Random();

    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;

    private static final String APP_NAME = "iAssistant";
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
    private int REQUEST_ENABLE_BLUETOOTH=1;
    private BluetoothAdapter bluetoothAdapter;
    private boolean connesso = false;
    private ServerClass serverClass ;
    private SendReceive sendReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simula_dispositivi);

        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
        }
        findViewByIdes();
        implementListeners();
    }

    private void implementListeners() {
        btAccendi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sendReceive !=null){
                    sendReceive.cancel();
                }
                if(serverClass!=null){
                    serverClass.cancel();
                }
                connesso = false;
                serverClass=new ServerClass();
                serverClass.start();

                stato.setText("connessione");

            }
        });

        btGeneraDato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(connesso == true){
                    Punto punto = new Punto(rand.nextInt(10),rand.nextInt(10),true,false);
                    sendReceive.writeSerialized(punto);
                }else{
                    Toast.makeText(SimulaDispositiviActivity.this,"Accendere prima il dispositivo dell'apposito tasto",Toast.LENGTH_LONG).show();
                }



            }
        });

        btGeneraFlusso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(connesso == true){
                    ArrayList<Punto> punti = new ArrayList<Punto>();
                    boolean ultimo = false;
                    for (int i=0;i<10;i++){
                        Double y = (5*Math.sin(7*i)*Math.sin(0.5*i)*Math.cos(3.25*i));
                        if(i==9){
                            ultimo = true;
                        }
                        punti.add(new Punto(i,y.floatValue(),ultimo,true)) ;
                    }

                    for(Punto punto: punti){
                        sendReceive.writeSerialized(punto);
                        try {
                            TimeUnit.SECONDS.sleep(1
                            );
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    Toast.makeText(SimulaDispositiviActivity.this,"Accendere prima il dispositivo dell'apposito tasto",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void findViewByIdes() {



        btAccendi = (Button) findViewById(R.id.btAccendi);
        btGeneraDato = (Button) findViewById(R.id.btSingoloDato);
        btGeneraFlusso = (Button) findViewById(R.id.btFlussoDati);
        stato = (TextView) findViewById(R.id.stato);
    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what)
            {
                case STATE_CONNECTING:
                    stato.setText("Connessione");
                    connesso = false;
                    break;
                case STATE_CONNECTED:
                    stato.setText("Connesso");
                    connesso = true;
                    break;
                case STATE_CONNECTION_FAILED:
                    stato.setText("Connessione fallita");
                    connesso = false;
                    break;
            }
            return true;
        }
    });

    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;
        private  BluetoothSocket socket;

        public ServerClass(){
            try {
                serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            socket=null;

            while (socket==null)
            {
                try {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket=serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if(socket!=null)
                {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sendReceive=new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
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

        public SendReceive (BluetoothSocket socket)
        {
            bluetoothSocket=socket;
        }

        public void run()
        {

        }


        public void writeSerialized(Object p){

            try {
                if(outputStream== null){
                    outputStream=new  ObjectOutputStream(bluetoothSocket.getOutputStream());
                }

                outputStream.writeObject(p);

            }catch(Exception e){

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
