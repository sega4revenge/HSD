//package com.finger.hsd.presenter
//
//import android.os.Looper
//import android.util.Log
//import com.androidnetworking.error.ANError
//import com.finger.hsd.model.Response
//import com.finger.hsd.model.User
//import com.finger.hsd.util.Constants
//import com.google.firebase.iid.FirebaseInstanceId
//import com.rx2androidnetworking.Rx2AndroidNetworking
//import io.reactivex.Observable
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.CompositeDisposable
//import io.reactivex.observers.DisposableObserver
//import io.reactivex.schedulers.Schedulers
//import org.json.JSONException
//import org.json.JSONObject
//
//class LoginPresenter(view: LoginView) {
//
//    internal var mLoginView: LoginView = view
//    var login = "LOGIN"
//    var register = "REGISTER"
//    private var register_finish = "REGISTER_FINISH"
//
//    private val jsonObject = JSONObject()
//    private val disposables = CompositeDisposable()
//    private fun getObservable_login(typesearch: String): Observable<Response> {
//        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
//                .setTag(login)
//                .addJSONObjectBody(jsonObject)
//                .build()
//
//                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
//                    Log.d(login, " timeTakenInMillis : $timeTakenInMillis")
//                    Log.d(login, " bytesSent : $bytesSent")
//                    Log.d(login, " bytesReceived : $bytesReceived")
//                    Log.d(login, " isFromCache : $isFromCache")
//                }
//
//                .getObjectObservable(Response::class.java)
//    }
//    private fun getDisposableObserver_login(): DisposableObserver<Response> {
//
//        return object : DisposableObserver<Response>() {
//
//            override fun onNext(response: Response) {
//                Log.d(login, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
//
//                mLoginView.setErrorMessage("202",999)
//            }
//            override fun onError(e: Throwable) {
//                if (e is ANError) {
//
//
//                    Log.d(login, "onError errorCode : " + e.errorCode)
//                    Log.d(login, "onError errorBody : " + e.errorBody)
//                    Log.d(login, e.errorDetail + " : " + e.message)
//                    mLoginView.isLoginSuccessful(false)
//                    mLoginView.setErrorMessage(e.errorCode.toString(),999)
//
//                } else {
//                    Log.d(login, "onError errorMessage : " + e.message)
//                    mLoginView.isLoginSuccessful(false)
//                }
//            }
//
//            override fun onComplete() {
//
//
//            }
//        }
//    }
//    fun login(phone_number: String) {
//
//
//        try {
//            jsonObject.put("phone", phone_number)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        disposables.add(getObservable_login("authenticate")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(getDisposableObserver_login()))
//    }
//    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    private fun getObservable_loginfinish(typesearch: String): Observable<Response> {
//        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
//                .setTag(login)
//                .addJSONObjectBody(jsonObject)
//                .build()
//
//                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
//                    Log.d(login, " timeTakenIn«Millis : $timeTakenInMillis")
//                    Log.d(login, " bytesSent : $bytesSent")
//                    Log.d(login, " bytesReceived : $bytesReceived")
//                    Log.d(login, " isFromCache : $isFromCache")
//                }
//
//                .getObjectObservable(Response::class.java)
//    }
//    private fun getDisposableObserver_loginfinish(): DisposableObserver<Response> {
//
//        return object : DisposableObserver<Response>() {
//
//            override fun onNext(response: Response) {
//                Log.d(login, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
//
//                mLoginView.getUserDetail(response.user!!)
//            }
//            override fun onError(e: Throwable) {
//                if (e is ANError) {
//
//
//                    Log.d(login, "onError errorCode : " + e.errorCode)
//                    Log.d(login, "onError errorBody : " + e.errorBody)
//                    Log.d(login, e.errorDetail + " : " + e.message)
//                    mLoginView.isLoginSuccessful(false)
//                    mLoginView.setErrorMessage(e.errorCode.toString(),0)
//
//                } else {
//                    Log.d(login, "onError errorMessage : " + e.message)
//                    mLoginView.isLoginSuccessful(false)
//                }
//            }
//
//            override fun onComplete() {
//                mLoginView.isLoginSuccessful(true)
//
//            }
//        }
//    }
//    fun loginfinish(phone_number: String,code: String) {
//
//        val tokenfirebase = FirebaseInstanceId.getInstance().token
//        try {
//            jsonObject.put("phone", phone_number)
//            jsonObject.put("code", code)
//            jsonObject.put("tokenfirebase", tokenfirebase)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        disposables.add(getObservable_loginfinish("authenticatefinish")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(getDisposableObserver_loginfinish()))
//    }
//    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    private fun getObservable_register(typesearch: String): Observable<Response> {
//        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
//                .setTag(register)
//                .addJSONObjectBody(jsonObject)
//                .build()
//
//                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
//                    Log.d(register, " timeTakenInMillis : " + timeTakenInMillis)
//                    Log.d(register, " bytesSent : " + bytesSent)
//                    Log.d(register, " bytesReceived : " + bytesReceived)
//                    Log.d(register, " isFromCache : " + isFromCache)
//                }
//
//                .getObjectObservable(Response::class.java)
//    }
//    private fun getDisposableObserver_register(type: Int): DisposableObserver<Response> {
//
//        return object : DisposableObserver<Response>() {
//
//            override fun onNext(response: Response) {
//                println(response.status)
//                if(response.status == 201)
//                    mLoginView.setErrorMessage("201",type)
//                else
//                {
//                    Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
//                    mLoginView.getUserDetail(response.user!!)
//                    mLoginView.isRegisterSuccessful(true, type)
//                }
//
//            }
//            override fun onError(e: Throwable) {
//                if (e is ANError) {
//                    Log.d(register, "onError errorCode : " + e.errorCode)
//                    Log.d(register, "onError errorBody : " + e.errorBody)
//                    Log.d(register, e.errorDetail + " : " + e.message)
//                    if(e.errorCode == 409)
//                    {
//                        mLoginView.setErrorMessage(e.errorCode.toString(),0)
//                    }else{
//                        mLoginView.setErrorMessage(e.errorCode.toString(),0)
//                    }
//                    mLoginView.isLoginSuccessful(false)
//                } else {
//                    Log.d(register, "onError errorMessage : " + e.message)
//                    mLoginView.setErrorMessage(e.message!!,1)
//                }
//            }
//
//            override fun onComplete() {
//
//            }
//        }
//    }
//    fun register(user: User, type: Int) {
//
//        try {
//            if (type == Constants.FACEBOOK) {
//                jsonObject.put("id", user.facebook!!.id)
//                jsonObject.put("token", user.facebook!!.token)
//                jsonObject.put("name", user.facebook!!.name)
//                jsonObject.put("email", user.facebook!!.email)
//                jsonObject.put("password", user.hashed_password)
//                jsonObject.put("photoprofile", user.facebook!!.photoprofile)
//                jsonObject.put("type", type)
//                jsonObject.put("tokenfirebase", user.tokenfirebase)
//            } else if (type == Constants.GOOGLE) {
//                jsonObject.put("id", user.google!!.id)
//                jsonObject.put("token", user.google!!.token)
//                jsonObject.put("name", user.google!!.name)
//                jsonObject.put("email", user.google!!.email)
//                jsonObject.put("password", user.hashed_password)
//                jsonObject.put("photoprofile", user.google!!.photoprofile)
//                jsonObject.put("type", type)
//                jsonObject.put("tokenfirebase", user.tokenfirebase)
//            } else {
//                jsonObject.put("name", user.name)
//                jsonObject.put("email", user.email)
//                jsonObject.put("type", type)
//                jsonObject.put("password", user.hashed_password)
//                jsonObject.put("tokenfirebase", user.tokenfirebase)
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        disposables.add(getObservable_register("users")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(getDisposableObserver_register(type)))
//
//    }
//
//    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    private fun getObservable_linkaccount(typesearch: String): Observable<Response> {
//        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
//                .setTag(register)
//                .addJSONObjectBody(jsonObject)
//                .build()
//
//                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
//                    Log.d(register, " timeTakenInMillis : " + timeTakenInMillis)
//                    Log.d(register, " bytesSent : " + bytesSent)
//                    Log.d(register, " bytesReceived : " + bytesReceived)
//                    Log.d(register, " isFromCache : " + isFromCache)
//                }
//
//                .getObjectObservable(Response::class.java)
//    }
//    private fun getDisposableObserver_linkaccount(type: Int): DisposableObserver<Response> {
//
//        return object : DisposableObserver<Response>() {
//
//            override fun onNext(response: Response) {
//                Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
////                Log.e("codeeee",response.message)
////                mLoginView.setCode(response.message.toString())
//                mLoginView.setErrorMessage(response.status.toString(),type)
//            }
//            override fun onError(e: Throwable) {
//                if (e is ANError) {
//                    Log.d(register, "onError errorCode : " + e.errorCode)
//                    Log.d(register, "onError errorBody : " + e.errorBody)
//                    Log.d(register, e.errorDetail + " : " + e.message)
//                    mLoginView.setErrorMessage(e.errorCode.toString(),1)
//                } else {
//                    Log.d(register, "onError errorMessage : " + e.message)
//                    mLoginView.setErrorMessage(e.message!!,1)
//                }
//            }
//
//            override fun onComplete() {
//
//            }
//        }
//    }
//    fun linkaccount(user: User, type: Int) {
//        println(type)
//        try {
//            if (type == Constants.FACEBOOK) {
//
//                jsonObject.put("id", user.facebook!!.id)
//                jsonObject.put("token", user.facebook!!.token)
//                jsonObject.put("name", user.facebook!!.name)
//                jsonObject.put("phone", user.phone)
//                jsonObject.put("email", user.facebook!!.email)
//                jsonObject.put("password", user.hashed_password)
//                jsonObject.put("photoprofile", user.facebook!!.photoprofile)
//                jsonObject.put("type", type)
//                jsonObject.put("tokenfirebase", user.tokenfirebase)
//            } else if (type == Constants.GOOGLE) {
//                jsonObject.put("id", user.google!!.id)
//                jsonObject.put("token", user.google!!.token)
//                jsonObject.put("name", user.google!!.name)
//                jsonObject.put("phone", user.phone)
//                jsonObject.put("email", user.google!!.email)
//                jsonObject.put("password", user.hashed_password)
//                jsonObject.put("photoprofile", user.google!!.photoprofile)
//                jsonObject.put("type", type)
//                jsonObject.put("tokenfirebase", user.tokenfirebase)
//            }
//            else{
//                jsonObject.put("name", user.name)
//                jsonObject.put("phone", user.phone)
//                jsonObject.put("email", user.email)
//                jsonObject.put("password", user.hashed_password)
//                jsonObject.put("photoprofile", user.photoprofile)
//                jsonObject.put("type", type)
//                jsonObject.put("tokenfirebase", user.tokenfirebase)
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//        disposables.add(getObservable_linkaccount("linkaccount")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(getDisposableObserver_linkaccount(type)))
//
//    }
//
//    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    private fun getObservable_register_finish(typesearch: String): Observable<Response> {
//        return Rx2AndroidNetworking.post(Constants.BASE_URL + typesearch)
//                .setTag(register_finish)
//                .addJSONObjectBody(jsonObject)
//                .build()
//
//                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
//                    Log.d(register_finish, " timeTakenInMillis : " + timeTakenInMillis)
//                    Log.d(register_finish, " bytesSent : " + bytesSent)
//                    Log.d(register_finish, " bytesReceived : " + bytesReceived)
//                    Log.d(register_finish, " isFromCache : " + isFromCache)
//                }
//
//                .getObjectObservable(Response::class.java)
//    }
//    private fun getDisposableObserver_register_finish(): DisposableObserver<Response> {
//
//        return object : DisposableObserver<Response>() {
//
//            override fun onNext(response: Response) {
//                Log.d(register, "onResponse isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString())
//                mLoginView.getUserDetail(response.user!!)
//            }
//            override fun onError(e: Throwable) {
//                if (e is ANError) {
//                    if (e.errorCode != 0) {
//                        // received ANError from server
//                        // error.getErrorCode() - the ANError code from server
//                        // error.getErrorBody() - the ANError body from server
//                        // error.getErrorDetail() - just a ANError detail
//                        Log.d(register, "onError errorCode : " + e.errorCode)
//                        Log.d(register, "onError errorBody : " + e.errorBody)
//                        Log.d(register, "onError errorDetail : " + e.errorDetail)
//                        mLoginView.setErrorMessage(e.errorCode.toString(), 2)
//                    } else {
//                        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
//                        Log.d(register, "onError errorDetail : " + e.errorDetail)
//                        mLoginView.setErrorMessage(e.errorCode.toString(), 2)
//                    }
//                } else {
//                    Log.d(register, "onError errorMessage : " + e.message)
//                    mLoginView.isRegisterSuccessful(false, 2)
//                }
//            }
//
//            override fun onComplete() {
//                mLoginView.isRegisterSuccessful(true,3)
//
//            }
//        }
//    }
//
//    fun register_finish(user: User, code: String,type: Int) {
//
//        try {
////            Log.e("weqweqwe",FirebaseInstanceId.getInstance().token)
////            Log.e("asdasd",user.phone + " / " + user.tokenfirebase)
//            jsonObject.put("phone", user.phone)
//            jsonObject.put("code", code)
//            jsonObject.put("type", type)
//            jsonObject.put("token",user.tokenfirebase)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        disposables.add(getObservable_register_finish("registerfinish")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(getDisposableObserver_register_finish()))
//    }
//    fun cancelRequest() {
//        Log.e("Cancel","Cancel Request")
//        disposables.clear()
//    }
//    interface LoginView {
//
//        fun isLoginSuccessful(isLoginSuccessful: Boolean)
//        fun isRegisterSuccessful(isRegisterSuccessful: Boolean, type: Int)
//        fun setErrorMessage(errorMessage: String, type: Int)
//        fun getUserDetail(user: User)
//        fun setCode(code : String)
//    }
//}
