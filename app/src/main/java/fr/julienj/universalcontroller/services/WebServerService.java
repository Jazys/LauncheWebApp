package fr.julienj.universalcontroller.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import fr.julienj.universalcontroller.Constants;

public class WebServerService extends Service {

    private static final String TAG = "WebServerService";
    private final IBinder mBinder = new WebServerBinder();

    public boolean isRunning=false;

    public class WebServerBinder extends Binder {
        public WebServerService getService() {
            return WebServerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startWebServer()
    {
        TinyWebServer.startServer("0.0.0.0", Constants.PORT_HTTP, "", getApplicationContext().getAssets());
        isRunning=true;
        Log.i(TAG, "startWebServer");
    }

    public void stopWebServer()
    {
        TinyWebServer.stopServer();
        isRunning=false;
    }

    @Override
    public void onDestroy() {
        stopWebServer();
        super.onDestroy();
    }
}
