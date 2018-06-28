package com.finger.hsd.activity

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.view.View
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.finger.hsd.AllInOneActivity
import com.finger.hsd.BaseActivity
import com.finger.hsd.R
import com.finger.hsd.common.GlideApp
import com.finger.hsd.manager.RealmController
import com.finger.hsd.manager.SessionManager
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.User
import com.finger.hsd.presenter.LoginPresenter
import com.finger.hsd.util.AppIntent
import com.finger.hsd.util.Constants
import com.finger.hsd.util.Mylog
import com.finger.hsd.util.Validation.validatePassword
import com.finger.hsd.util.Validation.validatePhone
import com.finger.hsd.util.Validation.validatePhone2
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_register.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

class RegisterActivity : BaseActivity(), LoginPresenter.LoginView {


    var realm: RealmController? = null
    var session: SessionManager? = null
    var rootFolder: File? = null
    private var pending_intent: PendingIntent? = null
    private val milDay = 86400000L
    lateinit var alarmManager: AlarmManager




    val options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.photo_unvailable)
            .error(R.drawable.ic_back)
            .priority(Priority.LOW)


    var user = User()

    var v: View? = null
    override fun isRegisterSuccessful(isRegisterSuccessful: Boolean) {
        if (isRegisterSuccessful) {
            startActivity(Intent(this@RegisterActivity, HorizontalNtbActivity::class.java))
            finish()
        }

    }

    var mRegisterPresenter: LoginPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        realm = RealmController(this)
        session = SessionManager(this)
        rootFolder = File(filesDir.toString() + "/files")
        if (!rootFolder!!.exists()) {
            rootFolder!!.mkdirs()
        }
        realm!!.DatabseLlistAlarm()

        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mRegisterPresenter = LoginPresenter(this)
        btn_join!!.setOnClickListener {
            register()
        }

        btn_backtologin!!.setOnClickListener {
            gotologin()
        }
    }


    private fun register() {

        setError()
        var err = 0

        if (!validatePhone(phone_number!!.text.toString())) {
            err++
            phone_number!!.error = resources.getString(R.string.not_null_case)
        } else {
            if (!validatePhone2(phone_number.text.toString())!!) {
                err++
                phone_number.error = resources.getString(R.string.phone_unvailable)
            }
        }

        if (!validatePassword(txt_password!!.text.toString())) {

            err++
            txt_password!!.error = resources.getString(R.string.not_null_case)
        }

        if (txt_password!!.text.toString() != repass!!.text.toString() || repass!!.text.toString() == "") {

            err++
            repass!!.error = resources.getString(R.string.repass_not_like)
        }


        if (err == 0) {

            showProgress()
            user.phone = phone_number.text.toString()

            user.password = txt_password.text.toString()
            user.tokenfirebase = FirebaseInstanceId.getInstance().token

            mRegisterPresenter!!.register(user, 0)


        } else {
            showSnack(R.string.input_infomation_valid, R.id.root_register)
        }
    }

    private fun gotologin() {

        finish()
    }

    private fun setError() {

        phone_number.error = null
        repass.error = null
        txt_password.error = null
    }
    private  val CHANNEL_APP_STATUS = "CHANNEL_APP_STATUS"

    fun showNotificationWelcome(){
        session!!.setCountNotification(1)
        val intent = Intent(this, AllInOneActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        intent.putExtra("InNotificaitonFragment","InNotificationFragment")


        val pIntent = PendingIntent.getActivity(this, 99 , intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this,CHANNEL_APP_STATUS)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            createNotificationChannel(applicationContext)
            builder
                    .setAutoCancel(true)
                    .setContentTitle(resources.getString(R.string.wellcome_hsd))
                    .setContentText(resources.getString(R.string.wellcome_hsd))
                    .setStyle(NotificationCompat.BigTextStyle()
                            .setSummaryText("HanSuDung")
                            .bigText(resources.getString(R.string.wellcome)))
//                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_hsd))
                    .setColor(resources.getColor(R.color.orange))
                    .setSmallIcon(R.drawable.ic_notificationclolor)
                    .setContentIntent(pIntent)
//                    .setLights(5,5,5)

        } else {

            builder.setAutoCancel(true)
                    .setContentTitle(resources.getString(R.string.wellcome_hsd))
                    .setContentText(resources.getString(R.string.wellcome_hsd))
                    .setStyle(NotificationCompat.BigTextStyle()
                            .setBigContentTitle(title)
                            .bigText(resources.getString(R.string.wellcome)))
//                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_hsd))
                    .setColor(resources.getColor(R.color.orange))
                    .setSmallIcon(R.drawable.ic_notificationclolor)
                    .setContentIntent(pIntent)
        }

        if(!session!!.get_open_Alarm().equals("off")){
            val soundId = resources.getIdentifier(session!!.get_Sound(), "raw", packageName)
            builder.setSound(Uri.parse("android.resource://"
                    + this.packageName + "/" + soundId))
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(99, builder.build())
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
    override fun isLoginSuccessful(isLoginSuccessful: Boolean) {

    }

    override fun setErrorMessage(errorCode: Int, errorBody: Int) {

        hideProgress()
        if (errorCode == 500) {
            if (errorBody == 401) {
                showSnack(R.string.phone_number_used, R.id.root_register)
            } else if (errorBody == 500) {
                showSnack(R.string.not_connect_check, R.id.root_register)
            }
        } else if (errorCode == 404) {
            showSnack(R.string.data_input_empty, R.id.root_register)
        } else {
            showSnack(R.string.not_connect_to_server, R.id.root_register)
        }


    }

    var listProduct: ArrayList<Product_v>? = null
    var temp = 0

    override fun getUserDetail(user: User) {
        // AppManager.saveAccountUser(this, user)
        var list =  realm!!.getDataTimeAlarm()

        for (index in list!!.indices) {

            val model = list.get(index)
            if (model.isSelected!!) {
                SettingAlarm(model.listtime!!.toInt(), true)
            }else{
                SettingAlarm(model.listtime!!.toInt(), false)
            }
        }
        Log.d("isLoginSuccessful","isLoginSuccessful1    "   +"  list   " +user)

        this.user = user

        realm!!.addUser(user)

        listProduct = realm!!.getlistProduct()
        showNotificationWelcome()

        temp = 0
        if (listProduct != null && !listProduct!!.isEmpty()) {

            onDownload(listProduct!!.get(temp))

        } else {
            hideProgress()
            session!!.setLogin(true)
            startActivity(Intent(this@RegisterActivity, AllInOneActivity::class.java))
            val intent = Intent()
            intent.putExtra("isfinishLogin", true)
            intent.action = AppIntent.ACTION_LOGIN
            sendBroadcast(intent)
            finish()
        }

    }

    var percent = 0
    fun onDownload(product: Product_v) {


        GlideApp.with(this)
                .asBitmap()
                .load(Constants.IMAGE_URL + product.imagechanged)
                .apply(options)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        temp++

                        if (listProduct != null && !listProduct!!.isEmpty() && temp < listProduct!!.size) {
                            percent = (temp.toFloat() / listProduct!!.size.toFloat() * 100f).toInt()

                            onDownload(listProduct!!.get(temp))
                        } else {
                            hideProgress()
                            session!!.setLogin(true)
                            startActivity(Intent(this@RegisterActivity, AllInOneActivity::class.java))
                            val intent = Intent()
                            intent.putExtra("isfinishLogin", true)
                            intent.action = AppIntent.ACTION_LOGIN
                            sendBroadcast(intent)
                            finish()
                        }
                    }

                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {

                        try {
                            val namePassive = product._id + "passive" + ".jpg"

                            var myDir = File(rootFolder, namePassive)

                            if (myDir.exists())
                                myDir.delete()

                            val out3 = FileOutputStream(myDir)

                            resource?.compress(Bitmap.CompressFormat.JPEG, 90, out3)
                            Mylog.d("aaaaaaaaaa my dir: " + myDir)
                            product.imagechanged = Uri.fromFile(myDir).toString()
                            realm!!.updateProduct(product)

                            temp++
                            out3.flush()
                            out3.close()

                            if (listProduct != null && !listProduct!!.isEmpty() && temp < listProduct!!.size) {
                                percent = (temp.toFloat() / (listProduct!!.size.toFloat()) * 100f).toInt()

                                showProgress(resources.getString(R.string.sync)+ percent)

                                onDownload(listProduct!!.get(temp))
                            } else {
                                percent = (temp.toFloat() / (listProduct!!.size).toFloat() * 100f).toInt()
                                showProgress(resources.getString(R.string.sync) + percent + " "+ resources.getString(R.string.complete))
                                session!!.setLogin(true)

                                hideProgress()
                                val intent = Intent()
                                intent.putExtra("isfinishLogin", true)
                                intent.action = AppIntent.ACTION_LOGIN
                                sendBroadcast(intent)
                                startActivity(Intent(this@RegisterActivity, AllInOneActivity::class.java))
                                finish()
                            }


                        } catch (e: Exception) {

                            println(e)
                            temp++
                            Mylog.d("aaaaaaaaaa " + temp)
                            if (listProduct != null && !listProduct!!.isEmpty() && temp < listProduct!!.size) {
                                percent = (temp.toFloat() / listProduct!!.size.toFloat() * 100f).toInt()

                                Mylog.d("aaaaaaaaaa chay tiep: " + temp)
                                onDownload(listProduct!!.get(temp))
                            } else {
                                hideProgress()
                                session!!.setLogin(true)
                                startActivity(Intent(this@RegisterActivity, AllInOneActivity::class.java))
                                finish()
                            }
                        }


                    }
                })
    }

    private fun SettingAlarm(hour: Int, boolean: Boolean) {
        val calendars = Calendar.getInstance()
        val now = Calendar.getInstance()
        val myIntent = Intent(this, AlarmReceiver::class.java)
        myIntent.putExtra("extra", "yes")
        calendars.set(Calendar.MINUTE, 0)
        calendars.set(Calendar.SECOND, 0)
        calendars.set(Calendar.HOUR_OF_DAY, hour)

        if (calendars.timeInMillis < now.timeInMillis) {
            calendars.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH) + 1)
        }
        if (boolean) {
            Log.d("LoginActivity", "boolean..true  ====>>>>    " + boolean + " =====  " + hour)
            pending_intent = PendingIntent.getBroadcast(this, hour, myIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendars.timeInMillis, milDay, pending_intent)

        } else {
            Log.d("LoginActivity", "boolean..false  ====>>>>>>    " + boolean + " =====  " + hour)
            pending_intent = PendingIntent.getBroadcast(this, hour, myIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager.cancel(pending_intent)

        }
    }


    public override fun onDestroy() {
        super.onDestroy()
        mRegisterPresenter?.cancelRequest()
    }
}