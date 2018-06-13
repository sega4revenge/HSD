//package com.finger.hsd.services
//
//import android.content.Context
//import android.os.AsyncTask
//import android.util.Log
//import com.androidnetworking.AndroidNetworking
//import com.androidnetworking.error.ANError
//import com.facebook.FacebookSdk
//import com.facebook.FacebookSdk.getApplicationContext
//import com.finger.hsd.common.MyApplication
//import com.finger.hsd.manager.RealmController
//import com.finger.hsd.model.Product_v
//import com.finger.hsd.util.Constants
//import java.io.File
//
//open class DataSync() : AsyncTask<List<Product_v>, Int, String>() {
//    override fun doInBackground(vararg p0: List<Product_v>?): String {
//        return dowloadImageFromServer(p0[0]!!)
//    }
//
//    internal var realm: RealmController? = null
//    var dataListener: DataListener? = null
//    var mContext : Context? = null
//
//
//    constructor(mContext: Context, dataListener: DataListener) : this() {
//        realm = RealmController(mContext)
//        this.dataListener = dataListener
//        this.mContext = mContext
//    }
//
//    override fun onPreExecute() {
//        dataListener!!.onLoadStarted()
//    }
//
//     override fun onProgressUpdate(vararg values: Int?) {
//        dataListener!!.onLoading(values[0]!!)
//    }
//
//    override fun onPostExecute(user: String) {
//        dataListener!!.onLoadComplete(user)
//    }
//
//    private fun dowloadImageFromServer(listProduct: List<Product_v>?): String {
//        var percent = 0
//        var current = 0
//
//
//        if (listProduct != null && !listProduct!!.isEmpty()) {
//            for (i in 0..listProduct!!.size - 1) {
//                current++
//                var product = listProduct!!.get(i)
//                val mediaStorageDir = getApplicationContext().getExternalFilesDir(null)
//                val timeStamp = System.currentTimeMillis()
//                var path2 = File.separator + "IMG_" + timeStamp + "_" + product?.barcode + ".jpg"
//
//                AndroidNetworking.initialize(FacebookSdk.getApplicationContext(), MyApplication.okhttpclient())
//                AndroidNetworking.download(Constants.IMAGE_URL + product.imagechanged, mediaStorageDir.path, path2)
//                        .build().startDownload(object : com.androidnetworking.interfaces.DownloadListener {
//                            override fun onDownloadComplete() {
//                                Log.d("REALMCONTROLLER", "UPDATE SUCCESS")
//                                product.imagechanged = mediaStorageDir.path + path2
//                                realm!!.updateProduct(product)
////                                realm.beginTransaction()
////                                backgroundRealm.copyToRealmOrUpdate(product)
////                                backgroundRealm.commitTransaction()
//                                Log.d("REALMCONTROLLER2", product._id)
//                            }
//
//                            override fun onError(anError: ANError?) {
//
//                                Log.d("REALMCONTROLLER", anError?.errorDetail + "//ERROR" + anError?.errorBody + "//" + product.imagechanged)
//                            }
//                        })
//
//                percent = (current / (listProduct!!.size - 1) * 100f).toInt()
//                publishProgress(percent)
//            }
//
//
//        }
//        return "oc"
//
//    }
//}
