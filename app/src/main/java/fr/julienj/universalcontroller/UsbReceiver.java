package fr.julienj.universalcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class UsbReceiver extends BroadcastReceiver {

    private static final String TAG = "UsbReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // assumes WordService is a registered service
        //Intent intent = new Intent(context, WordService.class);
        //context.startService(intent);
        Log.i(TAG,"Connexion USB Détecté");
        Toast.makeText(context, "Connexion USB Détecté",Toast.LENGTH_LONG).show();
    }
}

