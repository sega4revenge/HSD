package com.finger.hsd.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import com.finger.hsd.R
import com.finger.hsd.activity.HorizontalNtbActivity

@Suppress("DEPRECATION")
@SuppressLint("Registered")
class NotificationPlayingService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val mNM = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // request_code

        val intent1 = Intent(this, HorizontalNtbActivity::class.java)
        //        int request_code = intent.getExtras().getInt("request_code");

        val pIntent = PendingIntent.getActivity(this, 0, intent1, 0)

        val mNotify = Notification.Builder(this)
                .setContentTitle("Notification HSD " + "!")
                .setContentText(" .. ... ")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build()

        mNM.notify(0, mNotify)

        return Service.START_NOT_STICKY
    }
}