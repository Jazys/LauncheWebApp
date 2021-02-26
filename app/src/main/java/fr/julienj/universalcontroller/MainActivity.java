package fr.julienj.universalcontroller;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import fr.julienj.universalcontroller.utils.Utils;

//https://abhiandroid.com/ui/gridview
//http://javamind-fr.blogspot.com/2013/05/gridlayout-pour-creer-des-tableaux-ou.html

//Faire du propre
//Faire des toast pour indiquer que les service démarre
//Faire changer les couleurs des bouttons en fonction des états des services et griser le bouton pas possible
//Faire des notifications pour le service Bluetooth
//Faire un bouton pour lancer serveur + bluetooth
//Collapse des cellules pour centrage

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "Main";
    private final String PREF_NAME="GameServeController";
    private final String sharePrefUsbDevice="usbdeviceID";
    private final String sharePrefM1BT="m1BTMac";
    private final String sharePrefM2BT="m2BTMac";

    private enum UsbPermission { Unknown, Requested, Granted, Denied };
    private UsbPermission usbPermission = UsbPermission.Unknown;
    private static final String url = "http://127.0.0.1:9000/index.html";

    private BroadcastReceiver broadcastReceiver;

    private UsbSerialPort usbSerialPort;
    private boolean scanning = false;
    private SerialInputOutputManager usbIoManager;
    private SerialService serviceUSB;
    private WebServerService serviceWeb;
    private WebSocketServerService serviceWSS;
    private BluetoothService serviceBluetooth;
    private  BLEService serviceBLE;

    private ServiceConnection serviceSerailSC;
    private ServiceConnection serviceWebSC;
    private ServiceConnection serviceWSSSC;
    private ServiceConnection serviceBluetoothSC;
    private  ServiceConnection serviceBLESC;
    private TextView ipTextView;
    private TextView lastDataSerial;
    private Handler handler = new Handler();
    private SerialListener serialListenerUsb;
    private SerialListener serialListenerBluetooth;


    private boolean lsConnected=false;
    SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences(PREF_NAME, 0);

        CreateServiceConnexion();
        if(SingletonApp.getInstance().isActivityRecreate==false)
        {
            SingletonApp.getInstance().isActivityRecreate=true;
            Log.i(TAG, "onCreate");
        }
        else
        {
            Log.i(TAG, "onCreate 2 fois");
        }

        //Pour démarrer le service liée à la connexion en USB
        //bindService(new Intent(this, SerialService.class), serviceSerailSC, Context.BIND_AUTO_CREATE);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Constants.INTENT_ACTION_GRANT_USB)) {
                    usbPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                            ? UsbPermission.Granted : UsbPermission.Denied;
                    connectUSBLS();
                }else if(intent.getAction().equals(Constants.USB_ATTACHED)) {
                    //bindService(new Intent(getApplication(), SerialService.class), serviceSerailSC, Context.BIND_AUTO_CREATE);

                }else if(intent.getAction().equals(Constants.USB_DETTACHED))
                {
                    //disconnectUSBLS();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_GRANT_USB));
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.USB_ATTACHED));
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.USB_DETTACHED));

        //Pour avoir la fenetre en plein écran
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

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


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        if(serviceUSB != null)
            serviceUSB.attach(serialListenerUsb);

        if(serviceBluetooth!=null)
            serviceBluetooth.attach(serialListenerBluetooth);

        //else
        //    getApplicationContext().startForegroundService(new Intent(this, SerialService.class));

        final Button config= (Button) findViewById(R.id.buttonConfig);
        final Button startServeur = (Button) findViewById(R.id.buttonStartServer);
        final Button stopServer = (Button) findViewById(R.id.buttonStopServer);
        final Button startUSBSerie=(Button) findViewById(R.id.buttonStartUSBSerie);
        final Button stopUSBSerie=(Button) findViewById(R.id.buttonStopUSBSerie);
        final Button startBluetooth = (Button) findViewById(R.id.buttonLaunchBluetooth);
        final Button stopBluetooth = (Button) findViewById(R.id.buttonStopBluetooth);
        final Button startServiceJeu = (Button) findViewById(R.id.buttonstartservicejeu);
        final Button stopServiceJeu = (Button) findViewById(R.id.buttonstoptservicejeu);

        stopServer.setEnabled(false);
        stopUSBSerie.setEnabled(false);
        stopBluetooth.setEnabled(false);
        stopServiceJeu.setEnabled(false);

        startServeur.setBackgroundColor(Color.GREEN);
        startUSBSerie.setBackgroundColor(Color.GREEN);
        startBluetooth.setBackgroundColor(Color.GREEN);
        startServiceJeu.setBackgroundColor(Color.GREEN);



        if(serviceUSB!=null && serviceUSB.isRunning) {
            startUSBSerie.setEnabled(false);
            stopUSBSerie.setEnabled(true);
            startUSBSerie.setBackgroundColor(Color.GRAY);
        }

        if(serviceBluetooth!=null && serviceBluetooth.isRunning) {
            startBluetooth.setEnabled(false);
            stopBluetooth.setEnabled(true);
            startBluetooth.setBackgroundColor(Color.GRAY);
        }

        if(serviceWeb!=null && serviceWeb.isRunning) {
            startServeur.setEnabled(false);
            stopServer.setEnabled(true);
            startServeur.setBackgroundColor(Color.GRAY);
            stopServiceJeu.setEnabled(true);
        }

        config.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayConfigBox();
            }
        });

        startServeur.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(serviceWeb==null || (serviceWeb!=null && !serviceWeb.isRunning))
                    bindService(new Intent(getApplication(), WebServerService.class), serviceWebSC, Context.BIND_AUTO_CREATE);
                if(serviceWSS==null || (serviceWSS!=null && !serviceWSS.isRunning))
                    bindService(new Intent(getApplication(), WebSocketServerService.class), serviceWSSSC, Context.BIND_AUTO_CREATE);

                startServeur.setEnabled(false);
                startServeur.setBackgroundColor(Color.GRAY);
                stopServer.setEnabled(true);
                stopServer.setBackgroundColor(Color.RED);
            }
        });


        stopServer.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(serviceWeb!=null && serviceWeb.isRunning)
                {
                    unbindService(serviceWebSC);
                    getApplicationContext().stopService(new Intent(getApplication(), WebServerService.class));
                }

                if(serviceWSS!=null && serviceWSS.isRunning)
                {
                    unbindService(serviceWSSSC);
                    getApplicationContext().stopService(new Intent(getApplication(), WebSocketServerService.class));
                }

                startServeur.setEnabled(true);
                stopServer.setEnabled(false);
                startServeur.setBackgroundColor(Color.GREEN);
                stopServer.setBackgroundColor(Color.GRAY);

            }
        });

        //Pour afficher l'addresse IP du téléphone
        ipTextView = (TextView)findViewById(R.id.textView);

        WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));

        ipTextView.setText("Adresse Ip du téléphone "+ip);

        Log.i(TAG, "Adresse Ip du téléphone "+ip);

        lastDataSerial = (TextView)findViewById(R.id.SerialText);
        lastDataSerial.setText("Données :");


        startUSBSerie.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(serviceUSB==null || (serviceUSB!=null && !serviceUSB.isRunning))
                    bindService(new Intent(getApplication(), SerialService.class), serviceSerailSC, Context.BIND_AUTO_CREATE);

                startUSBSerie.setEnabled(false);
                stopUSBSerie.setEnabled(true);
                stopUSBSerie.setBackgroundColor(Color.RED);
                startUSBSerie.setBackgroundColor(Color.GRAY);

            }
        });


        stopUSBSerie.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(serviceUSB!=null && serviceUSB.isRunning)
                {
                    unbindService(serviceSerailSC);
                    getApplicationContext().stopService(new Intent(getApplication(), SerialService.class));
                }

                startUSBSerie.setEnabled(true);
                stopUSBSerie.setEnabled(false);
                startUSBSerie.setBackgroundColor(Color.GREEN);
                stopUSBSerie.setBackgroundColor(Color.GRAY);

            }
        });

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


        startBluetooth.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(serviceBluetooth==null || (serviceBluetooth!=null && !serviceBluetooth.isRunning))
                    bindService(new Intent(getApplication(), BluetoothService.class), serviceBluetoothSC, Context.BIND_AUTO_CREATE);

                if(serviceBLE==null || (serviceBLE!=null && !serviceBLE.isRunning))
                    bindService(new Intent(getApplication(), BLEService.class), serviceBLESC, Context.BIND_AUTO_CREATE);

                startBluetooth.setEnabled(false);
                stopBluetooth.setEnabled(true);
                stopBluetooth.setBackgroundColor(Color.RED);
                startBluetooth.setBackgroundColor(Color.GRAY);
            }
        });


        stopBluetooth.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(serviceBluetooth!=null && serviceBluetooth.isRunning) {
                    unbindService(serviceBluetoothSC);
                    getApplicationContext().stopService(new Intent(getApplication(), BluetoothService.class));
                }

                if(serviceBLE!=null && serviceBLE.isRunning) {
                    unbindService(serviceBLESC);
                    getApplicationContext().stopService(new Intent(getApplication(), BLEService.class));
                }

                startBluetooth.setEnabled(true);
                stopBluetooth.setEnabled(false);
                startBluetooth.setBackgroundColor(Color.GREEN);
                stopBluetooth.setBackgroundColor(Color.GRAY);
            }
        });

        startServiceJeu.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startServeur.performClick();
                startBluetooth.performClick();

                startServiceJeu.setEnabled(false);
                stopServiceJeu.setEnabled(true);
                startServiceJeu.setBackgroundColor(Color.GRAY);
                stopServiceJeu.setBackgroundColor(Color.RED);
            }
        });

        stopServiceJeu.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopServer.performClick();
                stopBluetooth.performClick();

                stopServiceJeu.setEnabled(false);
                startServiceJeu.setEnabled(true);
                stopServiceJeu.setBackgroundColor(Color.GRAY);
                startServiceJeu.setBackgroundColor(Color.GREEN);
            }
        });



        UsbManager manager = (UsbManager) getApplicationContext().getSystemService(Context.USB_SERVICE);

        for(UsbDevice v : manager.getDeviceList().values()) {
            handler.post(new Runnable() {
                @Override
                public void run() {

                    lastDataSerial.setText("Info USB : \n" + v.getManufacturerName() + "\n" + v.getProductName() + "\n" +
                            v.getProductId() + "\n" + v.getVendorId() + "\n Numero Device " + v.getDeviceId());
                    lastDataSerial.invalidate();
                    lastDataSerial.requestLayout();
                }
            });
        }

    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        if(serviceUSB != null)
            serviceUSB.detach();
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

        unregisterReceiver(broadcastReceiver);
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

    private void CreateServiceConnexion()
    {
        serviceSerailSC=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i(TAG, "Service démarré SerialService");
                serviceUSB = ((SerialService.SerialBinder) iBinder).getService();
                serviceUSB.attach(serialListenerUsb);
                connectUSBLS();

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(), "Démarrage du service liaison série", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                serviceUSB.detach();
                serviceUSB.disconnect();
                disconnectUSBLS();

            }
        };

        serviceWebSC =new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i(TAG, "Service démarré WebServerService");
                serviceWeb = ((WebServerService.WebServerBinder) iBinder).getService();
                serviceWeb.startWebServer();

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(), "Démarrage du service WebServerServiced", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                serviceWeb.stopWebServer();
            }
        };

        serviceWSSSC=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i(TAG, "Service démarré WebSocketServerService");
                serviceWSS = ((WebSocketServerService.WebSocketServerBinder) iBinder).getService();
                serviceWSS.startWSS();

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(), "Démarrage du service WebSocketServerService", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                serviceWSS.startWSS();
            }
        };

       serviceBluetoothSC= new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i(TAG, "Service démarré BluetoothService");
                serviceBluetooth = ((BluetoothService.BluetoothSocketSerie) iBinder).getService();
                serviceBluetooth.startBluetoothServer(settings.getString(sharePrefM1BT, ""),settings.getString(sharePrefM2BT, "") );
                serviceBluetooth.attach(serialListenerBluetooth);

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(), "Démarrage du service BluetoothService", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                serviceBluetooth.stopBluetoothServer();
            }
        };

        serviceBLESC= new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i(TAG, "Service démarré serviceBLE");
                serviceBLE = ((BLEService.BLEGatt) iBinder).getService();
                serviceBLE.startConnexion();

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(), "Démarrage du service serviceBLE", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                serviceBLE.stopBluetoothServer();
            }
        };

        serialListenerUsb= new SerialListener() {
            @Override
            public void onSerialConnect() {

            }

            @Override
            public void onSerialConnectError(Exception e) {

            }

            @Override
            public void onSerialRead(byte[] data) {
                final String dataStr= Utils.getMessageByte(data);

                Log.i(TAG, "Serial USB "+dataStr);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (lastDataSerial != null && dataStr.length()>2)
                        {
                            lastDataSerial.setText("Last data "+dataStr);
                            lastDataSerial.invalidate();
                            lastDataSerial.requestLayout();
                        }

                    }});
            }

            @Override
            public void onSerialIoError(Exception e) {

            }
        };

        serialListenerBluetooth= new SerialListener() {
            @Override
            public void onSerialConnect() {

            }

            @Override
            public void onSerialConnectError(Exception e) {

            }

            @Override
            public void onSerialRead(byte[] data) {
                final String dataStr= Utils.getMessageByte(data);
                Log.i(TAG, "Serial Bluetooth "+dataStr);

                if(serviceWSS!=null && serviceWSS.isRunning)
                    serviceWSS.sendMessage(dataStr);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (lastDataSerial != null && dataStr.length()>2)
                        {
                            lastDataSerial.setText("Last data BT "+dataStr);
                            lastDataSerial.invalidate();
                            lastDataSerial.requestLayout();
                            Log.i(TAG, "Serial Bluetooth");
                        }

                    }});
            }

            @Override
            public void onSerialIoError(Exception e) {

            }
        };


    }

    public void connectUSBLS()
    {

        UsbManager manager = (UsbManager) getApplicationContext().getSystemService(Context.USB_SERVICE);
        UsbDevice usbXiao=null;
        for(UsbDevice v : manager.getDeviceList().values())
        {
            Log.i(TAG, "Xiao trouvé via sharePref "+settings.getString(sharePrefUsbDevice, "0"));
            if(v.getDeviceId() == Integer.parseInt(settings.getString(sharePrefUsbDevice, "0")))
            {
                usbXiao=v;
                Log.i(TAG, "Xiao trouvé via sharePref");
                break;
            }else if(v.getDeviceId() == Constants.DEVICE_ID_XIAO)
            {
                usbXiao=v;
                Log.i(TAG, "Xiao trouvé");
                break;
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
            serviceUSB.connect(socket);

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

    private void displayConfigBox()
    {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.config_box, null);



        final EditText editTextUSB = (EditText) dialogView.findViewById(R.id.configusbdevice);
        final EditText editTextM1 = (EditText) dialogView.findViewById(R.id.configbluetooth1);
        final EditText editTextM2 = (EditText) dialogView.findViewById(R.id.configbluetooth2);

        editTextUSB.setText(settings.getString(sharePrefUsbDevice, ""));
        editTextM1.setText(settings.getString(sharePrefM1BT, ""));
        editTextM2.setText(settings.getString(sharePrefM2BT, ""));

        Button button1 = (Button) dialogView.findViewById(R.id.buttonSubmit);
        Button button2 = (Button) dialogView.findViewById(R.id.buttonCancel);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);

                if(editTextUSB.getText().toString()!="")
                {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(sharePrefUsbDevice, editTextUSB.getText().toString());
                    // Commit the edits!
                    editor.commit();
                }
                if(editTextM1.getText().toString()!="")
                {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(sharePrefM1BT, editTextM1.getText().toString());
                    // Commit the edits!
                    editor.commit();
                }
                if(editTextM2.getText().toString()!="")
                {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(sharePrefM2BT, editTextM2.getText().toString());
                    // Commit the edits!
                    editor.commit();
                }

                dialogBuilder.dismiss();

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
    
}
