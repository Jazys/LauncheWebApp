package fr.julienj.universalcontroller.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.UnknownHostException;

import fr.julienj.universalcontroller.Constants;

public class WebSocketServerService extends Service {

    private static final String TAG = "WebSocketServer";

    private final IBinder mBinder = new WebSocketServerBinder();
    private WebSocketServer socketWSS = null;

    public class WebSocketServerBinder extends Binder {
        public WebSocketServerService getService() {
            return WebSocketServerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startWSS()
    {

        try {
            socketWSS = new WebSocketServer(Constants.PORT_WS);
            socketWSS.start();
            Log.i(TAG,"startWSS");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void stopWSS() {
        try
        {
            socketWSS.stop();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        stopWSS();
        Log.i(TAG,"stop WSS");
        super.onDestroy();
    }
}
