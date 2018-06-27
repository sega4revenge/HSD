package com.finger.hsd.manager


import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.Uri
import android.support.v4.app.Fragment
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.facebook.FacebookSdk.getApplicationContext
import com.finger.hsd.common.MyApplication
import com.finger.hsd.model.Notification
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.TimeAlarm
import com.finger.hsd.model.User
import com.finger.hsd.util.Constants
import com.finger.hsd.util.Mylog
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList









class RealmController(application: Context) {
    var realm: Realm
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
        }
        realm.commitTransaction()
    }

    fun deleteNotification(id: String){
        realm.beginTransaction()
        var notification = realm.where(Notification::class.java).equalTo("id_product", id).findFirst()
        if(notification !=null) {
            notification.deleteFromRealm()
        }
        realm.commitTransaction()
    }


    fun getNotification() : RealmResults<Notification>{
        var create_at : String? = "create_at"
        var results = realm.where(com.finger.hsd.model.Notification::class.java).findAll()
     //   results = results.sort(created_at, true)
        return results
    }

    fun getOneNotification(idProduct: String) : Notification?{
        return realm.where(Notification::class.java).equalTo("id_product", idProduct).findFirst()
    }

    //Refresh the realm istance
    fun refresh() {
        realm?.refresh()
    }



    fun addUser(user: User){

//        realm.executeTransactionAsync( Realm.Transaction { realm ->
//           // val mData = realm.createObject(Data::class.java)
//            realm.copyToRealm(user)
//        }, Realm.Transaction.OnSuccess {
//            Log.e("setting", "success")
//        } )
        for(ii in 0 until user.listgroup!!.size){
            for(i in 0 until user.listgroup!![ii]!!.listproduct!!.size){
                user.listgroup!![ii]!!.listproduct!![i]?.barcode = user.listgroup!![ii]!!.listproduct!![i]?.producttype_id?.barcode
            }
        }

        realm.beginTransaction()
        realm.copyToRealmOrUpdate(user)
        realm.commitTransaction()
    }

//===========   NOTIFICATION====================================//
    fun addNotification(notification: Notification): Boolean{
        val toEdit = realm.where(Notification::class.java)
                .equalTo("id_product", notification.id_product).findFirst()

        if(toEdit !=null){
            realm.beginTransaction()
            toEdit.watched = false
            toEdit.create_at = notification.create_at
            realm.commitTransaction()
            return  false
        }else{
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(notification)
            realm.commitTransaction()
            return  true
        }



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
    fun listNotWatch():ArrayList<Notification> {

        return realm.copyFromRealm(realm.where(Notification::class.java).findAll().
                sort("create_at", Sort.DESCENDING)) as ArrayList<Notification>

    }

    //find all objects in the  table Notification
    fun getListNotification(): ArrayList<Notification>?{

        return realm.copyFromRealm(realm.where(Notification::class.java ).equalTo("delete", false)
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
        realm?.beginTransaction()
        realm?.copyToRealmOrUpdate(product)
        realm?.commitTransaction()
    }

    fun addProductWithNonImage(product: Product_v,mupdateData: updateData,context: Context){
        var productOld =  getProductWithBarcode(product?.barcode!!)
        if(productOld != null){
            realm?.beginTransaction()
            realm?.copyToRealmOrUpdate(product)
            realm?.commitTransaction()
            mupdateData.onupdateProduct(1, product)

        }else{
            this.mupdateData = mupdateData
            val mediaStorageDir = context.getExternalFilesDir(null)
            val timeStamp = System.currentTimeMillis()
            var path2 = File.separator+ "IMG_" + timeStamp +"_"+ product?.barcode + ".jpg"

            AndroidNetworking.initialize(context, MyApplication.okhttpclient())
            AndroidNetworking.download(product.imagechanged,mediaStorageDir.path,path2).build().startDownload(object: com.androidnetworking.interfaces.DownloadListener{
                override fun onDownloadComplete() {
                    product.imagechanged = mediaStorageDir.path+path2
                    product.barcode = product.producttype_id!!.barcode
                    realm?.beginTransaction()
                    realm?.copyToRealmOrUpdate(product)
                    realm?.commitTransaction()
                    mupdateData.onupdateProduct(1, product)
                }
                override fun onError(anError: ANError?) {
                    product.barcode = product.producttype_id!!.barcode
                    realm?.beginTransaction()
                    realm?.copyToRealmOrUpdate(product)
                    realm?.commitTransaction()
                    mupdateData.onupdateProduct(0,product)
                    Log.d("REALMCONTROLLER",anError?.message+"//ERROR"+anError?.errorBody+"//"+product.imagechanged)
                }
            })
        }
    }
    //clear all objects from Champion.class
    fun updateorCreateListProduct(product: ArrayList<Product_v>, mupdateData2:updateData){
        this.mupdateData = mupdateData2
        var mCout = 0
        for (i in 0 until product.size){
            Log.d("REALMCONTROLLER3",product[i]?._id)
            if(checkaddsuccess(product[i]?._id!!)!!>0)
            {
                var mProduct_v = getProduct(product[i]._id!!)
                product[i].imagechanged = mProduct_v?.imagechanged
                product[i].barcode = product[i].producttype_id!!.barcode
                product[i].delete = false
                product[i].isSyn = true
                realm?.beginTransaction()
                realm?.copyToRealmOrUpdate(product[i])
                realm?.commitTransaction()

                if(mCout==(product.size-1)){
                    mupdateData!!.onupdate()
                }
                mCout++
            }else{
                val mediaStorageDir = getApplicationContext().getExternalFilesDir(null)
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss")
                        .format(Date())
                var path2 = File.separator+ "IMG_" + timeStamp +"_"+ product[i].producttype_id!!.barcode + ".jpg"

                AndroidNetworking.initialize(getApplicationContext(),MyApplication.okhttpclient())
                AndroidNetworking.download(Constants.IMAGE_URL+product[i].imagechanged,mediaStorageDir.path,path2).build().startDownload(object: com.androidnetworking.interfaces.DownloadListener{
                    override fun onDownloadComplete() {
                        Log.d("REALMCONTROLLER","UPDATE SUCCESS")
                        product[i].imagechanged = mediaStorageDir.path+path2
                        product[i].barcode = product[i].producttype_id!!.barcode
                        product[i].delete = false
                        product[i].isSyn = true
                        realm?.beginTransaction()
                        realm?.copyToRealmOrUpdate(product[i])
                        realm?.commitTransaction()

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
        fun onupdateProduct(type:Int,product: Product_v)
    }


    fun getDataTimeAlarm(): List<TimeAlarm>? {
        val result = realm.where(TimeAlarm::class.java).findAllAsync()
        result.load()
        var output = ""
        for (alarm in result) {
            output += alarm.toString()
            Log.d("view_to_dataTimeAlarm ", "data Realm  ===========  " + output + " \n ")
        }
        return result
    }


    fun DatabseLlistAlarm(){
        val timeAlarm : ArrayList<TimeAlarm> = ArrayList<TimeAlarm>()
        val result = realm.where(TimeAlarm::class.java).findAllAsync()
        result.load()
        if(result.size == 0){
            timeAlarm.add(TimeAlarm(0,false))
            timeAlarm.add(TimeAlarm(1,false))
            timeAlarm.add(TimeAlarm(2,false))
            timeAlarm.add(TimeAlarm(3,false))
            timeAlarm.add(TimeAlarm(4,false))
            timeAlarm.add(TimeAlarm(5,false))
            timeAlarm.add(TimeAlarm(6,false))
            timeAlarm.add(TimeAlarm(7,false))
            timeAlarm.add(TimeAlarm(8,false))
            timeAlarm.add(TimeAlarm(9,false))
            timeAlarm.add(TimeAlarm(10,false))
            timeAlarm.add(TimeAlarm(11,false))
            timeAlarm.add(TimeAlarm(12,true))
            timeAlarm.add(TimeAlarm(13,false))
            timeAlarm.add(TimeAlarm(14,false))
            timeAlarm.add(TimeAlarm(15,false))
            timeAlarm.add(TimeAlarm(16,false))
            timeAlarm.add(TimeAlarm(17,false))
            timeAlarm.add(TimeAlarm(18,true))
            timeAlarm.add(TimeAlarm(19,false))
            timeAlarm.add(TimeAlarm(20,false))
            timeAlarm.add(TimeAlarm(21,false))
            timeAlarm.add(TimeAlarm(22,false))
            timeAlarm.add(TimeAlarm(23,false))
            addTimeAlarm(timeAlarm)
            Log.d("Realm add ", "create table Alarm realm >>>>>>>>>>>>>> ")
        }

    }

    fun addTimeAlarm(models: List<TimeAlarm>) {
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(models)
        realm.commitTransaction()

    }


    // get user
    fun getUser(): User? {
        return realm.where(User::class.java).findFirst()
    }

    //delete list product
    fun deletelistproduct(arr: List<Product_v>, mupdateData2:updateData){

        for(i in 0 until arr.size){
            try{
                arr.get(i).delete = true
                realm.beginTransaction()
                realm.copyToRealmOrUpdate(arr)
                realm.commitTransaction()
            }catch (err: Exception){
                Log.d("RealmController","error delete :"+err.message)
            }
        }
        mupdateData2.onupdateDelete()
    }


    // delete listproduct from server result -> sucess
    fun deletelistproductfromserversucess(arr: List<Product_v>, mupdateData2:updateData){

        for(i in 0 until arr.size){
            try{

                if (arr.get(i).imagechanged !=null) {
                    var numCount = realm.where(Product_v::class.java).equalTo("imagechanged", arr.get(i).imagechanged).count()
                    if(numCount == 1L)
                    {
                        val fdelete = File(Uri.parse(arr.get(i).imagechanged).path)
                        if (fdelete.exists()) {
                            System.out.println(arr.get(i).imagechanged+"exists()")
                            if (fdelete.delete()) {
                                System.out.println("file Del`eted :")
                            } else {
                                System.out.println("file not Deleted :")
                            }
                        }else{
                            Mylog.d("Don't Find File!! "+fdelete.absoluteFile+"//"+fdelete.exists())
                        }
                    }
                }

                realm.beginTransaction()
                var product = realm.where(Product_v::class.java).equalTo("_id", arr.get(i)._id).findFirst()
                if(product !=null) {
                    product.deleteFromRealm()
                }
                realm.commitTransaction()

//                if(arr.get(i) !=null) {
//                    Log.d("zzzzzzzzzzzzz","Deleted success")
//                    arr.get(i).deleteFromRealm()
//                }
            }catch (err: Exception){
                Log.d("RealmController","error delete DEEEEEEEEE :"+err.message)
            }
        }
        mupdateData2.onupdateDelete()
    }
    // delete listnotifcation  when deletelistproductfromserversucess from server result -> success
    fun deletelistnotificationfromserversucess(arr: List<Product_v>){

        for(i in 0 until arr.size){
            try{
                realm.beginTransaction()
                var notification = realm.where(Notification::class.java).equalTo("id_product", arr.get(i)._id).findFirst()
                if(notification !=null) {
                    notification!!.deleteFromRealm()
                }
                realm.commitTransaction()

            }catch (err: Exception){
                Log.d("RealmController","error delete notification:"+err.message)
            }
        }

    }

    //== delete listProduct from server result -> FAIL
    fun deletelistproductfromserverFail(arr: List<Product_v>, mupdateData2:updateData){

        for(i in 0 until arr.size){
            try{
                realm.beginTransaction()
                var product = arr.get(i)
                product.delete = true
                product.isSyn = false
                realm.commitTransaction()

            }catch (err: Exception){
                Log.d("RealmController","error delete :"+err.message)
            }
        }
        mupdateData2.onupdateDelete()
    }

    //=== delete listNotification  when deletelistproductfromserversucess  from server result -> FAIL
    fun deletelistnotificationfromserverFail(arr: List<Product_v>){

        for(i in 0 until arr.size){
            try{
                realm.beginTransaction()
                var notification = realm.where(Notification::class.java).equalTo("id_product", arr.get(i)._id).findFirst()
                if(notification !=null) {
                  notification.delete = true
                    notification.isSync = false
                }
                realm.commitTransaction()

            }catch (err: Exception){
                Log.d("RealmController","error delete :"+err.message)
            }
        }

    }

    //delete list product
    fun deleteProductFromDevice(product : Product_v,mupdateData2:updateData){
                realm.beginTransaction()
                product.deleteFromRealm()
                realm.commitTransaction()
    //   mupdateData2.onupdateDelete()
    }

    fun getlistProduct(): ArrayList<Product_v> {
        return realm.copyFromRealm(realm.where(Product_v::class.java).equalTo("delete",false).findAll()) as ArrayList<Product_v>
    }

    fun getlistProductLike(search: String): ArrayList<Product_v> {
        return realm.copyFromRealm(realm.where(Product_v::class.java).equalTo("delete",false).and().contains("namechanged", search).or().contains("barcode",search).findAll()) as ArrayList<Product_v>
    }

    fun getListProductNotSync(): ArrayList<Product_v>{
        return realm.copyFromRealm(realm.where(Product_v::class.java).equalTo("isSyn", false).findAll()) as ArrayList<Product_v>
    }

    fun getListNotificationNotSync(): ArrayList<Notification>{

        return realm.copyFromRealm(realm.where(Notification::class.java).equalTo("isSync", false)
                .findAll()) as ArrayList<Notification>

    }

    fun getlistProductAsync(): ArrayList<Product_v> {
        return realm.copyFromRealm(realm.where(Product_v::class.java).findAll()) as ArrayList<Product_v>
    }

    fun changeImageInUser(user: User){

    }

    //find all objects in the Champion.class
    fun getlistProductOffline(): ArrayList<Product_v> {
        return ArrayList(realm.where(Product_v::class.java).equalTo("delete",false).and().equalTo("isSyn",false).findAll())
    }

    //query a single item with the given id
    fun getProduct(id: String): Product_v? {
        return realm.where(Product_v::class.java).equalTo("_id", id).findFirst()
    }
//  sort("create_at", Sort.DESCENDING)
    fun getProductWithBarcode(barcode: String): Product_v? {
        return realm.where(Product_v::class.java).equalTo("barcode", barcode).findFirst()
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
