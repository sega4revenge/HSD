package com.finger.hsd.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast

class NotificationService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show()
        return START_STICKY
    }
    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.


    }

    override fun onDestroy() {
        super.onDestroy()
    }

}