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

class LoginPresenter(view: LoginView) {

    internal var mLoginView: LoginView = view
    var login = "LOGIN"
    var register = "REGISTER"

    private val jsonObject = JSONObject()
    private val disposables = CompositeDisposable()
    private fun getObservable_login(typesearch: String): Observable<Response> {
        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
                .setTag(login)
                .addJSONObjectBody(jsonObject)
                .build()

                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d(login, " timeTakenInMillis : $timeTakenInMillis")
                    Log.d(login, " bytesSent : $bytesSent")
                    Log.d(login, " bytesReceived : $bytesReceived")
                    Log.d(login, " isFromCache : $isFromCache")
                }

                .getObjectObservable(Response::class.java)
    }

    private fun getDisposableObserver_login(): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                Log.d(login, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())

                mLoginView.getUserDetail(response.user!!)
            }

            override fun onError(e: Throwable) {
                if (e is ANError) {


                    Log.d(login, "onError errorCode : " + e.errorCode)
                    Log.d(login, "onError errorBody : " + e.errorBody)
                    Log.d(login, e.errorDetail + " : " + e.message)
                    mLoginView.isLoginSuccessful(false)
                    mLoginView.setErrorMessage(e.errorCode.toString(), 0)

                } else {
                    Log.d(login, "onError errorMessage : " + e.message)
                    mLoginView.isLoginSuccessful(false)
                }
            }

            override fun onComplete() {
                mLoginView.isLoginSuccessful(true)

            }
        }
    }

    fun login(phone_number: String,password : String,tokenfirebase : String) {


        try {
            jsonObject.put("phone", phone_number)
            jsonObject.put("password", password)
            jsonObject.put("tokenfirebase", tokenfirebase)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_login("authenticate")
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

    private fun getDisposableObserver_register(type: Int): DisposableObserver<Response> {

        return object : DisposableObserver<Response>() {

            override fun onNext(response: Response) {
                println(response.status)
                if (response.status == 200) {
                    Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
                    mLoginView.getUserDetail(response.user!!)
                    mLoginView.isRegisterSuccessful(true, type)
                } else {
                    mLoginView.setErrorMessage("201", type)
                }

            }

            override fun onError(e: Throwable) {
                if (e is ANError) {
                    Log.d(register, "onError errorCode : " + e.errorCode)
                    Log.d(register, "onError errorBody : " + e.errorBody)
                    Log.d(register, e.errorDetail + " : " + e.message)
                    if (e.errorCode == 409) {
                        mLoginView.setErrorMessage(e.errorCode.toString(), 0)
                    } else {
                        mLoginView.setErrorMessage(e.errorCode.toString(), 0)
                    }
                    mLoginView.isLoginSuccessful(false)
                } else {
                    Log.d(register, "onError errorMessage : " + e.message)
                    mLoginView.setErrorMessage(e.message!!, 1)
                }
            }

            override fun onComplete() {

            }
        }
    }

    fun register(user: User, type: Int) {

        try {
            if (type == Constants.FACEBOOK) {
                jsonObject.put("id", user.facebook!!)
                jsonObject.put("type", type)
                jsonObject.put("tokenfirebase", user.tokenfirebase)
            } else if (type == Constants.GOOGLE) {
                jsonObject.put("id", user.google!!)
                jsonObject.put("type", type)
                jsonObject.put("tokenfirebase", user.tokenfirebase)
            } else {
                jsonObject.put("email", user.phone)
                jsonObject.put("type", type)
                jsonObject.put("password", user.hashed_password)
                jsonObject.put("tokenfirebase", user.tokenfirebase)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        disposables.add(getObservable_register("users")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObserver_register(type)))

    }

    fun cancelRequest() {
        Log.e("Cancel", "Cancel Request")
        disposables.clear()
    }

    interface LoginView {

        fun isLoginSuccessful(isLoginSuccessful: Boolean)
        fun isRegisterSuccessful(isRegisterSuccessful: Boolean, type: Int)
        fun setErrorMessage(errorMessage: String, type: Int)
        fun getUserDetail(user: User)
        fun setCode(code: String)
    }
}
