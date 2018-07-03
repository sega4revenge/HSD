package com.finger.hsd.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.finger.hsd.AllInOneActivity
import com.finger.hsd.R
import com.finger.hsd.activity.HorizontalNtbActivity
import com.finger.hsd.fragment.NotificationFragment
import com.finger.hsd.manager.RealmAlarmController
import com.finger.hsd.manager.SessionManager
import com.finger.hsd.model.Notification
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.Response
import com.finger.hsd.util.AppIntent
import com.finger.hsd.util.ConnectivityChangeReceiver
import com.finger.hsd.util.Constants
import com.finger.hsd.util.Mylog
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import me.leolin.shortcutbadger.ShortcutBadger
import org.json.JSONObject
import java.util.*

@Suppress("DEPRECATION")
@SuppressLint("Registered")

class NotificationPlayingService : Service() {
    var realms : RealmAlarmController?= null
    private  val CHANNEL_APP_STATUS = "CHANNEL_APP_STATUS"
    internal var product_v : List<Product_v> = ArrayList<Product_v>()
    internal var notification_v : List<Notification> = ArrayList<Notification>()
    private val milDay = 86400000L
    lateinit var sessionManager : SessionManager
    var mNotificationBadgeListener: NotificationFragment.NotificationBadgeListener? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    fun onAttachToContext(context: Context) {
        if (context is NotificationFragment.NotificationBadgeListener) {
            mNotificationBadgeListener = context

        }
    }
    override fun onStartCommand(intent : Intent?, flags : Int, startId : Int): Int {
        realms = RealmAlarmController(this)
        val now = Calendar.getInstance()
        product_v = realms!!.getDataProduct()!!
        notification_v = realms!!.getNotification()!!
        sessionManager = SessionManager(applicationContext)
        onAttachToContext(this)
        var day : Int?
        val notificationId = Random().nextInt()
        var countWarnings = 0
        var countExpiry = 0
        var product_expiry : String? = ""
        var product_warring : String? = ""

        for (index in  product_v.indices){
            val now = Calendar.getInstance()
            now.set(Calendar.HOUR, 0)
            now.set(Calendar.MINUTE, 0)
            now.set(Calendar.SECOND, 0)
            var countAddNotification  = 0
            day = ((product_v[index].expiretime - now.timeInMillis  )/milDay).toInt()
            var id_notification : String?

            if(day <= 0 && day > -3){
                countExpiry++
                if (!product_expiry.equals("")) {
                    product_expiry += ", "
                }
                // lay name san pham
                product_expiry += product_v[index].namechanged.toString()
                // check san pham da từng thông báo
                id_notification = product_v[index]._id
//                for (dex in notification_v.indices){
//
//                    Log.d("NotificationService " ," for-===  == " +(product_v[index]._id ) + "  <  --------- > "+ notification_v[dex]._id)
//                    if(product_v[index]._id === notification_v[dex].id_product){
//
//                        countAddNotification=0
//                        id_notification = notification_v[dex]._id // neu da ton tai thong bao truoc do - > lay _id notification update
//                        Log.d("NotificationService " ," check id product == id notification da ton tai  == " +(product_v[index]._id ) + "  <  --------- > "+ notification_v[dex]._id)
//                    } else{
//
//                        Log.d("NotificationService " ," check id product != id notification khong ton tai  == " +(product_v[index]._id ) + "  <  --------- > "+ notification_v[dex]._id)
//
//                        countAddNotification ++
//                        id_notification = product_v[index]._id //create _id notification new
//
//                    }
//
//                }
                // sản phẩm chưa được add
//                if(countAddNotification > 0 ){
                    var notification_new = Notification()
//                    notification_new._id = id_notification
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
//                    bundle.putSerializable("notificationModel", notification_new)
                    bundle.putBoolean("addnotification", true)
                    intent.putExtras(bundle)
                    sendBroadcast(intent)
//                }
                // the second update notification to server
                if (ConnectivityChangeReceiver.isConnected()) {
                    updateNotficationOnServer(notification_new)
                }else{
                    notification_new.isSync = false
                    realms!!.addInTableNotification(notification_new)
                    }
//                }


            }else if(day <= product_v[index].daybefore && day > 0) {

                countWarnings++
                if (!product_warring.equals("")) {
                    product_warring += ", "
                }

                product_warring += product_v[index].namechanged.toString()
                Log.d("NotificationService " ," notification_v.size -===  == " +notification_v.size)
                id_notification = product_v[index]._id
//                for (dex in notification_v.indices) {
////                    Log.d("NotificationService " ," for-===  == " +(product_v[index]._id ) + "  <  --------- > "+ notification_v[dex]._id)
//
//                    if (product_v[index]._id == notification_v[dex].id_product) {
//                        countAddNotification = 0
////                        id_notification = notification_v[dex]._id
//                        Log.d("NotificationService ", " check id product == id notification da ton tai  warring== " + (product_v[index]._id) + "  <  --------- > " + notification_v[dex]._id)
//                    } else {
//                        id_notification = product_v[index]._id
//                        countAddNotification++
//                        Log.d("NotificationService ", " check id product != id notification khong ton tai warring == " + (product_v[index]._id) + "  <  --------- > " + notification_v[dex]._id)
//                    }
//
//
//                }

                Log.d("NotificationService", " warring id_notification " + id_notification)

//                if(countAddNotification > 0){
                var notification_new = Notification()
                    notification_new.id_product = product_v[index]._id
//                notification_new._id = id_notification
                notification_new.idinvite = null
                notification_new.status_expiry = "warning"
                notification_new.idgeneral = null
                notification_new.type = 0
                notification_new.create_at = now.timeInMillis.toString()
                notification_new.watched = false
                notification_new.isSync = false
                realms!!.addInTableNotification(notification_new)
                realms!!.view_to_dataNotification()

                var intent = Intent()
                var bundle = Bundle()
                intent.action = AppIntent.ACTION_UPDATE_ITEM
//                    bundle.putSerializable("notificationModel", notification_new)
                bundle.putBoolean("addnotification", true)
                intent.putExtras(bundle)
                sendBroadcast(intent)

                // the second update notification to server
                if (ConnectivityChangeReceiver.isConnected()) {
                    updateNotficationOnServer(notification_new)
                } else {
                    notification_new.isSync = false
                    realms!!.addInTableNotification(notification_new)

//                }
            }

            }
            // ============= end else =====================



        }

        var count = sessionManager.getCountNotification() + countExpiry + countWarnings
        //count notification
        sessionManager.setCountNotification(count)

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


        return Service.START_STICKY
    }

    // count notification on home screen
    fun badgeIconScreen() {
        var badgeCount = 0

        if (sessionManager.getCountNotification() > 0) {
            try {
                badgeCount = sessionManager.getCountNotification()
            } catch (e: NumberFormatException) {
                Mylog.d("badge Count screen: ", e.message.toString())
            }

            ShortcutBadger.applyCount(applicationContext, badgeCount)
        } else {
            ShortcutBadger.removeCount(applicationContext)
        }

    }

    fun createNotificationChannel(context: Context) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = notificationManager.getNotificationChannel(CHANNEL_APP_STATUS)
            if (notificationChannel == null) {
                val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val description = "......."
                val importance = NotificationManager.IMPORTANCE_HIGH
                val mChannel = NotificationChannel(CHANNEL_APP_STATUS, "Foreground Service", importance)
                mChannel.description = description
                mChannel.enableLights(true)
                mChannel.lightColor = Color.RED
                mChannel.enableVibration(true)
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                mNotificationManager.createNotificationChannel(mChannel)
            }
        }

    }
    // group of notification
    fun Group_Notification( title:String,  content : String){
        createNotificationChannel(applicationContext)
        realms!!.view_notification()
        Mylog.d("Group_Notification: ", "  content  "+title +"  content  " + content )
        val intent = Intent(this, AllInOneActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        intent.putExtra("InNotificaitonFragment","InNotificationFragment")

        val pIntent = PendingIntent.getActivity(this, 99 , intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this,CHANNEL_APP_STATUS)

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            builder
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .setSummaryText("HanSuDung")
                            .bigText(content))
//                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_hsd))
                    .setColor(resources.getColor(R.color.orange))
                    .setSmallIcon(R.drawable.ic_notificationclolor)
                    .setContentIntent(pIntent)
//                    .setLights(5,5,5)
        } else {
            builder.setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .setBigContentTitle(title)
                            .bigText(content))
//                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_hsd))
                    .setColor(resources.getColor(R.color.orange))
                    .setSmallIcon(R.drawable.ic_notificationclolor)
                    .setContentIntent(pIntent)
        }

        if(!sessionManager.get_open_Alarm().equals("off")){
            val soundId = resources.getIdentifier(sessionManager.get_Sound(), "raw", packageName)
            builder.setSound(Uri.parse("android.resource://"
                    + this.packageName + "/" + soundId))
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(99, builder.build())

        ShortcutBadger.applyNotification(applicationContext, builder.notification, sessionManager.getCountNotification())
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

    fun updateNotficationOnServer(notification: Notification){
        var user = realms!!.getSingleUser()
        var jsonObject = JSONObject()
        try {
            jsonObject.put("id_product", notification.id_product)
            jsonObject.put("idUser", user?._id)
            jsonObject.put("type", notification.type)
            jsonObject.put("watched", notification.watched)
            jsonObject.put("time", notification.create_at)
            jsonObject.put("status_expired", notification.status_expiry)
        }catch (e:Exception){
            Mylog.d("Error "+e.message)
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
                .subscribeWith(object : DisposableObserver<Response>(){
                    override fun onComplete() {

                    }

                    override fun onNext(t: Response?) {
                        realms!!.addInTableNotification(notification)
                        Mylog.d("aaaaaaaaaaaaa "+notification)
                    }

                    override fun onError(e: Throwable?) {
                        Mylog.d(e?.printStackTrace().toString())
                        notification.isSync = false
                        realms!!.addInTableNotification(notification)
                        Mylog.d("aaaaaaaaaaaaa "+notification)

                    }

                })

    }

    override fun onDestroy() {
        stopForeground(true)
        Mylog.d("aaaaaaaaaaaaa "+ "onDestroy")
        super.onDestroy()
    }


}

