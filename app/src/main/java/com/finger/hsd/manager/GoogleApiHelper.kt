package com.finger.hsd.manager

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient

class GoogleApiHelper(private val context: Context) : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    var googleApiClient: GoogleApiClient? = null
        private set
    private var connectionListener: ConnectionListener? = null
    private var connectionBundle: Bundle? = null

    init {
        buildGoogleApiClient()
        connect()
    }

    fun setConnectionListener(connectionListener: ConnectionListener) {
        this.connectionListener = connectionListener
        if (this.connectionListener != null && isConnected) {
            connectionListener.onConnected(connectionBundle, googleApiClient)
        }
    }

    private fun connect() {
        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }
    }

    fun disconnect() {
        if (googleApiClient != null && googleApiClient!!.isConnected) {
            googleApiClient!!.disconnect()
        }
    }

    val isConnected: Boolean
        get() = googleApiClient != null && googleApiClient!!.isConnected

    private fun buildGoogleApiClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        googleApiClient = GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build()

    }

    override fun onConnected(bundle: Bundle?) {
        connectionBundle = bundle
        if (connectionListener != null) {
            connectionListener?.onConnected(bundle, googleApiClient)
        }
    }

    override fun onConnectionSuspended(i: Int) {
        Log.d(TAG, "onConnectionSuspended: googleApiClient.connect()")
        googleApiClient!!.connect()
        if (connectionListener != null) {
            connectionListener!!.onConnectionSuspended(i)
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed: connectionResult = " + connectionResult)
        if (connectionListener != null) {
            connectionListener!!.onConnectionFailed(connectionResult)
        }
    }

    interface ConnectionListener {
        fun onConnectionFailed(connectionResult: ConnectionResult)

        fun onConnectionSuspended(i: Int)

        fun onConnected(bundle: Bundle?, client: GoogleApiClient?)
    }

    companion object {
        private val TAG = GoogleApiHelper::class.java.simpleName
    }
}