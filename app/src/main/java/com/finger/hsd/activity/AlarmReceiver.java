package com.finger.hsd.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.finger.hsd.Services.NotificationPlayingService;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String state  = intent.getExtras().getString("extra");
        Log.d("AlarmReceiver","Alarm in Receiver run "  + state );

        Intent serviceIntent = new Intent(context,NotificationPlayingService.class);
        serviceIntent.putExtra("extra", state);
//        serviceIntent.putExtra("request_code", request_code);

        context.startService(serviceIntent);
    }
}