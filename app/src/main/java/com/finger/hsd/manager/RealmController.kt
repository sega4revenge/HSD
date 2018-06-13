package com.finger.hsd.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.support.v4.app.Fragment
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.facebook.FacebookSdk.getApplicationContext
import com.finger.hsd.common.MyApplication
import com.finger.hsd.model.Group
import com.finger.hsd.model.Notification
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.User
import com.finger.hsd.util.Constants
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class RealmController(application: Context) {
    val realm: Realm
    private var mupdateData: updateData? = null

    init {
        Realm.init(application)
        realm = Realm.getDefaultInstance()
    }

    fun updateProduct(product: Product_v){
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(product)
        realm.commitTransaction()

    }

    fun deleteProduct(id: String){
        realm.beginTransaction()
         var product = realm.where(Product_v::class.java).equalTo("_id", id).findFirst()
        if(product !=null) {
            product.deleteFromRealm()
            realm.commitTransaction()
        }
    }
    fun getNotification() : RealmResults<Notification>{
        var create_at : String? = "create_at"
        var results = realm.where(com.finger.hsd.model.Notification::class.java).findAll()
     //   results = results.sort(created_at, true)
        return results
    }

    fun getOneNotification(id: String) : Notification?{
        return realm.where(Notification::class.java).equalTo("_id", id).findFirst()
    }

    //Refresh the realm istance
    fun refresh() {
        realm.refresh()
    }

    fun getUser(): User?{
        return realm.where(User::class.java).findFirst()
    }
    fun getGroup(): Group?{
        return realm.where(Group::class.java).findFirst()
    }
//    fun insert(data: Data) {
//        }

    fun addUser(user: User){

//        realm.executeTransactionAsync( Realm.Transaction { realm ->
//           // val mData = realm.createObject(Data::class.java)
//            realm.copyToRealm(user)
//        }, Realm.Transaction.OnSuccess {
//            Log.e("setting", "success")
//        } )

        realm.beginTransaction()
        realm.copyToRealmOrUpdate(user)
        realm.commitTransaction()
    }

//===========   NOTIFICATION====================================//
    fun addNotification(notification: Notification){
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(notification)
        realm.commitTransaction()
    }

    //find all object in the Notification.class

    // update notification
    fun updateNotification(_id: String){
        val toEdit = realm.where(Notification::class.java)
                .equalTo("_id", _id).findFirst()
        realm.beginTransaction()
        toEdit!!.watched = true
        realm.commitTransaction()
    }

    // find each element and change watched
    fun setWatchedNotification(){
        var listNotification: List<Notification> =realm.copyFromRealm(realm.where(Notification::class.java).findAll().
                sort("created_at", Sort.DESCENDING)) as ArrayList<Notification>
        for(i in 0..listNotification.size){
            realm.beginTransaction()
            var notification = listNotification.get(i)
            notification.watched = true
            realm.commitTransaction()
        }
    }
    //find all objects in the  table Notification
    fun getListNotification(): ArrayList<Notification>?{

        return realm.copyFromRealm(realm.where(Notification::class.java )
                .findAll().sort("create_at", Sort.DESCENDING)) as ArrayList<Notification>

//        realm.executeTransactionAsync(Realm.Transaction { realm ->
//             listNotification =  realm.copyFromRealm(realm.where(Notification::class.java ).findAll().sort("created_at", Sort.DESCENDING)) as ArrayList<ItemNotification>
//
//        }, Realm.Transaction.OnSuccess {
//            Mylog.d("aaaaaaaa "+ "chay ngay di chay ngay di")
//            for (i in 0..listNotification!!.size-1){
//                var item :  ItemNotification = listNotification!!.get(i)
//                var product = getProduct(item.idproduct.toString())
//                item.namechanged = product!!.namechanged
//                item.imagechanged = product!!.imagechanged
//                item.expiredtime = product!!.expiretime
//
//            }
//        })

//        if(listNotification!=null && !listNotification!!.isEmpty()) {
//            Mylog.d("aaaaaaa control: " + listNotification!!.get(0).content)
//            return listNotification!!
//        }
//        else{
//            return  null
//        }


    }

    // caculate notification watched = true
    fun countNotification(): Int{
        var listNotification: List<Notification> =realm.copyFromRealm(realm.where(Notification::class.java).equalTo("watched", false).findAll()
                ) as ArrayList<Notification>
         if(listNotification.size!=0) {
             return  listNotification.size
         }else{
             return  0
         }
    }
//=========== END NOTIFICATION====================================//
    fun addProduct(product: Product_v){
        realm.beginTransaction()
        realm.copyToRealm(product)
        realm.commitTransaction()
    }
    fun addProductWithNonImage(product: Product_v,mupdateData: updateData){
        this.mupdateData = mupdateData
        val mediaStorageDir = getApplicationContext().getExternalFilesDir(null)
        val timeStamp = System.currentTimeMillis()
        var path2 = File.separator+ "IMG_" + timeStamp +"_"+ product?.barcode + ".jpg"

        AndroidNetworking.initialize(getApplicationContext(), MyApplication.okhttpclient())
        AndroidNetworking.download(Constants.IMAGE_URL+product.imagechanged,mediaStorageDir.path,path2).build().startDownload(object: com.androidnetworking.interfaces.DownloadListener{
            override fun onDownloadComplete() {
                Log.d("REALMCONTROLLER","UPDATE SUCCESS")
                product.imagechanged = mediaStorageDir.path+path2
                realm.beginTransaction()
                realm.copyToRealmOrUpdate(product)
                realm.commitTransaction()
                Log.d("REALMCONTROLLER2",product._id)
                mupdateData.onupdateProduct(1)
            }
                override fun onError(anError: ANError?) {
                    mupdateData.onupdateProduct(0)
                Log.d("REALMCONTROLLER",anError?.errorDetail+"//ERROR"+anError?.errorBody+"//"+product.imagechanged)
            }
        })

    }
    //clear all objects from Champion.class
    fun updateorCreateListProduct(product: ArrayList<Product_v>,mupdateData2:updateData){
        this.mupdateData = mupdateData2
        var mCout = 0
        for (i in 0 until product.size){
            Log.d("REALMCONTROLLER3",product[i]?._id)
            if(checkaddsuccess(product[i]?._id!!)!!>0)
            {
                var mProduct_v = getProduct(product[i]._id)
                product[i].imagechanged = mProduct_v?.imagechanged
                realm.beginTransaction()
                realm.copyToRealmOrUpdate(product[i])
                realm.commitTransaction()

                if(mCout==(product.size-1)){
                    mupdateData!!.onupdate()
                }
                mCout++
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

                        if(mCout==(product.size)-1){
                            mupdateData!!.onupdate()
                        }
                        mCout++
                    }
                    override fun onError(anError: ANError?) {
                        if(mCout==(product.size)-1){
                            mupdateData!!.onupdate()
                        }
                        mCout++
                        Log.d("REALMCONTROLLER",anError?.errorDetail+"//ERROR"+anError?.errorBody+"//"+product[i].imagechanged)
                    }
                })
            }

        }
    }

    interface updateData {
        fun onupdate()
        fun onupdateDelete()
        fun onupdateProduct(type:Int)
    }

    // get user
    fun getUser(id: String): User? {
        return realm.where(User::class.java).equalTo("_id", id).findFirst()
    }

    //delete list product
    fun deletelistproduct(arr: List<Product_v>,mupdateData2:updateData){

        for(i in 0 until arr.size){
            try{
                Log.d("RealmController","error delete :"+arr.get(i))
                arr.get(i).isDelete = true
                realm.beginTransaction()
                realm.copyToRealmOrUpdate(arr)
                realm.commitTransaction()
            }catch (err: Exception){
                Log.d("RealmController","error delete :"+err.message)
            }
        }
        mupdateData2.onupdateDelete()
    }

    //find all objects in the Champion.class
    fun getlistProduct(): ArrayList<Product_v> {
        return realm.copyFromRealm(realm.where(Product_v::class.java).equalTo("delete",false).findAll()) as ArrayList<Product_v>
    }

    fun getlistProductAsync(): ArrayList<Product_v> {
        return realm.copyFromRealm(realm.where(Product_v::class.java).findAll()) as ArrayList<Product_v>
    }

    fun changeImageInUser(user: User){

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
