package com.carvalho.wushuwatchv3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class nlService extends NotificationListenerService {

    NotificationBroadcastReceiver nbReceiver;
    StatusBarNotification[] sbNotification;


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void onCreate(){
        super.onCreate();
        nbReceiver = new NotificationBroadcastReceiver();
        IntentFilter intFilter = new IntentFilter();
        intFilter.addAction("com.carvalho.wushuwatchv3.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nbReceiver,intFilter, Context.RECEIVER_NOT_EXPORTED);
    }

    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(nbReceiver);
    }

    ArrayList<String> getNotificationAppsNames(){
        ArrayList<String> appsNames = new ArrayList<String>();
        if(sbNotification != null) {
            for (StatusBarNotification nt : sbNotification) {
                appsNames.add(nt.getPackageName());
            }
        } else {
            appsNames.add("Status Bar Null");
        }
        if(appsNames.size() == 0){
            appsNames.add("No notifications added");
        }
        return appsNames;
    }

    public String updateStatusBar(){
        sbNotification = nlService.this.getActiveNotifications();
        String msg = "SBNotification updated, Size: " + String.valueOf(sbNotification.length);
        return msg;
    }

    // Create onNotificationPosted() -- WIP


    class NotificationBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            // Implement clear all notfications -- WIP

            Intent i1 = new Intent("com.carvalho.wushuwatchv3.NOTIFICATION_LISTENER_EXAMPLE");
            i1.putExtra("notification_event","");
            sendBroadcast(i1);
            nlService.this.updateStatusBar();
        }
    }


}
