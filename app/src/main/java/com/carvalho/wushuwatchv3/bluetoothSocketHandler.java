package com.carvalho.wushuwatchv3;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
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
    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @SuppressLint("MissingPermission")
    public bluetoothSocketHandler(BluetoothDevice device, Context context){
        this.context = context;
        this.device = device;
//        this.blAdapter = blAdapter;

        // criar socket
        try {
            blSocket = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @SuppressLint("MissingPermission")
    public void run() {
        while(true) {
            try {
                blSocket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                blSocket.connect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            blHandler = new bluetoothHandler(blSocket,context);
            blHandler.start();

            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }


    }
}
