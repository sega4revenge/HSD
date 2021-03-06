package com.finger.hsd.presenter

import android.os.Looper
import android.util.Log
import com.androidnetworking.error.ANError
import com.finger.hsd.model.Response
import com.finger.hsd.model.User
import com.finger.hsd.util.Constants
import com.finger.hsd.util.Mylog
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class LoginPresenter(view: LoginView) {
var a : String?=null
    internal var mLoginView: LoginView = view
    var login = "LOGIN"
    var register = "REGISTER"
    private val jsonObject = JSONObject()
    private val disposables = CompositeDisposable()
    var task: TimerTask? = null
    var timer : Timer? = null
    private fun getObservable_login(typesearch: String): Observable<Response> {

        return Rx2AndroidNetworking.post(Constants.URL_LOGIN)
                .setTag(login)
                .addJSONObjectBody(jsonObject)
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->

                    Log.d(login, " timeTakenInMillis : $timeTakenInMillis")
                    Log.d(login, " bytesSent : $bytesSent")
                    Log.d(login, " bytesReceived : $bytesReceived")
                    Log.d(login, " isFromCache : $isFromCache")
                }
//                }.setDownloadProgressListener(object : DownloadProgressListener{
//                    override fun onProgress(bytesDownloaded: Long, totalBytes: Long) {
//
//                        percent = (bytesDownloaded* 100f /totalBytes).toInt()
//
//                    }
//
//                })
                .getObjectObservable(Response::class.java)

    }

    private fun getDisposableObserver_login(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {
            override fun onNext(response: Response?) {

                if (response!!.status == 200) {
                    Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                    mLoginView.getUserDetail(response.user!!)
                  //  mLoginView.isRegisterSuccessful(true)
                }

            }

            override fun onError(e: Throwable) {
                if (e is ANError) {

                    Log.d(login, "onError errorCode : " + e.errorCode)
                    Log.d(login, "onError errorBody : " + e.errorBody)
                    Log.d(login, e.errorDetail + " : " + e.message)
                    if(e.errorBody !=null) {
//                    mLoginView.isLoginSuccessful(false)
                        var jsonObject = JSONObject(e.errorBody)
                        var errorStatus = jsonObject.getInt("status")
                        mLoginView.setErrorMessage(e.errorCode, errorStatus)
                    }
                    else
                        mLoginView.setErrorMessage(e.errorCode, 0)

                } else {
                    mLoginView.setErrorMessage(500,500)
                    Log.d(login, "onError errorMessage : " + e.message)
                    mLoginView.isLoginSuccessful(false)
                }
            }

            override fun onComplete() {
              //  mLoginView.isLoginSuccessful(true)

            }
        }
    }

    fun login(user :User) {

        try {
            jsonObject.put("phone", user.phone)
            jsonObject.put("password", user.password)
            jsonObject.put("tokenfirebase", user.tokenfirebase)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_login("login-android")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_login()))
    }


    /////////////////////////////////////////////// REGISTER //////////////////////////////////////////////////////////////
    private fun getObservable_register(typesearch: String): Observable<Response> {
        return Rx2AndroidNetworking.post(typesearch)
                .setTag(register)
                .addJSONObjectBody(jsonObject)
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(register, " timeTakenInMillis : $timeTakenInMillis")
                    Log.d(register, " bytesSent : $bytesSent")
                    Log.d(register, " bytesReceived : $bytesReceived")
                    Log.d(register, " isFromCache : $isFromCache")
                }

                .getObjectObservable(Response::class.java)
    }

    // register
    private fun getDisposableObserver_register(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Mylog.d("ppppp " +response.user)
                if (response.status == 200) {
                    Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                    mLoginView.getUserDetail(response.user!!)
                  //  mLoginView.isRegisterSuccessful(true)
                }

            }

            override fun onError(e: Throwable) {
                Mylog.d("ppppp " +e.message)
                if (e is ANError) {

                    Log.d(register, "onError errorCode : " + e.errorCode)
                    Log.d(register, "onError errorBody : " + e.errorBody)
                    Log.d(register, e.errorDetail + " : " + e.message)

                   if (e.errorBody != null) {
                       var jsonObject = JSONObject(e.errorBody)
                       var errorStatus = jsonObject.getInt("status")
                       mLoginView.setErrorMessage(e.errorCode, errorStatus)
                   }else{
                       mLoginView.setErrorMessage(e.errorCode, 0)
                   }

                } else {
                    Log.d(register, "onError errorMessage : " + e.message)
                    mLoginView.setErrorMessage(500 , 500)
                }
            }

            override fun onComplete() {

            }
        }
    }

    fun register(user: User, type: Int) {

        Mylog.d("ppppp types: "+type)
        try {
                jsonObject.put("phone", user.phone)
                jsonObject.put("password", user.password)
                jsonObject.put("tokenfirebase", user.tokenfirebase)
                jsonObject.put("type", type)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        disposables.add(getObservable_register(Constants.URL_REGISTER_USER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_register()))

    }

    fun cancelRequest() {
        Log.e("Cancel", "Cancel Request")
        disposables.clear()
    }


    interface LoginView {

        fun isLoginSuccessful(isLoginSuccessful: Boolean)
        fun isRegisterSuccessful(isRegisterSuccessful: Boolean)
        fun setErrorMessage(errorCode: Int, errorBody: Int)
        fun getUserDetail(user: User)

    }
}
