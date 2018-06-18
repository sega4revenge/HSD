package com.finger.hsd.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.finger.hsd.manager.SessionManager
import com.finger.hsd.util.Mylog

import me.leolin.shortcutbadger.ShortcutBadger

class NotificationService: Service() {
    var session : SessionManager? = null
    override fun onCreate() {
        session = SessionManager(applicationContext)
        badgeIconScreen()
    }

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    fun badgeIconScreen() {
        var badgeCount = 0

        if (session!!.getCountNotification() > 0) {
            try {
                badgeCount = session!!.getCountNotification()
            } catch (e: NumberFormatException) {
                Mylog.d("badge Count screen: ", e.message!!)
            }
            ShortcutBadger.applyCount(applicationContext, badgeCount)
        } else {
            ShortcutBadger.removeCount(applicationContext)
        }

    }
    override fun onDestroy() {
        super.onDestroy()
    }

}