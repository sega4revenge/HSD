@file:Suppress("DEPRECATION")

package com.finger.hsd.activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.finger.hsd.services.NotificationPlayingService




class AlarmReceiver : BroadcastReceiver() {
//    var br: BroadcastReceiver = AlarmReceiver()
    override fun onReceive(context: Context, intent: Intent) {
// ==================================================================

        //        String state  = intent.getExtras().getString("extra");
        //        Log.d("AlarmReceiver","Alarm in Receiver run "  + state );

        val serviceIntent = Intent(context, NotificationPlayingService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //            AlarmReceiver.this.startForegroundService(serviceIntent);
            context.startForegroundService(serviceIntent)
        } else {
            //            context.startService(new Intent(context, ServedService.class));
            context.startService(serviceIntent)
        }

//         throw UnsupportedOperationException("Not yet implemented")
    }
    private fun onCallEnd(context: Context) {
        context.stopService(Intent(context, NotificationPlayingService::class.java))
    }
}