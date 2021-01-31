package fr.julienj.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class WebServerService extends Service {

    private final IBinder mBinder = new WebServerBinder();

    public class WebServerBinder extends Binder {
        WebServerService getService() {
            return WebServerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startWebServer()
    {
        TinyWebServer.startServer("0.0.0.0",Constants.PORT_HTTP, "", getApplicationContext().getAssets());
    }

    public void stopWebServer()
    {
        TinyWebServer.stopServer();
    }

    @Override
    public void onDestroy() {
        stopWebServer();
        super.onDestroy();
    }
}
