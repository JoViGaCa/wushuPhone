package com.carvalho.wushuwatchv3;

import static android.service.controls.ControlsProviderService.TAG;

import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.room.Room;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

public class bluetoothHandler extends Thread {

    private final BluetoothSocket bhSocket;
    private final InputStream bhInStream;
    private final OutputStream bhOutStream;
    private byte[] mmBuffer; // mmBuffer store for the stream
    private int buffSize = 1024;
    Context context;
    String msg;
    String[] arr;
    Date currentTime;

    Schedule sch;
    dbHandler db;
    ScheduleDAO schDAO;


    public bluetoothHandler(BluetoothSocket bhSocket, Context context) {
        db = Room.databaseBuilder(context, dbHandler.class, "Schedule-Database").allowMainThreadQueries().build();

        schDAO = db.schDAO();

        this.context = context;
        this.bhSocket = bhSocket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = bhSocket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = bhSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }
        bhInStream = tmpIn;
        bhOutStream = tmpOut;
    }


    public void run() {
        mmBuffer = new byte[buffSize];
        int numBytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                if (bhSocket.isConnected()) {
                    numBytes = bhInStream.read(mmBuffer, 0, buffSize);
                    msg = new String(mmBuffer, StandardCharsets.UTF_8);
                    arr = msg.split(":");
                    if (arr[0].equals("00000001")) {
                        currentTime = Calendar.getInstance().getTime();
                        //
                        write(currentTime.toString().getBytes());
                    } else if (arr[0].equals("00000010")) {
                        //create new schedule
                        sch = new Schedule();
                        sch.evento = arr[1];
                        sch.dia = arr[2];
                        sch.mes = arr[3];
                        sch.hora = arr[4];
                        sch.min = arr[5];

                        //save schedule
                        db.schDAO().insertSchedule(sch);
                    } else if (arr[0].equals("00000011")) {
                        List<Schedule> list = db.schDAO().getByDay(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                        StringBuilder aux = new StringBuilder();
                        for (Schedule sch : list) {
                            aux.append(sch.evento).append(":").append(sch.hora).append(":").append(sch.min).append("|");
                        }
                        this.write(aux.toString().getBytes());

                    }

                }
                // Send the obtained bytes to the UI activity.
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }

    public String getMsg() {
        return arr[0];
    }

    public void write(byte[] bytes) {
        //showToast(mmOutStream.toString());
        if (bhOutStream != null) {
            try {
                bhOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("MainActivity", "Error occurred when sending data", e);
            }
        }
    }

    public String getSchedules() {
        StringBuilder aux = new StringBuilder();
        List<Schedule> schedules = schDAO.getAll();
        for (Schedule sch : schedules) {
            aux.append("|").append(sch.evento).append(":").append(sch.dia);
        }
        if (aux.toString() == "") {
            return "aux is null";
        } else {
            return aux.toString();
        }
    }
}

