package com.finger.hsd.common


import android.util.Log
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import org.json.JSONObject

open class getObservable {
    internal var jsonObject = JSONObject()

    fun getJsonObjectObservable(link: String): Observable<JSONObject> {
        return Rx2AndroidNetworking.post(link)
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d("", " timeTakenInMillis : $timeTakenInMillis")
                    Log.d("", " bytesSent : $bytesSent")
                    Log.d("", " bytesReceived : $bytesReceived")
                    Log.d("", " isFromCache : $isFromCache")
                }.jsonObjectObservable
    }

    fun getJsonResponseObservable(link: String): Observable<com.finger.hsd.model.Response> {
        return Rx2AndroidNetworking.post(link)
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d("", " timeTakenInMillis : $timeTakenInMillis")
                    Log.d("", " bytesSent : $bytesSent")
                    Log.d("", " bytesReceived : $bytesReceived")
                    Log.d("", " isFromCache : $isFromCache")
                }  .getObjectObservable(com.finger.hsd.model.Response::class.java)
    }
}
