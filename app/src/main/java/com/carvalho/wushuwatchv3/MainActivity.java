package com.carvalho.wushuwatchv3;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    Context context;
    // Buttons Used
    Button turnOnOff, serviceStart, serviceStop;
    // Text views Used
    TextView statusBL, statusService;
    BluetoothAdapter blAdapter;
    BluetoothDevice wushuWatch = null;
    BluetoothSocket blSocket;
    bluetoothHandler blHandler = null;
    nlService nlS;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        turnOnOff = findViewById(R.id.buttonOnOff);
        serviceStart = findViewById(R.id.buttonStartService);
        serviceStop = findViewById(R.id.buttonStopService);


        statusBL = findViewById(R.id.textBTStatus);
        statusService = findViewById(R.id.textStartService);

        blAdapter = BluetoothAdapter.getDefaultAdapter();
        int requestCode = 1;
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
        startActivityForResult(discoverableIntent, requestCode);
        context = getApplicationContext();
        nlS = new nlService();

        if (blAdapter != null) {
            if (blAdapter.isEnabled()) {
                statusBL.setText("Bluetooth is current: On");
                turnOnOff.setText("Turn Off");
            } else {
                statusBL.setText("Bluetooth is current: Off");
                turnOnOff.setText("Turn On");
            }
        } else {
            statusBL.setText("Bluetooth not available");
        }

        if (foregroundServiceRunning()) {
            statusService.setText("Service Running");
        } else {
            statusService.setText("Service not Running");
        }


        turnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blAdapter != null) {
                    if (blAdapter.isEnabled()) {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                        }
                        blAdapter.disable();
                        statusBL.setText("Bluetooth is current: Off");
                        turnOnOff.setText("Turn On");
                    } else {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                        }
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, REQUEST_ENABLE_BT);
                        statusBL.setText("Bluetooth is current: On");
                        turnOnOff.setText("Turn Off");
                    }
                } else {
                    statusBL.setText("Bluetooth not available");
                }
            }
        });

        serviceStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blAdapter != null) {
                    if (blAdapter.isEnabled()) {
                        if (!foregroundServiceRunning()) {
                            Intent serviceIntent = new Intent(context, bluetoothService.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                showToast("Initiating Service");
                                startForegroundService(serviceIntent);
                                statusService.setText("Service Running");
                            } else {
                                showToast("SDK vertion is too low");
                            }

                        } else {
                            showToast("Service already running");
                        }
                    } else {
                        showToast("Bluetooth is off");
                    }
                } else {
                    showToast("Bluetooth not available");
                }
            }
        });

        serviceStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(context, bluetoothService.class);
                stopService(serviceIntent);
                if (foregroundServiceRunning()) {
                    statusService.setText("Service Running");
                } else {
                    statusService.setText("Service not Running");
                }
            }
        });


    }

    public boolean foregroundServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (bluetoothService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (requestCode == RESULT_OK) {
                    statusBL.setText("Bluetooth is current: On");
                    turnOnOff.setText("Turn Off");
                }
        }
    }


}
