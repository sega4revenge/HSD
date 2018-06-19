package com.finger.hsd.presenter

import android.util.Log
import com.finger.hsd.common.getObservable
import com.finger.hsd.model.Notification
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.Response
import com.finger.hsd.util.Constants
import com.finger.hsd.util.Mylog
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject

class SyncPresenter : getObservable {

    val disposable = CompositeDisposable()

    val  Ipresenter : ISyncPresenter

    constructor(IpresenterView : ISyncPresenter){

        this.Ipresenter = IpresenterView

    }

    fun processDeleteProduct(idProduct : String, iduser: String){
        try {
            jsonObject.put("id_product", idProduct)
            jsonObject.put("id_user", iduser)
        }catch (e: JSONException){
            e.printStackTrace()
        }
        disposable.add(getJsonObjectObservable(Constants.URL_DELETE_PRODUCT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<JSONObject>(){
                    override fun onComplete() {

                    }

                    override fun onNext(t: JSONObject?) {
                        Ipresenter.onSucess(t!!, 111)
                    }

                    override fun onError(e: Throwable?) {
                        Mylog.d(e!!.printStackTrace().toString())
                        Ipresenter.onError(111)
                    }

                }))
    }

    fun updateProduct(product: Product_v){
        try {
            jsonObject.put("id_product", product._id)
            jsonObject.put("nameproduct", product.namechanged)
            jsonObject.put("hsd_ex", product.expiretime)
            jsonObject.put("description", product.description)
        }catch (e: JSONException){
            e.printStackTrace()
        }
        disposable.add(getJsonResponseObservable(Constants.URL_UPDATE_INFOMATION_PRODUCT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Response>(){
                    override fun onComplete() {

                    }

                    override fun onNext(t: Response?) {
                        Ipresenter.onSucess(jsonObject, 222)
                    }

                    override fun onError(e: Throwable?) {
                        Mylog.d(e!!.printStackTrace().toString())
                        Ipresenter.onError(222)
                    }

                }))
    }

    fun updateNotficationOnServer(notification: Notification, iduser: String) {
        Mylog.d("ttttttttt idproduct: "+notification.id_product)

        var jsonObject = JSONObject()
        try {
            jsonObject.put("id_product", notification.id_product)
            jsonObject.put("idUser", iduser)
            jsonObject.put("type", notification.type)
            jsonObject.put("watched", notification.watched)
            jsonObject.put("time", notification.create_at)
        } catch (e: Exception) {
            Mylog.d("Error " + e.message)
        }
        Rx2AndroidNetworking.post(Constants.URL_UPDATE_NOTIFICATION)
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d("", " timeTakenInMillis : $timeTakenInMillis")
                    Log.d("", " bytesSent : $bytesSent")
                    Log.d("", " bytesReceived : $bytesReceived")
                    Log.d("", " isFromCache : $isFromCache")
                }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Response>() {
                    override fun onComplete() {

                    }

                    override fun onNext(t: Response?) {
                        Mylog.d("onsucess")
                      Ipresenter.onSucess(jsonObject, 333)
                    }

                    override fun onError(e: Throwable?) {
                        Mylog.d(e!!.printStackTrace().toString())

                        Ipresenter.onError(333)



                    }

                })

    }


    interface ISyncPresenter{
        fun onSucess(response: JSONObject,type: Int)
        fun onError (typeError : Int)
    }


}