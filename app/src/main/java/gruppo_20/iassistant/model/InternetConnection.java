package gruppo_20.iassistant.model;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

//classe che verifica lo stato della connessione ad internet.
public class InternetConnection{

    //Il metodo restituisce True se la connessione ad internet è presente. False se no
    public static boolean haveInternetConnection(Context contesto) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) contesto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}