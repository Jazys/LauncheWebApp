package fr.julienj.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class UsbReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // assumes WordService is a registered service
        //Intent intent = new Intent(context, WordService.class);
        //context.startService(intent);
        Toast.makeText(context, "Connexion USB Détecté",Toast.LENGTH_LONG).show();
    }
}

