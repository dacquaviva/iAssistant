package gruppo_20.iassistant.ui;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import gruppo_20.iassistant.R;

/**
 * Created by vito on 19/09/2018.
 */
public class NotificaPianificazioni extends Service {

    private Calendar calendar = Calendar.getInstance();
    private DatabaseReference dbRefVisite;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        FileInputStream fileInput = null;
        String dataOdierna = MainActivity.setCalendarToString(calendar);

        try {
            fileInput = openFileInput("ID_operatore.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        InputStreamReader inputReader = new InputStreamReader(fileInput);
        BufferedReader bufferedReader = new BufferedReader(inputReader);
        String idOperatore = "";

        try {
            idOperatore = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dbRefVisite = FirebaseDatabase.getInstance().getReference().child("operatori")
                .child(idOperatore).child("visite").child(dataOdierna);
        dbRefVisite.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> orari = new ArrayList<String>();

                for (DataSnapshot data : dataSnapshot.getChildren()){
                    orari.add(data.getKey());
                }

                for (int i = 0; i<orari.size(); i++) {

                    String orario = orari.get(i);
                    String[] time = orario.split ( ":" );
                    int ora = Integer.parseInt ( time[0].trim());
                    int min = Integer.parseInt ( time[1].trim());

                    calendar.set(Calendar.HOUR_OF_DAY, ora - 1);
                    calendar.set(Calendar.MINUTE, min);
                    calendar.set(Calendar.SECOND, 0);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(NotificaPianificazioni.this, AlertReciver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(NotificaPianificazioni.this, 1, intent, 0);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return super.onStartCommand(intent, flags, startId);
    }
}
