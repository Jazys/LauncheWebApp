package fr.julienj.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.net.UnknownHostException;

public class WebSocketServerService extends Service {

    private final IBinder mBinder = new WebSocketServerBinder();
    private WebSocketServer socketWSS = null;

    public class WebSocketServerBinder extends Binder {
        WebSocketServerService getService() {
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
        System.out.println("jj stopWSS");
        super.onDestroy();
    }
}
