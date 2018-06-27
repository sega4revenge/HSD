package com.finger.hsd.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
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
            phone_number!!.error = "Không được để trống trường này!"
        } else {
            if (!validatePhone2(phone_number.text.toString())!!) {
                err++
                phone_number.error = "Số điện thoại không hợp lệ! Hãy thử nhập lại nhé!"
            }
        }

        if (!validatePassword(txt_password!!.text.toString())) {

            err++
            txt_password!!.error = "Không được để trống trường này!"
        }

        if (txt_password!!.text.toString() != repass!!.text.toString() || repass!!.text.toString() == "") {

            err++
            repass!!.error = "Mật khẩu không trùng khớp! Vui lòng nhập lại mật khẩu!"
        }


        if (err == 0) {

            showProgress()
            user.phone = phone_number.text.toString()

            user.password = txt_password.text.toString()
            user.tokenfirebase = FirebaseInstanceId.getInstance().token

            mRegisterPresenter!!.register(user, 0)


        } else {
            showSnack("Vui lòng nhập đầy đủ thông tin hợp lệ!", R.id.root_register)
        }
    }

    private fun gotologin() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }

    private fun setError() {

        phone_number.error = null
        repass.error = null
        txt_password.error = null
    }


    override fun isLoginSuccessful(isLoginSuccessful: Boolean) {

    }

    override fun setErrorMessage(errorCode: Int, errorBody: Int) {

        hideProgress()
        if (errorCode == 500) {
            if (errorBody == 401) {
                showSnack("Số điện thoại đã được sử dụng!", R.id.root_register)
            } else if (errorBody == 500) {
                showSnack("Không có kết nối! vui lòng kiểm tra kết nối internet của bạn!", R.id.root_register)
            }
        } else if (errorCode == 404) {
            showSnack("Dữ liệu nhập vào bị rỗng!", R.id.root_register)
        } else {
            showSnack("Không thể kết nối đến máy chủ, vui lòng kiểm tra lại!", R.id.root_register)
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
        Log.d("isLoginSuccessful","isLoginSuccessful1    "   +"  list   " +list)

        this.user = user

        realm!!.addUser(user)

        listProduct = realm!!.getlistProduct()


        temp = 0
        if (listProduct != null && !listProduct!!.isEmpty()) {

            onDownload(listProduct!!.get(temp))

        } else {
            hideProgress()
            session!!.setLogin(true)
            startActivity(Intent(this@RegisterActivity, AllInOneActivity::class.java))
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

                                showProgress("Sync... " + percent)

                                onDownload(listProduct!!.get(temp))
                            } else {
                                percent = (temp.toFloat() / (listProduct!!.size).toFloat() * 100f).toInt()
                                showProgress("Sync... " + percent + "% complete")
                                session!!.setLogin(true)

                                hideProgress()

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