package com.finger.hsd.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.support.v4.app.Fragment
import android.util.Log
import com.finger.hsd.model.*
import io.realm.Realm
import java.util.*

@Suppress("DEPRECATION")
class RealmAlarmController (application: Context) {
    val timeAlarm : ArrayList<TimeAlarm> = ArrayList<TimeAlarm>()
    val listSound : ArrayList<Sound> = ArrayList<Sound>()
    val realm: Realm

    init {
        realm = Realm.getDefaultInstance()
    }



//     fun view_to_database(): RealmResults<TimeAlarm>? {
//        val result = realm.where(TimeAlarm::class.java).findAllAsync()
//        result.load()
//        var output = ""
//        for (alarm in result) {
//            output += alarm.toString()
//            Log.d("view_to_database = ", "data Realm  ===========  " + output + " \n ")
//        }
//        return result
//    }
//
//    fun update_to_database(alarm : TimeAlarm){
//        realm.beginTransaction()
//        realm.copyToRealmOrUpdate(alarm)
//        realm.commitTransaction()
//    }
//    var id : String? = null
//    var timechoose : String? = null
//    var stampAlam


//    fun save_to_database(  id :  Int , stampAlarm: Boolean?) {
//        Log.d("Realm add ", "check database realm    " +realm.isEmpty)
//        if(!realm.isEmpty){
//            Log.d("Realm Update database ", "realm    " +realm)
//            var alarm = TimeAlarm()
//            alarm.listtime  = id
//            alarm.isSelected = stampAlarm
//            update_to_database(alarm)
//
//        }else{
//
//            realm.executeTransactionAsync({ bgRealm ->
//                var alarm   = bgRealm.createObject(TimeAlarm::class.java, id)
//                alarm.isSelected = stampAlarm
//                Log.d("Realm add ", "Success   " +alarm.listtime)
//            }, {
//                // Transaction was a success.
//                Log.d("Realm add ", "Success >>>>>>>>>>>>>>> OK <<<<<<<<<<<<<<  ")
//                view_to_database()
//
//            }) { error ->
//
//                Log.d("Realm add ", "Fail >>>>>>>>>>>>>> " + error.printStackTrace().toString())
//                // Transaction failed and was automatically canceled.
//            }
//
//        }
//    }
        fun getNotification(): List<Notification>? {
            val result = realm.where(Notification::class.java).findAllAsync()
            result.load()
            var output = ""
            for (alarm in result) {
                output += alarm.toString()
                Log.d("getDataProduct ", "data Realm  ===========  " + output + " \n ")
            }
        return result
        }

    fun getDataProduct(): List<Product_v>? {
        val result = realm.where(Product_v::class.java).findAllAsync()
        result.load()
        var output = ""
        for (alarm in result) {
            output += alarm.toString()
            Log.d("getDataProduct ", "data Realm  ===========  " + output + " \n ")
        }
        return result
    }

    fun DatabseLlistAlarm(){
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
            storeListToRealm(timeAlarm)
            Log.d("Realm add ", "create table Alarm realm >>>>>>>>>>>>>> ")
            // for (index in list.indices)
//            realm.executeTransactionAsync({ bgRealm ->
//                var alarm   = bgRealm.createObject(TimeAlarm::class.java)
//
//
//                timeAlarm
//                Log.d("Realm add ", "Success   " + alarm)
//            }, {
//
//                Log.d("Realm add ", "Success >>>>>>>>>>>>>>> OK <<<<<<<<<<<<<<  ")
////                update_to_timeAlarm(timeAlarm)
////                for(index in timeAlarm.indices){
////                    var alarm = TimeAlarm()
////                    alarm.isSelected  = false
////                    alarm.listtime = timeAlarm[index]
////                    update_to_timeAlarm(alarm)
////                }
//                // Transaction was a success.
                view_to_dataTimeAlarm()
//
//            }) { error ->
//
//                Log.d("Realm add ", "Fail >>>>>>>>>>>>>> " + error.printStackTrace().toString())
//                // Transaction failed and was automatically canceled.
//            }
        }else{
            Log.d("Realm add ", "no create table Alarm realm >>>>>>>>>>>>>> ")

            view_to_dataTimeAlarm()

        }

    }

//    fun UpdateListToRealm(models: List<Sound>) {
//        realm.beginTransaction()
//        realm.copyToRealmOrUpdate(models)
//        realm.commitTransaction()
//    }

    fun addInTableNotification(models: Notification) {

        realm.beginTransaction()
        realm.copyToRealmOrUpdate(models)
        realm.commitTransaction()

    }

    fun storeListToRealm(models: List<TimeAlarm>) {
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(models)
        realm.commitTransaction()

    }

    fun update_to_SetAlarm(timeAlarm : Int, ischeck : Boolean){
        var timeAlarms = TimeAlarm()
        timeAlarms.listtime = timeAlarm
        timeAlarms.isSelected = ischeck

        realm.beginTransaction()
        realm.copyToRealmOrUpdate(timeAlarms)
        realm.commitTransaction()


    }

    fun update_to_timeAlarm(timeAlarm : TimeAlarm){
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(timeAlarm)
        realm.commitTransaction()
    }



    fun view_to_dataTimeAlarm(): List<TimeAlarm>? {
        val result = realm.where(TimeAlarm::class.java).findAllAsync()
        result.load()
        var output = ""
        for (alarm in result) {
            output += alarm.toString()
            Log.d("view_to_dataTimeAlarm ", "data Realm  ===========  " + output + " \n ")
        }
        return result
    }

   // Log.d("NoPlayingService" ," warring countAddNotification "+countAddNotification  +"countExpiry == " + countExpiry)

    fun numberproduct(): Int {
        val result = realm.where(Product_v::class.java).findAllAsync()
        result.load()
        var output = ""
        for (alarm in result) {
            output += alarm.toString()
            Log.d("numberproduct ", "number product   ===========  " + output + " \n ")
        }

        return result.size
    }

    fun view_to_dataNotification(): List<Notification>? {
        val result = realm.where(Notification::class.java).findAllAsync()
        result.load()
        var output = ""
        for (alarm in result) {
            output += alarm.toString()
            Log.d("NoPlayingService ", "view_to_dataNotification  ===========  " + output + " \n ")
        }
        return result
    }
    fun getSingleUser(): User? {
        return realm.where(User::class.java).findFirst()
    }

    fun getUser() : List<User>{
        val result = realm.where(User::class.java).findAllAsync()
        result.load()

        var output = ""
        for (alarm in result) {
            output += alarm.toString()
            Log.d("NoPlayingService ", "getUser  ===========  " + output + " \n ")
        }

        return result
    }

    companion object {
        var instance: RealmAlarmController? = null
            private set

        fun with(fragment: Fragment): RealmAlarmController {
            if (instance == null) {
                instance = RealmAlarmController(fragment.activity!!.application)
            }
            return instance as RealmAlarmController
        }

        fun with(activity: Activity): RealmAlarmController {

            if (instance == null) {
                instance = RealmAlarmController(activity.application)
            }
            return instance as RealmAlarmController
        }

        fun with(application: Application): RealmAlarmController {

            if (instance == null) {
                instance = RealmAlarmController(application)
            }
            return instance as RealmAlarmController
        }

        fun with(context: Context): RealmAlarmController {

            if (instance == null) {
                instance = RealmAlarmController(context)
            }
            return instance as RealmAlarmController
        }
    }

    fun deleteAllData(){
        try {

            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            realm.deleteAll()
            realm.commitTransaction()

            Log.d("deleteAllData ", "delete data realm  ===========  "  + " \n ")

            view_to_dataTimeAlarm()
            getUser()
            realm.close()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    //Refresh the realm istance
    fun refresh() {

        realm.refresh()

    }

    fun closeRealm(){

        realm.close()

    }
}

