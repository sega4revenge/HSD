package com.finger.hsd.activity

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.androidnetworking.error.ANError
import com.finger.hsd.BaseActivity
import com.finger.hsd.library.CompressImage
import com.finger.hsd.manager.RealmController
import com.finger.hsd.model.Notification
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.Response
import com.finger.hsd.util.Constants
import com.finger.hsd.util.Mylog
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class SyncActivity : BaseActivity() {

    //thuc hien dong bo len server
    // kiem tra co mang hay khong

    var realm: RealmController? = null
    val disposable = CompositeDisposable()
    var listProduct : ArrayList<Product_v>? = null
    var listNotification : ArrayList<Notification>? = null
    var temp: Int = 0
    var indexNotification : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        realm = RealmController(this)

        // get list no sync
        listProduct = realm!!.getListProductNotSync()
        listNotification = realm!!.getListNotificationNotSync()
        temp = 0
        indexNotification = 0
        syncListProduct()


    }

    fun syncListProduct() {
        if (listProduct != null && !listProduct!!.isEmpty() && temp < listProduct!!.size) {
            processInfomationUpdate(listProduct!![temp])

        } else {
            syncNotification()
        }
    }

    fun syncNotification(){
        if (listNotification != null && !listNotification!!.isEmpty() && temp < listNotification!!.size) {
            processNotificationUpdate(listNotification!![indexNotification])
        }else{
            finish()
        }
    }

    fun processInfomationUpdate(product: Product_v){
         var jsonObject = JSONObject()
        try {
            jsonObject.put("id_product", product._id)
            jsonObject.put("nameproduct", product.namechanged)
            jsonObject.put("hsd_ex", product.expiretime)
            jsonObject.put("description", product.description)
            jsonObject.put("delete", product.delete)
        }catch (e: JSONException){
            e.printStackTrace()
        }
        disposable.add(Rx2AndroidNetworking.post(Constants.URL_UPDATE_INFOMATION_PRODUCT)
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d("", " timeTakenInMillis : $timeTakenInMillis")
                    Log.d("", " bytesSent : $bytesSent")
                    Log.d("", " bytesReceived : $bytesReceived")
                    Log.d("", " isFromCache : $isFromCache")
                }.jsonObjectObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<JSONObject>(){
                    override fun onComplete() {

                    }

                    override fun onNext(t: JSONObject?) {
//                        val file = File(product.imagechanged!!)
//                        val imageUri = Uri.fromFile(file)

                        var file = File(product.imagechanged)
                        UploadImage(product._id!!, file)
                    }

                    override fun onError(e: Throwable?) {
                        Mylog.d(e!!.printStackTrace().toString())

                    }

                }))
    }
    fun UploadImage(idProduct: String, file: File?) {
        val disposable = CompositeDisposable()
        Rx2AndroidNetworking.upload(Constants.URL_UPDATE_IMAGE)
                .addMultipartParameter("id_product", idProduct)
                .addMultipartFile("image", CompressImage.compressImage(this, file))
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache -> }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            Mylog.d(e.message!!)
                        }
                    }

                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(response: Response) {
                        temp++
                        syncListProduct()

//                        val intent = Intent()
//                        intent.putExtra(AppIntent.DATA_UPDATE_ITEM, position)
//                        intent.putExtra("expredTime", expiredTime)
//                        intent.putExtra("name", response.nameProduct)
//                        intent.putExtra("image", response.image)
//                        setResult(AppIntent.RESULT_UPDATE_ITEM, intent)

                    }
                })
    }


    fun processNotificationUpdate(notification: Notification){
        var jsonObject = JSONObject()
        try {
            jsonObject.put("id_product", notification.id_product)
            jsonObject.put("watched", notification.watched)
            jsonObject.put("content", notification.content)
            jsonObject.put("delete", notification.delete)
        }catch (e: JSONException){
            e.printStackTrace()
        }
        disposable.add(Rx2AndroidNetworking.post(Constants.URL_UPDATE_NOTIFICATION)
                .addJSONObjectBody(jsonObject)
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                    Log.d("", " timeTakenInMillis : $timeTakenInMillis")
                    Log.d("", " bytesSent : $bytesSent")
                    Log.d("", " bytesReceived : $bytesReceived")
                    Log.d("", " isFromCache : $isFromCache")
                }.jsonObjectObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<JSONObject>(){
                    override fun onComplete() {

                    }

                    override fun onNext(t: JSONObject?) {
                        indexNotification++
                        syncNotification()

                    }

                    override fun onError(e: Throwable?) {
                        Mylog.d(e!!.printStackTrace().toString())

                    }

                }))
    }
    fun getRealFilePath(context: Context, uri: Uri): String? {
        if (null == uri) return null
        val scheme: String = uri.scheme
        var data: String? = null
        if (scheme == null) {
            data = uri.path
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            val cursor = context.getContentResolver().query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }


}