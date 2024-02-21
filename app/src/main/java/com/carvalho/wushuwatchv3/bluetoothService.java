package com.carvalho.wushuwatchv3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class bluetoothService extends Service {
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    BluetoothAdapter blAdapter;
    BluetoothDevice wushuWatch = null;
    BluetoothSocket blSocket;
    bluetoothHandler blHandler = null;
    Context context;
    TextView statusService;
    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { //WIP
        blAdapter = BluetoothAdapter.getDefaultAdapter();
        context = getApplicationContext();
        if (blAdapter.isEnabled()) {

            @SuppressLint("MissingPermission") Set<BluetoothDevice> devices = blAdapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                if (device.getName().equals("Relogio_ESP32")) {
                    wushuWatch = device;
                }
            }
        }

        try {
            blSocket = wushuWatch.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            blSocket.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // create thread to keep connecting device
        blHandler = new bluetoothHandler(blSocket, context);
        blHandler.start();

        // create notification
        final String CHANNELID = "FOREGROUNG SERVICE ID";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNELID,
                    CHANNELID,
                    NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(context, CHANNELID)
                    .setContentText("Service is running")
                    .setContentTitle("WushuWatchV3")
                    .setSmallIcon(R.drawable.ic_launcher_background);

            startForeground(1001, notification.build());

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    // WIP


}
