package com.finger.hsd.manager

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    var image: Bitmap? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.e("firebase: messager ", remoteMessage?.data?.toString())


        }
    }




