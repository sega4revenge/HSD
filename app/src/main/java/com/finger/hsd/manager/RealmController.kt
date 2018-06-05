package com.finger.hsd.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.support.v4.app.Fragment

import com.finger.hsd.model.Product
import com.finger.hsd.model.Product_v

import io.realm.Realm
import android.os.Environment.getExternalStorageDirectory
import android.graphics.Bitmap
import android.content.Context.MODE_PRIVATE
import com.facebook.FacebookSdk.getApplicationContext
import android.content.ContextWrapper
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import android.media.MediaScannerConnection
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import android.util.Log
import java.io.IOException
import android.graphics.BitmapFactory
import android.webkit.DownloadListener
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.finger.hsd.MyApplication
import com.finger.hsd.util.Constants


class RealmController(application: Context) {
    val realm: Realm
    private var mupdateData: updateData? = null

    init {
        Realm.init(application)
        realm = Realm.getDefaultInstance()
    }

    //Refresh the realm istance
    fun refresh() {
        realm.refresh()
    }

    fun addProduct(product: Product_v){
        realm.beginTransaction()
        realm.copyToRealm(product)
        realm.commitTransaction()
    }

    //clear all objects from Champion.class
    fun updateorCreateListProduct(product: ArrayList<Product_v>,mupdateData2:updateData){
        this.mupdateData = mupdateData2
        var mCout = 0
        for (i in 0 until product.size){
            if(checkaddsuccess(product[i]?._id!!)!!>0)
            {
                var mProduct_v = getProduct(product[i]._id)
                product[i].imagechanged = mProduct_v?.imagechanged
                realm.beginTransaction()
                realm.copyToRealmOrUpdate(product[i])
                realm.commitTransaction()
                mCout++
                if(mCout==(product.size-1)){
                    mupdateData!!.onupdate()
                }
            }else{
                val mediaStorageDir = getApplicationContext().getExternalFilesDir(null)
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss")
                        .format(Date())
                var path2 = File.separator+ "IMG_" + timeStamp +"_"+ product[i]?.barcode + ".jpg"

                AndroidNetworking.initialize(getApplicationContext(),MyApplication.okhttpclient())
                AndroidNetworking.download(Constants.IMAGE_URL+product[i].imagechanged,mediaStorageDir.path,path2).build().startDownload(object: com.androidnetworking.interfaces.DownloadListener{
                    override fun onDownloadComplete() {
                        Log.d("REALMCONTROLLER","UPDATE SUCCESS")
                        product[i].imagechanged = mediaStorageDir.path+path2
                        realm.beginTransaction()
                        realm.copyToRealmOrUpdate(product[i])
                        realm.commitTransaction()
                        mCout++
                        if(mCout==(product.size)-1){
                            mupdateData!!.onupdate()
                        }
                    }
                    override fun onError(anError: ANError?) {
                        mCout++
                        Log.d("REALMCONTROLLER",anError?.errorDetail+"//ERROR"+anError?.errorBody+"//"+product[i].imagechanged)
                    }
                })
            }

        }
    }

    interface updateData {
        fun onupdate()
    }

    //find all objects in the Champion.class
    fun getlistProduct(): ArrayList<Product_v> {
        return realm.copyFromRealm(realm.where(Product_v::class.java).findAll()) as ArrayList<Product_v>
    }

    //query a single item with the given id
    fun getProduct(id: String): Product_v? {
        return realm.where(Product_v::class.java).equalTo("_id", id).findFirst()
    }
    fun checkaddsuccess(id: String): Long? {
        return realm.where(Product_v::class.java).equalTo("_id", id).count()
    }
    companion object {

        var instance: RealmController? = null
            private set

        fun with(fragment: Fragment): RealmController {

            if (instance == null) {
                instance = RealmController(fragment.activity!!.application)
            }
            return instance as RealmController
        }

        fun with(activity: Activity): RealmController {

            if (instance == null) {
                instance = RealmController(activity.application)
            }
            return instance as RealmController
        }

        fun with(application: Application): RealmController {

            if (instance == null) {
                instance = RealmController(application)
            }
            return instance as RealmController
        }

        fun with(context: Context): RealmController {

            if (instance == null) {
                instance = RealmController(context)
            }
            return instance as RealmController
        }
    }

    //check if Champion.class is empty


    //query example

}
