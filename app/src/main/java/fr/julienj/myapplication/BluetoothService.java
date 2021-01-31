package fr.julienj.myapplication;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.net.UnknownHostException;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.DeviceCallback;

public class BluetoothService extends Service {

    private final IBinder mBinder = new BluetoothSocketSerie();
    private Bluetooth bluetooth;

    public class BluetoothSocketSerie extends Binder {
        BluetoothService getService() {
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
        System.out.println("jj " +bluetooth.getPairedDevices());
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
                System.out.println("jj "+mess);
                bluetooth.send(mess+"\r\n");
            }

            @Override
            public void onError(int errorCode) {

            }

            @Override public void onConnectError(BluetoothDevice device, String message) {}
        });

    }

    public void stopBluetoothServer() {
        bluetooth.disconnect();

    }

    @Override
    public void onDestroy() {
        stopBluetoothServer();
        super.onDestroy();
    }
}

