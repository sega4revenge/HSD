package com.finger.hsd.common

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.support.multidex.MultiDex
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.ConnectionQuality
import com.finger.hsd.manager.GoogleApiHelper
import com.finger.hsd.util.ConnectivityChangeReceiver
import io.realm.Realm
import io.realm.RealmConfiguration
import okhttp3.OkHttpClient
import java.net.Socket
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class MyApplication : Application() {
        private var mSocket: Socket? = null
        //   private var mRealm: Realm? = null
        private var googleApiHelper: GoogleApiHelper? = null


        fun getSocket(): Socket? = mSocket
        private fun getUnsafeOkHttpClient(): OkHttpClient {
            try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {

                    }

                    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {

                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }

                })

                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory

                val builder = OkHttpClient.Builder()
                builder.connectTimeout(5, TimeUnit.SECONDS)
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier { _, _ -> true }

                return builder.build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }

        //    override fun onNetworkConnectionChanged(isConnected: Boolean) {
//        if(isConnected){
//            if(mRealm!=null){
//                mRealm?.close()
//            }
//            mRealm = Realm.getDefaultInstance()
//        }else{
//            if(mRealm!=null){
//                mRealm?.close()
//            }
//            val configSyn = RealmConfiguration.Builder()
//                    .name("realmChangeNotNetWork.realm")
//                    .build()
//            mRealm = Realm.getInstance(configSyn)
//        }
//    }
        fun setConnectivityListener(listener: ConnectivityChangeReceiver.ConnectivityReceiverListener) {
            ConnectivityChangeReceiver.connectivityReceiverListener = listener
        }

        override fun onCreate() {
            super.onCreate()
            instance = this
            var GlideApp = MyGlideModule()
            Realm.init(this)
            val config = RealmConfiguration.Builder().name("myAlarm.realm").build()
            Realm.setDefaultConfiguration(config)

            val okHttpClient = OkHttpClient().newBuilder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build()
            AndroidNetworking.initialize(applicationContext, getUnsafeOkHttpClient())
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
            MultiDex.install(this)
        }

        fun getGoogleApiHelperInstance(): GoogleApiHelper? = this.googleApiHelper

        companion object {
            var instance: MyApplication? = null
                private set
            fun getConnectivityListener(connect:ConnectivityChangeReceiver.ConnectivityReceiverListener ) = instance!!.setConnectivityListener(connect)
            fun okhttpclient(): OkHttpClient = instance!!.getUnsafeOkHttpClient()
            fun getGoogleApiHelper(): GoogleApiHelper? = instance!!.getGoogleApiHelperInstance()
        }
}

