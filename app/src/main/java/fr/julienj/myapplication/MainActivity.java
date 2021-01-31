package fr.julienj.myapplication;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.welie.blessed.BluetoothBytesParser;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.GattStatus;
import com.welie.blessed.WriteType;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.BluetoothCallback;
import me.aflak.bluetooth.interfaces.DeviceCallback;
import me.aflak.bluetooth.interfaces.DiscoveryCallback;

import static android.bluetooth.BluetoothGatt.CONNECTION_PRIORITY_HIGH;

public class MainActivity extends AppCompatActivity  implements ServiceConnection, SerialListener {

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        if( iBinder.getClass().toString().contains("fr.julienj.myapplication.SerialService"))
        {
            service = ((SerialService.SerialBinder) iBinder).getService();
            service.attach(this);
        }
        else if (iBinder.getClass().toString().contains("fr.julienj.myapplication.WebServerService"))
        {
            serviceWeb = ((WebServerService.WebServerBinder) iBinder).getService();
            serviceWeb.startWebServer();
        }
        else if (iBinder.getClass().toString().contains("fr.julienj.myapplication.WebSocketServerService"))
        {
            serviceWSS = ((WebSocketServerService.WebSocketServerBinder) iBinder).getService();
            serviceWSS.startWSS();
        }
        else if (iBinder.getClass().toString().contains("fr.julienj.myapplication.BluetoothService"))
        {
            serviceBluetooth = ((BluetoothService.BluetoothSocketSerie) iBinder).getService();
            serviceBluetooth.startBluetoothServer();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    @Override
    public void onSerialConnect() {

    }

    @Override
    public void onSerialConnectError(Exception e) {

    }

    @Override
    public void onSerialRead(byte[] data) {
        System.out.println("jj size "+data.length);
        System.out.println("jj +"+HexDump.dumpHexString(data));

        String mess="";
        for(int i=0 ; i<data.length; i++){
            mess+= String.valueOf((char)data[i]);
        }
        System.out.println("jj "+mess);


    }

    @Override
    public void onSerialIoError(Exception e) {
        disconnectUSBLS();
    }

    private enum UsbPermission { Unknown, Requested, Granted, Denied };
    private UsbPermission usbPermission = UsbPermission.Unknown;
    private static final String url = "http://127.0.0.1:9000/index.html";
    private Configuration config;
    private SocketIOServer server;

    private Bluetooth bluetooth;
    private ArrayAdapter<String> scanListAdapter;
    private ArrayAdapter<String> pairedListAdapter;
    private List<BluetoothDevice> pairedDevices;
    private List<BluetoothDevice> scannedDevices;

    private BroadcastReceiver broadcastReceiver;

    private BluetoothCentralManager central;
    private UsbSerialPort usbSerialPort;
    private boolean scanning = false;
    private SerialInputOutputManager usbIoManager;
    private SerialService service;
    private WebServerService serviceWeb;
    private WebSocketServerService serviceWSS;
    private BluetoothService serviceBluetooth;
    private TextView ipTextView;

    private boolean lsConnected=false;

    private boolean isActivityRecreate=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isActivityRecreate==false)
        {
            isActivityRecreate=true;
            System.out.println("jj activite cree");
        }
        else
        {
            System.out.println("jj activite deja cree");
        }

        //Pour avoir la fenetre en plein écran
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Pour démarrer le service liée à la connexion en USB
        bindService(new Intent(this, SerialService.class), this, Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, WebServerService.class), this, Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, WebSocketServerService.class), this, Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, BluetoothService.class), this, Context.BIND_AUTO_CREATE);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Constants.INTENT_ACTION_GRANT_USB)) {
                    usbPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                            ? UsbPermission.Granted : UsbPermission.Denied;
                    connectUSBLS();
                }else if(intent.getAction().equals(Constants.USB_ATTACHED)) {
                    connectUSBLS();
                }else if(intent.getAction().equals(Constants.USB_DETTACHED))
                {
                    disconnectUSBLS();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_GRANT_USB));
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.USB_ATTACHED));
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.USB_DETTACHED));


        //Pour demander les permissions
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, 1);

            }
        }

        Button startServeur = (Button) findViewById(R.id.buttonStartServer);
        startServeur.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //TinyWebServer.startServer("0.0.0.0",9000, "", getApplicationContext().getAssets());

            }
        });

        Button stopServer = (Button) findViewById(R.id.buttonStopServer);
        stopServer.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //TinyWebServer.stopServer();


            }
        });

        //Pour afficher l'addresse IP du téléphone
        ipTextView = (TextView)findViewById(R.id.textView);

        WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));

        ipTextView.setText(ip);

        System.out.println("jj "+ip);


        Button startApp = (Button) findViewById(R.id.buttonLaunch);
        startApp.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    Uri uri = Uri.parse("googlechrome://navigate?url=" + url);
                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    // Chrome is probably not installed
                }

            }
        });

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
        BluetoothCentralManager central = new BluetoothCentralManager(getApplicationContext(),bluetoothCentralManagerCallback , new Handler(Looper.getMainLooper()));
        BluetoothPeripheral peripheral = central.getPeripheral("C4:BE:84:1A:C2:07");

     //   central.scanForPeripherals();
        System.out.println("jj test"+peripheral.getName()+": "+peripheral.readRemoteRssi()+ ":"+peripheral.getAddress());
        central.autoConnectPeripheral(peripheral, peripheralCallback);


    }


    @Override
    public void onStart() {
        super.onStart();
        if(service != null)
            service.attach(this);
        else
            getApplicationContext().startForegroundService(new Intent(this, SerialService.class));
       }

    @Override
    public void onStop() {
        if(service != null)
            service.detach();
        super.onStop();
    }
    @Override
    public void onDestroy() {
        if (lsConnected != false)
            disconnectUSBLS();
        getApplicationContext().stopService(new Intent(this, SerialService.class));
        super.onDestroy();
    }

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

            peripheral.writeCharacteristic(UUID.fromString("0000dfb0-0000-1000-8000-00805f9b34fb"), UUID.fromString("0000dfb1-0000-1000-8000-00805f9b34fb"),valueByte,WriteType.WITH_RESPONSE);

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

    public void connectUSBLS()
    {

        UsbManager manager = (UsbManager) getApplicationContext().getSystemService(Context.USB_SERVICE);
        UsbDevice usbXiao=null;
        for(UsbDevice v : manager.getDeviceList().values())
        {
            if(v.getDeviceId() == Constants.DEVICE_ID_XIAO)
            {
                usbXiao=v;
                System.out.println("jj usb usb");
            }
        }

        if(usbXiao==null)
            return ;

        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(usbXiao);
        driver = CustomProber.getCustomProber().probeDevice(usbXiao);

        usbSerialPort = driver.getPorts().get(0);
        UsbDeviceConnection usbConnection = manager.openDevice(driver.getDevice());
        if(usbConnection == null && usbPermission == UsbPermission.Unknown && !manager.hasPermission(driver.getDevice())) {
            usbPermission = UsbPermission.Requested;
            PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(Constants.INTENT_ACTION_GRANT_USB), 0);
            manager.requestPermission(driver.getDevice(), usbPermissionIntent);
            return;
        }
        if(usbConnection == null) {
            if (!manager.hasPermission(driver.getDevice()))
                System.out.println("jj usb conn test");
            else
                System.out.println("jj usb notconntest");
            return;
        }

        try {
            usbSerialPort.open(usbConnection);
            usbSerialPort.setParameters(Constants.BAUD_RATE_XIAO, 8, 1, UsbSerialPort.PARITY_NONE);

            SerialSocket socket = new SerialSocket(getApplicationContext(), usbConnection, usbSerialPort);
            service.connect(socket);
            // usb connect is not asynchronous. connect-success and connect-error are returned immediately from socket.connect
            // for consistency to bluetooth/bluetooth-LE app use same SerialListener and SerialService classes
            onSerialConnect();

            System.out.println("jj right");
            lsConnected=true;

        } catch (Exception e) {
            System.out.println("jj "+e);
            disconnectUSBLS();
        }
    }

    private void disconnectUSBLS() {
        lsConnected=false;
        if(usbIoManager != null)
            usbIoManager.stop();
        usbIoManager = null;
        try {
            usbSerialPort.close();
        } catch (IOException ignored) {}
        usbSerialPort = null;
    }
}
