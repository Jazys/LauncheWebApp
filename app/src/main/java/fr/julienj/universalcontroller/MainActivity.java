package fr.julienj.universalcontroller;

import android.Manifest;
import android.app.PendingIntent;
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
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import java.io.IOException;

import fr.julienj.universalcontroller.interfaceclass.SerialListener;
import fr.julienj.universalcontroller.services.BLEService;
import fr.julienj.universalcontroller.services.BluetoothService;
import fr.julienj.universalcontroller.services.SerialService;
import fr.julienj.universalcontroller.services.SerialSocket;
import fr.julienj.universalcontroller.services.WebServerService;
import fr.julienj.universalcontroller.services.WebSocketServerService;
import fr.julienj.universalcontroller.utils.CustomProber;
import fr.julienj.universalcontroller.utils.HexDump;

public class MainActivity extends AppCompatActivity  implements ServiceConnection, SerialListener {

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        if( iBinder.getClass().toString().contains("fr.julienj.universalcontroller.services.SerialService"))
        {
            Log.i(TAG, "Service démarré SerialService");
            service = ((SerialService.SerialBinder) iBinder).getService();
            service.attach(this);
        }
        else if (iBinder.getClass().toString().contains("fr.julienj.universalcontroller.services.WebServerService"))
        {
            Log.i(TAG, "Service démarré WebServerService");
            serviceWeb = ((WebServerService.WebServerBinder) iBinder).getService();
            serviceWeb.startWebServer();
        }
        else if (iBinder.getClass().toString().contains("fr.julienj.universalcontroller.services.WebSocketServerService"))
        {
            Log.i(TAG, "Service démarré WebSocketServerService");
            serviceWSS = ((WebSocketServerService.WebSocketServerBinder) iBinder).getService();
            serviceWSS.startWSS();
        }
        else if (iBinder.getClass().toString().contains("fr.julienj.universalcontroller.services.BluetoothService"))
        {
            Log.i(TAG, "Service démarré BluetoothService");
            serviceBluetooth = ((BluetoothService.BluetoothSocketSerie) iBinder).getService();
            serviceBluetooth.startBluetoothServer();
        }
        else if (iBinder.getClass().toString().contains("fr.julienj.universalcontroller.services.BLEService"))
        {
            Log.i(TAG, "Service démarré serviceBLE");
            serviceBLE = ((BLEService.BLEGatt) iBinder).getService();
            serviceBLE.startConnexion();
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
        String mess="";
        for(int i=0 ; i<data.length; i++){
            mess+= String.valueOf((char)data[i]);
        }
        Log.i(TAG, "Serial USB "+mess);
    }

    @Override
    public void onSerialIoError(Exception e) {
        //disconnectUSBLS();
    }

    private static final String TAG = "Main";
    private enum UsbPermission { Unknown, Requested, Granted, Denied };
    private UsbPermission usbPermission = UsbPermission.Unknown;
    private static final String url = "http://127.0.0.1:9000/index.html";

    private BroadcastReceiver broadcastReceiver;

    private UsbSerialPort usbSerialPort;
    private boolean scanning = false;
    private SerialInputOutputManager usbIoManager;
    private SerialService service;
    private WebServerService serviceWeb;
    private WebSocketServerService serviceWSS;
    private BluetoothService serviceBluetooth;
    private  BLEService serviceBLE;
    private TextView ipTextView;

    private boolean lsConnected=false;

    private boolean isActivityRecreate=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(SingletonApp.getInstance().isActivityRecreate==false)
        {
            SingletonApp.getInstance().isActivityRecreate=true;
            Log.i(TAG, "onCreate");
        }
        else
        {
            Log.i(TAG, "onCreate 2 fois");
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
        bindService(new Intent(this, BLEService.class), this, Context.BIND_AUTO_CREATE);

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
            }
        });

        Button stopServer = (Button) findViewById(R.id.buttonStopServer);
        stopServer.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        //Pour afficher l'addresse IP du téléphone
        ipTextView = (TextView)findViewById(R.id.textView);

        WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));

        ipTextView.setText(ip);

        Log.i(TAG, "ip "+ip);


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
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        if(service != null)
            service.attach(this);
        else
            getApplicationContext().startForegroundService(new Intent(this, SerialService.class));
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        if(service != null)
            service.detach();
        super.onStop();
    }
    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        if (lsConnected != false)
            disconnectUSBLS();
        getApplicationContext().stopService(new Intent(this, SerialService.class));
        getApplicationContext().stopService(new Intent(this, WebServerService.class));
        getApplicationContext().stopService(new Intent(this, WebSocketServerService.class));
        getApplicationContext().stopService(new Intent(this, BluetoothService.class));
        getApplicationContext().stopService(new Intent(this, BLEService.class));
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    public void connectUSBLS()
    {

        UsbManager manager = (UsbManager) getApplicationContext().getSystemService(Context.USB_SERVICE);
        UsbDevice usbXiao=null;
        for(UsbDevice v : manager.getDeviceList().values())
        {
            if(v.getDeviceId() == Constants.DEVICE_ID_XIAO)
            {
                usbXiao=v;
                Log.i(TAG, "Xiao trouvé");
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
                Log.i(TAG, "Pas de permission pour l'USB");
            else
                Log.i(TAG, "Erreur sur l'USB");
            return;
        }

        try {
            usbSerialPort.open(usbConnection);
            usbSerialPort.setParameters(Constants.BAUD_RATE_XIAO, 8, 1, UsbSerialPort.PARITY_NONE);

            SerialSocket socket = new SerialSocket(getApplicationContext(), usbConnection, usbSerialPort);
            service.connect(socket);

            onSerialConnect();

            Log.i(TAG, "Liaison série USB ouverte sur XIAO");
            lsConnected=true;

        } catch (Exception e) {
            Log.w(TAG, "Impossible d'ouvrir la liaison série avec le XIAO");
            disconnectUSBLS();
        }
    }

    private void disconnectUSBLS() {
        lsConnected=false;
        if(usbIoManager != null)
            usbIoManager.stop();
        usbIoManager = null;
        try {
            if(usbSerialPort!=null)
                usbSerialPort.close();
        } catch (IOException ignored) {}
        usbSerialPort = null;
    }
    
}
