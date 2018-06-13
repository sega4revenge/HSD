package com.finger.hsd.presenter

import android.os.Looper
import android.util.Log
import com.androidnetworking.error.ANError
import com.finger.hsd.model.Response
import com.finger.hsd.model.User
import com.finger.hsd.util.Constants
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
        var  startTime = System.nanoTime();
        val total = 100
        var current = 0
        var percent: Int = 0

//        task = object : TimerTask() {
//            internal var mHandler = Handler()
//
//            override fun run() {
//                mHandler.post(Runnable {
//                    mLoginView.isProgressData(percent)
//
//                })
//            }
//        }

//        timer = Timer()
//        timer!!.scheduleAtFixedRate(task, 0, 500)


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
                } else {
                    mLoginView.setErrorMessage("201")
                }

            }

            override fun onError(e: Throwable) {
                if (e is ANError) {

                    Log.d(login, "onError errorCode : " + e.errorCode)
                    Log.d(login, "onError errorBody : " + e.errorBody)
                    Log.d(login, e.errorDetail + " : " + e.message)
                    mLoginView.isLoginSuccessful(false)
                    mLoginView.setErrorMessage(e.errorCode.toString())

                } else {
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


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun getObservable_register(typesearch: String): Observable<Response> {
        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
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

    private fun getDisposableObserver_register(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                println(response.status)
                if (response.status == 200) {
                    Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                    mLoginView.getUserDetail(response.user!!)
                    mLoginView.isRegisterSuccessful(true)
                } else {
                    mLoginView.setErrorMessage("201")
                }

            }

            override fun onError(e: Throwable) {
                if (e is ANError) {
                    Log.d(register, "onError errorCode : " + e.errorCode)
                    Log.d(register, "onError errorBody : " + e.errorBody)
                    Log.d(register, e.errorDetail + " : " + e.message)
                    if (e.errorCode == 409) {
                        mLoginView.setErrorMessage(e.errorCode.toString())
                    } else {
                        mLoginView.setErrorMessage(e.errorCode.toString())
                    }
                    mLoginView.isLoginSuccessful(false)
                } else {
                    Log.d(register, "onError errorMessage : " + e.message)
                    mLoginView.setErrorMessage(e.message!!)
                }
            }

            override fun onComplete() {

            }
        }
    }

    fun register(user: User) {

        try {

                jsonObject.put("phone", user.phone)
                jsonObject.put("password", user.password)
                jsonObject.put("tokenfirebase", user.tokenfirebase)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_register("users")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_register()))

    }

    fun cancelRequest() {
        Log.e("Cancel", "Cancel Request")
        disposables.clear()
    }


    interface LoginView {
        fun isProgressData(percent : Int)
        fun isLoginSuccessful(isLoginSuccessful: Boolean)
        fun isRegisterSuccessful(isRegisterSuccessful: Boolean)
        fun setErrorMessage(errorMessage: String)
        fun getUserDetail(user: User)

    }
}
