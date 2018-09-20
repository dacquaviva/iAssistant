package gruppo_20.iassistant.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootUpReciver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, NotificaPianificazioni.class);
        service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(service);
    }
}

