package com.finger.hsd.activity

import android.accounts.Account
import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.finger.hsd.AllInOneActivity
import com.finger.hsd.BaseActivity
import com.finger.hsd.R
import com.finger.hsd.common.GlideApp
import com.finger.hsd.common.MyApplication
import com.finger.hsd.manager.RealmController
import com.finger.hsd.manager.SessionManager
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.User
import com.finger.hsd.presenter.LoginPresenter
import com.finger.hsd.util.AppIntent
import com.finger.hsd.util.Constants
import com.finger.hsd.util.Mylog
import com.finger.hsd.util.Validation.validatePhone
import com.finger.hsd.util.Validation.validatePhone2
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class LoginActivity : BaseActivity(), LoginPresenter.LoginView, GoogleApiClient.OnConnectionFailedListener {


    var account: Account? = null
    var user = User()
    var TAG = "Login Activity"

    var realm: RealmController? = null
    lateinit var alarmManager: AlarmManager
    private val milDay = 86400000L
    private var pending_intent: PendingIntent? = null
    lateinit var session: SessionManager

    private var callbackManager: CallbackManager? = null
    val options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_add_photo)
            .error(R.drawable.ic_back)
            .priority(Priority.LOW)

    var v: View? = null
    private val RC_SIGN_IN = 7
    var mLoginPresenter: LoginPresenter? = null
    var type: Int = 0
    var rootFolder: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        registerReceiver(this.broadcastLogin, IntentFilter(AppIntent.ACTION_LOGIN))
        callbackManager = CallbackManager.Factory.create()

        setContentView(R.layout.activity_login)

        getKeyHash()
        realm = RealmController(this)
        rootFolder = File(filesDir.toString() + "/files")
        if (!rootFolder!!.exists()) {
            rootFolder!!.mkdirs()
        }
        session = SessionManager(this)
        if (session.isLogin()) {
            val intent = Intent(this, AllInOneActivity::class.java)
            startActivity(intent)
            finish()
        }

        realm!!.DatabseLlistAlarm()

        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        AppEventsLogger.activateApp(this)
        mLoginPresenter = LoginPresenter(this)

        btn_login!!.setOnClickListener {

            login()
        }
        btn_toRegister!!.setOnClickListener {
            gotoregister()
        }
//================================fogot==============================
//        btn_forgot!!.setOnClickListener {
//            gotoforgot()
//        }
        //==============================================================


        // login facebook
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                ) { _, response ->
                    // Application code

                    try {
                        Log.i("Response", response.toString())

                        val tokenfirebase = FirebaseInstanceId.getInstance().token
                        val id = response.jsonObject.getString("id")
                        user.phone = id
                        user.password = ""
                        user.tokenfirebase = (tokenfirebase)
                        showProgress()
                        mLoginPresenter!!.register(user, 1)


                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,email,first_name,last_name,gender, birthday, name,picture")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                println("loi cmnr")
            }

            override fun onError(error: FacebookException) {
                Log.i("Error", error.message)
            }
        })
        btn_facebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
        }



        btn_google.setOnClickListener {
            //            showProgress()
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(MyApplication.getGoogleApiHelper()!!.googleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    fun getKeyHash() {
        try {
            val info = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }
    }



    private fun login() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(password.getWindowToken(), 0)
        setError()

        var err = 0
        if (!validatePhone(phone_number_login!!.text.toString())) {
            err++
            phone_number_login!!.error = resources.getString(R.string.not_null_case)
        } else {
            if (!validatePhone2(phone_number_login.text.toString())!!) {
                err++
                phone_number_login.error = resources.getString(R.string.phone_unvailable)
            }
        }
        if (err == 0) {
            showProgress(resources.getString(R.string.processing))
            user.phone = phone_number_login.text.toString()
            user.password = password.text.toString()
            user.tokenfirebase = FirebaseInstanceId.getInstance().token
            mLoginPresenter!!.login(user)
        } else {
            showSnack(R.string.phone_or_pass_not_avali, R.id.root_login)

        }
    }

    private fun setError() {
        phone_number_login.error = null

    }


    override fun isLoginSuccessful(isLoginSuccessful: Boolean) {
        if (isLoginSuccessful) {
            startActivity(Intent(this@LoginActivity, AllInOneActivity::class.java))
            finish()
        } else {

            println("loi")
        }
    }

    override fun isRegisterSuccessful(isRegisterSuccessful: Boolean) {
        if (isRegisterSuccessful) {
            startActivity(Intent(this@LoginActivity, AllInOneActivity::class.java))
            finish()
        } else {
            println("loi")
        }
    }

    override fun setErrorMessage(errorCode: Int, errorBody: Int) {
        hideProgress()
        // errorCode: 500: 404: user chua dang ky
        // 401: sai mat khau
        // 500 loi server
        if (errorCode == 500) {
            if (errorBody == 404)
                showSnack(R.string.user_not_register, R.id.root_login)
            else if (errorBody == 401) {
                showSnack(R.string.pass_wrong, R.id.root_login)
            } else if (errorBody == 500) {
                showSnack(R.string.not_connect_check, R.id.root_login)
            }
        } else {
            showSnack(R.string.not_connect_to_network, R.id.root_login)
        }

    }

    var listProduct: ArrayList<Product_v>? = null
    var temp = 0
    override fun getUserDetail(user: User) {


        var list = realm!!.getDataTimeAlarm()

        for (index in list!!.indices) {

            val model = list.get(index)
            if (model.isSelected!!) {
                SettingAlarm(model.listtime!!.toInt(), true)
            } else {
                SettingAlarm(model.listtime!!.toInt(), false)
            }
        }

        realm!!.addUser(user)
        listProduct = realm!!.getlistProduct()


        temp = 0
        if (listProduct != null && !listProduct!!.isEmpty()) {
            // get image
            onDownload(listProduct!!.get(temp))

        } else {
            // khong co du lieu
            showNotificationWelcome()
            hideProgress()
            session.setLogin(true)
            startActivity(Intent(this@LoginActivity, AllInOneActivity::class.java))
            finish()
        }


    }

    private val CHANNEL_APP_STATUS = "CHANNEL_APP_STATUS"
    fun showNotificationWelcome() {
        session.setCountNotification(1)

        val intent = Intent(this, AllInOneActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        intent.putExtra("InNotificaitonFragment", "InNotificationFragment")


        val pIntent = PendingIntent.getActivity(this, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, CHANNEL_APP_STATUS)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            createNotificationChannel(applicationContext)
            builder
                    .setAutoCancel(true)
                    .setContentTitle(resources.getString(R.string.wellcome_hsd))
                    .setContentText(resources.getString(R.string.wellcome_hsd))
                    .setStyle(NotificationCompat.BigTextStyle()
                            .setSummaryText("HanSuDung")
                            .bigText(resources.getString(R.string.wellcome_hsd)))
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

        if (!session.get_open_Alarm().equals("off")) {
            val soundId = resources.getIdentifier(session.get_Sound(), "raw", packageName)
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


    var percent = 0

    fun onDownload(product: Product_v) {

        Mylog.d("aaaa link anh: " + product.imagechanged)
        GlideApp.with(this)
                .asBitmap()
                .load(Constants.IMAGE_URL + product.imagechanged)
                .apply(options)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        temp++

                        Mylog.d("aaaaaaaaaa error " + temp + " sizelistproduct: " + listProduct!!.size)
                        if (listProduct != null && !listProduct!!.isEmpty() && temp < listProduct!!.size) {
                            percent = (temp.toFloat() / listProduct!!.size.toFloat() * 100f).toInt()


                            onDownload(listProduct!!.get(temp))
                        } else {
                            hideProgress()
                            session.setLogin(true)
                            startActivity(Intent(this@LoginActivity, AllInOneActivity::class.java))
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

                            resource.compress(Bitmap.CompressFormat.JPEG, 90, out3)
                            Mylog.d("aaaaaaaaaa my dir: " + myDir)
                            product.imagechanged = Uri.fromFile(myDir).toString()
                            realm!!.updateProduct(product)

                            temp++
                            out3.flush()
                            out3.close()

                            if (listProduct != null && !listProduct!!.isEmpty() && temp < listProduct!!.size) {
                                percent = (temp.toFloat() / (listProduct!!.size.toFloat()) * 100f).toInt()

                                showProgress( percent.toString() + " %")

                                onDownload(listProduct!!.get(temp))
                            } else {
                                percent = (temp.toFloat() / (listProduct!!.size).toFloat() * 100f).toInt()
                                showProgress( percent.toString() + " " + resources.getString(R.string.complete))
                                session.setLogin(true)
                                hideProgress()

                                startActivity(Intent(this@LoginActivity, AllInOneActivity::class.java))
                                finish()
                            }


                        } catch (e: Exception) {

                            println(e)
                            temp++
                            Mylog.d("aaaaaaaaaa " + temp)
                            if (listProduct != null && !listProduct!!.isEmpty() && temp < listProduct!!.size) {
                                percent = (temp.toFloat() / listProduct!!.size.toFloat() * 100f).toInt()


                                onDownload(listProduct!!.get(temp))
                            }
                        }


                    }
                })

    }

    private fun SettingAlarm(hour: Int, boolean: Boolean) {
        val calendars = Calendar.getInstance()
        val now = Calendar.getInstance()
        val myIntent = Intent(this, AlarmReceiver::class.java)
//        myIntent.putExtra("extra", "yes")
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


    private fun gotoregister() {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)


    }

//    private fun gotoforgot() {
//        startActivityForResult(Intent(this@LoginActivity, ForgotPassword::class.java), Constants.FOTGOTPASSWORD)
////        finish()
//        overridePendingTransition(0, 0)
//    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        hideProgress()
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            handleSignInResult(result)
        } else if (requestCode == Constants.FOTGOTPASSWORD) {

            Log.e("requestCode: ", "OK ne")
            if (resultCode == Activity.RESULT_OK) {
                showSnack(R.string.clear_sucess, R.id.root_login)

            }
        }
    }

    // login as google
    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d(TAG, "handleSignInResult:" + result.status)
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.
            showProgress()
            val acct: GoogleSignInAccount? = result.signInAccount
            Log.e(TAG, "display name: " + acct!!.displayName)
            val tokenfirebase = FirebaseInstanceId.getInstance().token
            user.phone = acct.id
            user.password = ""
            user.tokenfirebase = tokenfirebase
            mLoginPresenter!!.register(user, 2)

            Log.e(TAG, "Name: " + user.google + ", email: ")
        } else {
            // Signed out, show unauthenticated UI.

        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed:" + p0)
        hideProgress()
    }

    //=========== broad case
    internal var broadcastLogin: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            if (bundle != null) {
                if (bundle.getBoolean("isfinishLogin")) {
                    this@LoginActivity.finish()

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (broadcastLogin != null)
            unregisterReceiver(this.broadcastLogin)
        mLoginPresenter?.cancelRequest()

    }

    override fun onResume() {
        super.onResume()
        registerReceiver(this.broadcastLogin, IntentFilter(AppIntent.ACTION_LOGIN))

    }

}




