package com.finger.hsd

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.ConnectionQuality
import com.finger.hsd.manager.GoogleApiHelper
import okhttp3.OkHttpClient
import java.net.Socket
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    private var mSocket: Socket? = null
    private var googleApiHelper: GoogleApiHelper? = null

    fun getSocket(): Socket? = mSocket
    override fun onCreate() {
        super.onCreate()
        instance = this

        val okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .build()
        AndroidNetworking.initialize(applicationContext, okHttpClient)
        val options = BitmapFactory.Options()
        AndroidNetworking.setBitmapDecodeOptions(options)
        AndroidNetworking.enableLogging()
        AndroidNetworking.setConnectionQualityChangeListener { connectionQuality, _ ->
            when (connectionQuality) {
                ConnectionQuality.EXCELLENT -> System.out.println("Tot")

                ConnectionQuality.POOR -> System.out.println("Duoc")
                else -> {
                    System.out.println("Te")
                }
            }
        }
        googleApiHelper = GoogleApiHelper(instance!!)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

    }

    fun getGoogleApiHelperInstance(): GoogleApiHelper? = this.googleApiHelper

    companion object {
        var instance: MyApplication? = null
            private set

        fun getGoogleApiHelper(): GoogleApiHelper? = instance!!.getGoogleApiHelperInstance()
    }
}
