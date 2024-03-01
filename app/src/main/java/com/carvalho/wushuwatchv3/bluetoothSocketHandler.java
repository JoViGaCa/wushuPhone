package com.carvalho.wushuwatchv3;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class bluetoothSocketHandler  extends Thread{
    BluetoothAdapter blAdapter;
    BluetoothDevice device;
    bluetoothHandler blHandler = null;
    Context context;
    BluetoothServerSocket blServerSocket;
    BluetoothSocket blSocket = null;
    BluetoothServerSocket blServer;
    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @SuppressLint("MissingPermission")
    public bluetoothSocketHandler(BluetoothAdapter adapter, Context context){
        this.context = context;
        this.blAdapter = adapter;
        BluetoothServerSocket temporary_socket;

        // criar socket
        try {
            temporary_socket = blAdapter.listenUsingInsecureRfcommWithServiceRecord("WhushuPhone",uuid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        blServer = temporary_socket;
    }

    @SuppressLint("MissingPermission")
    public void run() {
        while(true) {
            try {
                Log.i(TAG,"Starting accept");
                blSocket = blServer.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (blSocket != null) {
                Log.i(TAG,"Socket created");

                blHandler = new bluetoothHandler(blSocket,context);
                blHandler.start();

            }

        }


    }
}
