package fr.julienj.universalcontroller.services;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import fr.julienj.universalcontroller.Constants;
import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.DeviceCallback;

public class BluetoothService extends Service {

    private static final String TAG = "BluetoothService";

    private final IBinder mBinder = new BluetoothSocketSerie();
    private Bluetooth bluetooth;

    public class BluetoothSocketSerie extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startBluetoothServer()
    {
        bluetooth = new Bluetooth(getApplicationContext());
        bluetooth.onStart();
        Log.i(TAG, "startBluetoothServer "+bluetooth.getPairedDevices());

        //bluetooth.connectToAddress("2C:33:7A:26:40:A6");
        bluetooth.connectToName(Constants.NAME_BLUETOOTH);
        //bluetooth.startScanning();

        bluetooth.setDeviceCallback(new DeviceCallback() {
            @Override public void onDeviceConnected(BluetoothDevice device) {}
            @Override public void onDeviceDisconnected(BluetoothDevice device, String message) {}
            @Override public void onMessage(byte[] message) {
                String mess="";
                for(int i=0 ; i<message.length; i++){
                    mess+= String.valueOf((char)message[i]);
                }
                Log.i(TAG, "onMessage "+mess);
                bluetooth.send(mess+"\r\n");
            }

            @Override
            public void onError(int errorCode) {

            }

            @Override public void onConnectError(BluetoothDevice device, String message) {}
        });

    }

    public void stopBluetoothServer() {
        if(bluetooth!=null && bluetooth.isConnected())
            bluetooth.disconnect();

    }

    @Override
    public void onDestroy() {
        stopBluetoothServer();
        super.onDestroy();
    }
}

