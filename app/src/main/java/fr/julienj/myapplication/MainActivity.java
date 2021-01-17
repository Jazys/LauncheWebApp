package fr.julienj.myapplication;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String url = "http://127.0.0.1:9000/index.html";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        Button startServeur = (Button) findViewById(R.id.buttonStartServer);
        startServeur.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TinyWebServer.startServer("0.0.0.0",9000, "", getApplicationContext().getAssets());

            }
        });

        Button stopServer = (Button) findViewById(R.id.buttonStopServer);
        stopServer.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TinyWebServer.stopServer();


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

    }
}
