package fr.julienj.universalcontroller.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import fr.julienj.universalcontroller.Constants;
import fr.julienj.universalcontroller.R;
import fr.julienj.universalcontroller.interfaceclass.SerialListener;
import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.DeviceCallback;

public class BluetoothService extends Service {

    private static final String TAG = "BluetoothService";
    private Handler mainLooper;
    public boolean isRunning = false;

    private final IBinder mBinder = new BluetoothSocketSerie();
    private Bluetooth bluetooth1;
    private Bluetooth bluetooth2;
    private SerialListener listener;

    private DeviceCallback deviceCallBackBluetooth1 = new DeviceCallback() {
        @Override
        public void onDeviceConnected(BluetoothDevice device) {
        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device, String message) {
        }

        @Override
        public void onMessage(byte[] message) {
            String mess = "";
            for (int i = 0; i < message.length; i++) {
                mess += String.valueOf((char) message[i]);
            }
            Log.i(TAG, "onMessage " + mess);

            if (listener != null)
                listener.onSerialRead(message);

            bluetooth1.send(mess + "\r\n");
        }

        @Override
        public void onError(int errorCode) {

        }

        @Override
        public void onConnectError(BluetoothDevice device, String message) {
        }
    };

    private DeviceCallback deviceCallBackBluetooth2 = new DeviceCallback() {
        @Override
        public void onDeviceConnected(BluetoothDevice device) {
        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device, String message) {
        }

        @Override
        public void onMessage(byte[] message) {
            String mess = "";
            for (int i = 0; i < message.length; i++) {
                mess += String.valueOf((char) message[i]);
            }

            if (listener != null)
                listener.onSerialRead(message);

            Log.i(TAG, "onMessage2 " + mess);
            bluetooth2.send(mess + "\r\n");
        }

        @Override
        public void onError(int errorCode) {

        }

        @Override
        public void onConnectError(BluetoothDevice device, String message) {
        }
    };


    public class BluetoothSocketSerie extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startBluetoothServer(String nameM1BT, String nameM2BT) {
        mainLooper = new Handler(Looper.getMainLooper());
        bluetooth1 = new Bluetooth(getApplicationContext());
        bluetooth1.onStart();
        Log.i(TAG, "startBluetoothServer1 " + bluetooth1.getPairedDevices());

        if(nameM1BT!="") {
            bluetooth1.connectToName(nameM1BT);
            Log.i(TAG, "Bluetooth connect with Pref "+nameM1BT);
        }
        else {
            bluetooth1.connectToName(Constants.NAME_BLUETOOTH);
            Log.i(TAG, "Bluetooth connect with Constant "+Constants.NAME_BLUETOOTH);
        }
        bluetooth1.setDeviceCallback(deviceCallBackBluetooth1);
        isRunning = true;

        //bluetooth.startScanning();


        if (nameM2BT != "") {
            bluetooth2 = new Bluetooth(getApplicationContext());
            bluetooth2.onStart();
            Log.i(TAG, "startBluetoothServer2 " + bluetooth2.getPairedDevices());
            //bluetooth2.connectToAddress("08:21:EF:6C:9B:78");
            bluetooth2.connectToName(nameM2BT);
            bluetooth2.setDeviceCallback(deviceCallBackBluetooth2);

        }

        createNotification();

    }

    public void stopBluetoothServer() {
        if (bluetooth1 != null && bluetooth1.isConnected())
            bluetooth1.disconnect();

        if (bluetooth2 != null && bluetooth2.isConnected())
            bluetooth2.disconnect();

        isRunning = false;
        cancelNotification();

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
                .setContentText("Bluetooth Connecté")
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

    @Override
    public void onDestroy() {
        stopBluetoothServer();
        super.onDestroy();
    }

    public void attach(SerialListener listener) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread())
            throw new IllegalArgumentException("not in main thread");
        //cancelNotification();
        // use synchronized() to prevent new items in queue2
        // new items will not be added to queue1 because mainLooper.post and attach() run in main thread
        synchronized (this) {
            this.listener = listener;
        }
    }
}

