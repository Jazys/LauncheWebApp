package fr.julienj.myapplication;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;

import com.welie.blessed.BluetoothBytesParser;
import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.GattStatus;
import com.welie.blessed.WriteType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.DeviceCallback;

import static android.bluetooth.BluetoothGatt.CONNECTION_PRIORITY_HIGH;

public class BLEService extends Service {

    private final IBinder mBinder = new BLEGatt();

    private BluetoothCentralManager central;
    private BluetoothPeripheral peripheral;

    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered(BluetoothPeripheral peripheral) {
            // Request a higher MTU, iOS always asks for 185

            BluetoothGattCharacteristic aCharacteristic = null;
            peripheral.requestMtu(185);

            peripheral.requestConnectionPriority(CONNECTION_PRIORITY_HIGH);

            System.out.println("jj test1 "+peripheral.getName());
            System.out.println("jj test1 "+peripheral.getServices());

            for (int i=0;i<peripheral.getServices().size();i++)
            {
                BluetoothGattService ble= peripheral.getServices().get(i);
                System.out.println("jj -- "+ble.getUuid());

                for(int z=0;z<ble.getCharacteristics().size();z++)
                {
                    BluetoothGattCharacteristic characteristic=ble.getCharacteristics().get(z);
                    System.out.println("jj car "+characteristic.getUuid().toString()+ ": "+characteristic.getWriteType());
                    if(characteristic.getUuid().toString().equalsIgnoreCase("0000dfb1-0000-1000-8000-00805f9b34fb"))
                    {
                        aCharacteristic=characteristic;
                    }
                    //characteristic.
                }

                // BluetoothGattCharacteristic characteristic= new BluetoothGattCharacteristic()
            }

            //uuid service 00001800-0000-1000-8000-00805f9b34fb
            //character uuid 00002a02-0000-1000-8000-00805f9b34fb
            UUID service= UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");
            UUID car= UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");
            System.out.println("jj readcar" +peripheral.readCharacteristic(service,car));

            System.out.println("jj readcar" +peripheral.readCharacteristic(UUID.fromString("0000dfb0-0000-1000-8000-00805f9b34fb"), UUID.fromString("0000dfb1-0000-1000-8000-00805f9b34fb")));

            byte[] valueByte= new byte[1];
            valueByte[0]=0x25;

            BluetoothGattCharacteristic currentTimeCharacteristic = peripheral.getCharacteristic(UUID.fromString("0000dfb0-0000-1000-8000-00805f9b34fb"), UUID.fromString("0000dfb1-0000-1000-8000-00805f9b34fb"));
            if (currentTimeCharacteristic != null) {
                peripheral.setNotify(currentTimeCharacteristic, true);
            }

            peripheral.setNotify(UUID.fromString("0000dfb0-0000-1000-8000-00805f9b34fb"), UUID.fromString("0000dfb2-0000-1000-8000-00805f9b34fb"),true);

            peripheral.writeCharacteristic(UUID.fromString("0000dfb0-0000-1000-8000-00805f9b34fb"), UUID.fromString("0000dfb1-0000-1000-8000-00805f9b34fb"),valueByte, WriteType.WITH_RESPONSE);

            peripheral.setNotify(aCharacteristic,true);
            System.out.println("jj testisnoti"+peripheral.isNotifying(aCharacteristic));

            //peripheral.setNotify(aCharacteristic,false);




        }

        @Override
        public void onNotificationStateUpdate(BluetoothPeripheral peripheral, BluetoothGattCharacteristic characteristic, GattStatus status) {
            System.out.println("jj test2");

        }

        @Override
        public void onCharacteristicWrite(BluetoothPeripheral peripheral, byte[] value, BluetoothGattCharacteristic characteristic, GattStatus status) {
            System.out.println("jj test3");

        }

        @Override
        public void onCharacteristicUpdate(BluetoothPeripheral peripheral, byte[] value, BluetoothGattCharacteristic characteristic, GattStatus status) {
            BluetoothBytesParser parser = new BluetoothBytesParser(value);

            String mess="";
            for(int i=0 ; i<value.length; i++){
                mess+= String.valueOf((char)value[i]);
            }
            System.out.println("jj test4 "+mess);
            System.out.println("jj test4 "+parser.getStringValue(BluetoothBytesParser.FORMAT_SINT8));


        }

        @Override
        public void onMtuChanged(BluetoothPeripheral peripheral, int mtu, GattStatus status) {
            System.out.println("jj test5");
            peripheral.setNotify(UUID.fromString("0000dfb0-0000-1000-8000-00805f9b34fb"), UUID.fromString("0000dfb2-0000-1000-8000-00805f9b34fb"),true);


        }

        private void sendMeasurement(Intent intent, BluetoothPeripheral peripheral ) {
            System.out.println("jj test6");

        }

        private void writeContourClock(BluetoothPeripheral peripheral) {
            System.out.println("jj test7");

        }

        private void writeGetAllGlucoseMeasurements(BluetoothPeripheral peripheral) {
            System.out.println("jj test8");

        }
    };


    public class BLEGatt extends Binder {
        BLEService getService() {
            return BLEService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startConnexion()
    {
        BluetoothCentralManagerCallback bluetoothCentralManagerCallback = new BluetoothCentralManagerCallback() {
            @Override
            public void onDiscoveredPeripheral(BluetoothPeripheral peripheral, ScanResult scanResult) {
                System.out.println("jj test");
                central.stopScan();

                List<ParcelUuid> parcelUuids = scanResult.getScanRecord().getServiceUuids();

                List<UUID> serviceList = new ArrayList<>();

                for (int i = 0; i < parcelUuids.size(); i++)
                {
                    UUID serviceUUID = parcelUuids.get(i).getUuid();

                    System.out.println("jj "+serviceUUID.toString());

                    if (!serviceList.contains(serviceUUID))
                        serviceList.add(serviceUUID);
                }


                //central.connectPeripheral(peripheral, peripheralCallback);
            }
        };

// Create BluetoothCentral and receive callbacks on the main thread
        central = new BluetoothCentralManager(getApplicationContext(),bluetoothCentralManagerCallback , new Handler(Looper.getMainLooper()));
        central.scanForPeripherals();
        peripheral = central.getPeripheral(Constants.MAC_BLUNO);


        //   central.scanForPeripherals();
        System.out.println("jj test"+peripheral.getName()+": "+peripheral.readRemoteRssi()+ ":"+peripheral.getAddress());
        central.autoConnectPeripheral(peripheral, peripheralCallback);


    }

    public void stopBluetoothServer() {
        central.cancelConnection(peripheral);
    }

    @Override
    public void onDestroy() {
        stopBluetoothServer();
        super.onDestroy();
    }
}
