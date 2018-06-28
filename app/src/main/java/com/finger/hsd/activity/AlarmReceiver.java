package com.finger.hsd.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.finger.hsd.services.NotificationPlayingService;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String state  = intent.getExtras().getString("extra");
        Log.d("AlarmReceiver","Alarm in Receiver run "  + state );

        Intent serviceIntent = new Intent(context,NotificationPlayingService.class);
        serviceIntent.putExtra("extra", state);
//        serviceIntent.putExtra("request_code", request_code);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            AlarmReceiver.this.startForegroundService(serviceIntent);
            context.startForegroundService(serviceIntent);
//            context.startForegroundService(getSyncIntent(context));
//            ContextCompat.startForegroundService(context, serviceIntent);
        } else {
//            context.startService(new Intent(context, ServedService.class));
            context.startService(serviceIntent);
        }


    }



//    public void onReceive(final Context context, final Intent intent) {
//        final PendingResult result = goAsync();
//        wl.acquire();
//        AsyncHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                handleIntent(context, intent);//耗时操作
//                result.finish();
//            }
//        });
//    }


    private void onCallEnd(Context context) {
        context.stopService(new Intent(context, NotificationPlayingService.class));
    }
}