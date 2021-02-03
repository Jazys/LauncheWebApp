package fr.julienj.universalcontroller.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.net.UnknownHostException;

import fr.julienj.universalcontroller.Constants;
import fr.julienj.universalcontroller.R;

public class WebSocketServerService extends Service {

    private static final String TAG = "WebSocketServer";
    public boolean isRunning=false;

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
            isRunning=true;
            createNotification();
            Log.i(TAG,"startWSS");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void stopWSS() {
        try
        {
            socketWSS.stop();
            isRunning=false;
            cancelNotification();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(String data)
    {
        socketWSS.broadcast(data);
    }

    @Override
    public void onDestroy() {
        stopWSS();
        Log.i(TAG,"stop WSS");
        super.onDestroy();
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(Constants.NOTIFICATION_CHANNEL, "Background service", NotificationManager.IMPORTANCE_LOW);
            nc.setShowBadge(false);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(nc);
        }
        Intent disconnectIntent = new Intent()
                .setAction(Constants.INTENT_ACTION_DISCONNECT);
        Intent restartIntent = new Intent()
                .setClassName(this, Constants.INTENT_CLASS_MAIN_ACTIVITY)
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent disconnectPendingIntent = PendingIntent.getBroadcast(this, 1, disconnectIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent restartPendingIntent = PendingIntent.getActivity(this, 1, restartIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("WebSocket Server connecté")
                .setContentIntent(restartPendingIntent)
                .setOngoing(true);
        //.addAction(new NotificationCompat.Action(R.drawable.ic_launcher_background, "Disconnect", disconnectPendingIntent));
        // @drawable/ic_notification created with Android Studio -> New -> Image Asset using @color/colorPrimaryDark as background color
        // Android < API 21 does not support vectorDrawables in notifications, so both drawables used here, are created as .png instead of .xml
        Notification notification = builder.build();
        startForeground(Constants.NOTIFY_MANAGER_START_FOREGROUND_SERVICE, notification);
    }

    private void cancelNotification() {
        stopForeground(true);
    }
}
