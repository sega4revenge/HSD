package com.finger.hsd.services

import android.annotation.SuppressLint

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri

import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.finger.hsd.AllInOneActivity
import com.finger.hsd.R

import com.finger.hsd.activity.HorizontalNtbActivity
import com.finger.hsd.manager.RealmAlarmController
import com.finger.hsd.manager.SessionManager
import com.finger.hsd.model.General
import com.finger.hsd.model.Invite
import com.finger.hsd.model.Notification
import com.finger.hsd.model.Product_v
import com.finger.hsd.util.AppIntent
import java.util.*

@Suppress("DEPRECATION")
@SuppressLint("Registered")
class NotificationPlayingService : Service() {
    var realms : RealmAlarmController?= null
    internal var product_v : List<Product_v> = ArrayList<Product_v>()
    internal var notification_v : List<Notification> = ArrayList<Notification>()
    private val milDay = 86400000L
    lateinit var sessionManager : SessionManager
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onStartCommand(intent : Intent, flags : Int, startId : Int): Int {
        realms = RealmAlarmController(this)
        val now = Calendar.getInstance()
        product_v = realms!!.getDataProduct()!!
        notification_v = realms!!.getNotification()!!
        sessionManager = SessionManager(applicationContext)

        var day : Int?
        val notificationId = Random().nextInt()
        var countWarnings = 0
        var countExpiry = 0

        var product_expiry : String? = ""
        var product_warring : String? = ""

        Log.d("NoPlayingService " ," 000 " + "countExpiry == " + countExpiry + "  countWarnings == " + countWarnings )

        for (index in  product_v.indices){
            val now = Calendar.getInstance()
            now.set(Calendar.HOUR, 0)
            now.set(Calendar.MINUTE, 0)
            now.set(Calendar.SECOND, 0)
            var countAddNotification  = 0
            day = ((product_v[index].expiretime - now.timeInMillis  )/milDay).toInt()
            Log.d("NoPlayingService" ," day " + day)
            var id_notification : String?= null

            if(day <= 0 && day > -3){
                countExpiry++
                if (!product_expiry.equals("")) {
                    product_expiry += ", "
                }
                product_expiry += product_v[index].namechanged.toString()

                for (dex in notification_v.indices){
                    if(product_v[index]._id == notification_v[dex].id_product){
                        countAddNotification=0
                        id_notification = notification_v[dex]._id // neu da ton tai thong bao truoc do - > lay _id notification update

                    } else{

                        countAddNotification ++
                        id_notification = product_v[index]._id //create _id notification new
                    }
                }
                Log.d("NoPlayingService" ," Expiry id_notification "+id_notification)

                if(countAddNotification > 0 ){
                    var notification_new = Notification()
                    notification_new._id = id_notification
                    notification_new.idinvite = null
                    notification_new.id_product = product_v[index]._id
                    notification_new.status_expiry = "expired"
                    notification_new.idgeneral = null
                    notification_new.type = 0
                    notification_new.create_at = now.timeInMillis.toString()
                    notification_new.watched = false
//                        realms!!.addInTableNotification(notification_new)
//                        realms!!.view_to_dataNotification()
                    var intent = Intent()
                    var bundle = Bundle()
                    intent.action = AppIntent.ACTION_UPDATE_ITEM
                    bundle.putSerializable("notificationModel", notification_new)
                    bundle.putBoolean("addnotification", true)
                    intent.putExtras(bundle)
                    sendBroadcast(intent)
                }


            }else if(day <= product_v[index].daybefore && day > 0){
                countWarnings++
                if (!product_warring.equals("")) {
                    product_warring += ", "
                }

                product_warring += product_v[index].namechanged.toString()

                for (dex in notification_v.indices){
                    if(product_v[index]._id == notification_v[dex].id_product){
                        countAddNotification = 0
                        id_notification = notification_v[dex]._id
                    }else{
                        id_notification = product_v[index]._id
                        countAddNotification++
                    }
                }
                Log.d("NoPlayingService" ," warring id_notification "+id_notification)

                if(countAddNotification > 0){
                    var notification_new = Notification()
                    notification_new._id = id_notification
                    notification_new.idinvite = null
                    notification_new.id_product = product_v[index]._id
                    notification_new.status_expiry = "warring"
                    notification_new.idgeneral = null
                    notification_new.type = 0
                    notification_new.create_at = now.timeInMillis.toString()
                    notification_new.watched = false
//                        realms!!.addInTableNotification(notification_new)
//                        realms!!.view_to_dataNotification()
                    var intent = Intent()
                    var bundle = Bundle()
                    intent.action = AppIntent.ACTION_UPDATE_ITEM
                    bundle.putSerializable("notificationModel", notification_new)
                    bundle.putBoolean("addnotification", true)
                    intent.putExtras(bundle)
                    sendBroadcast(intent)



                    countAddNotification = 0
                }

            }



        }
        Log.d("NoPlayingService" ," 333 " +"countExpiry == " + countExpiry + "countWarnings == " + countWarnings )
        // chi co san pham het han
        if(countExpiry > 0 && countWarnings== 0){
            var strTitle : String = resources.getString(R.string.notification_expiry)
            var strcontent : String = resources.getString(R.string.You_have) + " ''" + countExpiry + "'' "+resources.getString(R.string.product_expiry)+ " " + product_expiry + " "+
                    resources.getString(R.string.expiry)+ " "+ resources.getString(R.string.please_check_product)
            Group_Notification(strTitle,strcontent)

         // chi co san pham canh bao
        }else if(countWarnings>0 && countExpiry == 0){

            var strTitle : String = resources.getString(R.string.warring_expiry)
            var strcontent : String = resources.getString(R.string.Warring_You_have) + " ''" + countWarnings + "'' "+resources.getString(R.string.product_expiry)+ " " + product_warring + " "+
                    resources.getString(R.string.expiry_)+ " "+ resources.getString(R.string.please_check_product)
            Group_Notification(strTitle,strcontent)

         // san pham het han va canh bao
        }else if(countExpiry >0 && countWarnings > 0){
            var strTitle : String = resources.getString(R.string.notification_)
            var strcontent : String = resources.getString(R.string.You_have) + " ''" + countExpiry + "'' "+resources.getString(R.string.product_expiry)+ " " + product_expiry + " "+
                    resources.getString(R.string.expiry_and)+" ''"+countWarnings +"'' "+resources.getString(R.string.product_expiry)+ " " + product_warring + " "+
                    resources.getString(R.string.expiry_)+" "+ resources.getString(R.string.please_check_product)
            Group_Notification(strTitle,strcontent)

        }


        return Service.START_NOT_STICKY
    }



    fun Group_Notification( title:String,  content : String){

        val intent = Intent(this, AllInOneActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        intent.putExtra("InNotificationFragment","InNotificationFragment")


        val pIntent = PendingIntent.getActivity(this, 99 , intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder
                    .setAutoCancel(true)
                    .setContentTitle(title)
//                    .setContentText(content)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .setBigContentTitle(title)
                            .setSummaryText("Han Su Dung")
                            .bigText(content))
                    .setColor(resources.getColor(R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pIntent)
                    .setLights(5,5,5)

        } else {

            builder.setAutoCancel(true)
                    .setContentTitle(title)
//                    .setContentText(content)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .setBigContentTitle(title)
                            .bigText(content))
                    .setColor(resources.getColor(R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pIntent)
        }

        if(!sessionManager.get_open_Alarm().equals("off")){
            val soundId = resources.getIdentifier(sessionManager.get_Sound(), "raw", packageName)
            builder.setSound(Uri.parse("android.resource://"
                    + this.packageName + "/" + soundId))
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(99, builder.build())
    }

    fun Notification_Expiry( title:String, content : String ,  int: Int , day : Int ){
        val intent = Intent(this, HorizontalNtbActivity::class.java)
        val pIntent = PendingIntent.getActivity(this, int, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        var strTitle : String = resources.getString(R.string.notification_expiry)+" ''"+title + "''."
        var content_noti : String = resources.getString(R.string.product_expiry) + " ''" + content + "'' "+
                resources.getString(R.string.expired)+ " "+
                -day +" "+ resources.getString(R.string.day)

        val builder = NotificationCompat.Builder(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder
                    .setAutoCancel(true)
                    .setContentTitle(strTitle)
                    .setContentText(content_noti)
                    .setColor(resources.getColor(R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pIntent)
                    .setLights(5,5,5)



        } else {

            builder.setAutoCancel(true)
                    .setContentTitle(strTitle)
                    .setContentText(content_noti)
                    .setColor(resources.getColor(R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pIntent)
        }
//    val soundId = resources.getIdentifier(session.getSoundSuri(), "raw", packageName)
//    builder.setSound(Uri.parse("android.resource://"
//            + this.packageName + "/" + soundId))
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(int, builder.build())

    }

    fun WaringNotification( title:String, content : String ,  int: Int , day :Int ){

        val intent = Intent(this, HorizontalNtbActivity::class.java)
        val pIntent = PendingIntent.getActivity(this, int, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        var strTitle : String = resources.getString(R.string.warring_expiry)+" ''"+title + "''."

        var content_noti : String = resources.getString(R.string.warring_expiry) + " ''" + content + "'' "+
                resources.getString(R.string.con)+ " "
                day.toString() +" "+ resources.getString(R.string.expiring)

        val builder = NotificationCompat.Builder(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder
                    .setAutoCancel(true)
                    .setContentTitle(strTitle)
                    .setContentText(content_noti)
                    .setColor(resources.getColor(R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pIntent)
                    .setLights(5,5,5)



        } else {

            builder.setAutoCancel(true)
                    .setContentTitle(strTitle)
                    .setContentText(content_noti)
                    .setColor(resources.getColor(R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pIntent)
        }
//    val soundId = resources.getIdentifier(session.getSoundSuri(), "raw", packageName)
//    builder.setSound(Uri.parse("android.resource://"
//            + this.packageName + "/" + soundId))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(int, builder.build())

    }


//    @SuppressLint("NewApi")
//    fun ShowNotification( title:String, content : String ,  int: Int ){
//        val mNM = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        // request_code
//        val intent1 = Intent(this, HorizontalNtbActivity::class.java)
//        //        int request_code = intent.getExtras().getInt("request_code");
//
//        val pIntent = PendingIntent.getActivity(this, int, intent1, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        val mNotify = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            Notification.Builder(this)
//                    .setContentTitle(title)
//                    .setContentText(content)
//                    .setSmallIcon(R.drawable.ic_notification)
//                    .setContentIntent(pIntent)
//                    .setAutoCancel(true)
//                    .build()
//
////            val soundId = resources.getIdentifier(session.getSoundSuri(), "raw", packageName)
////
////
////            mNM.setSound(Uri.parse("android.resource://"
////                    + this.packageName + "/" + soundId))
//
//        } else {
//
//            Notification.Builder(this)
//                    .setContentTitle(title)
//                    .setContentText(content)
//                    .setSmallIcon(R.drawable.ic_notification)
//                    .setContentIntent(pIntent)
//                    .setAutoCancel(true)
//                    .build()
//        }
//        mNM.notify(int, mNotify)
//
//    }
}

