package com.finger.hsd.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.finger.hsd.MyApplication
import com.finger.hsd.R
import com.finger.hsd.manager.AppManager
import com.finger.hsd.model.User
import com.finger.hsd.presenter.LoginPresenter
import com.finger.hsd.util.Constants
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

class LoginActivity : AppCompatActivity(), LoginPresenter.LoginView, GoogleApiClient.OnConnectionFailedListener {


    var mAccountManager: AccountManager? = null
    var account: Account? = null
    var user = User()
    var TAG = "Login Activity"


    private var callbackManager: CallbackManager? = null


    var v: View? = null
    private val RC_SIGN_IN = 7
    var mLoginPresenter: LoginPresenter? = null
    var type: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)

        callbackManager = CallbackManager.Factory.create()
        setContentView(R.layout.activity_login)

        mAccountManager = AccountManager.get(this)
          val intent = Intent(this, HorizontalNtbActivity::class.java)
          startActivity(intent)
//        val accountsFromFirstApp = mAccountManager!!.getAccountsByType(AppManager.ACCOUNT_TYPE)
//        if (accountsFromFirstApp.isNotEmpty()) {
//
//           // User is already logged in. Take him to main activity
//            val intent = Intent(this, HorizontalNtbActivity::class.java)
//            startActivity(intent)
//            finish()
//            overridePendingTransition(0, 0)
//        }

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
                        user.facebook = id
                        user.password = ""
                        user.tokenfirebase = (tokenfirebase)
                        mLoginPresenter!!.register(user)


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
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(MyApplication.getGoogleApiHelper()!!.googleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun login() {

        setError()


        var err = 0
        if (!validatePhone(phone_number!!.text.toString())) {
            err++
            phone_number!!.error = "loi"
        } else {
            if (!validatePhone2(phone_number.text.toString())!!) {
                err++
                phone_number.error = "loi"
            }
        }
        if (err == 0) {
            user.iduser = phone_number.text.toString()
            user.password = password.text.toString()
            user.tokenfirebase = FirebaseInstanceId.getInstance().token
            mLoginPresenter!!.login(user)
        } else {
            println("error")

        }
    }

    private fun setError() {
        phone_number.error = null

    }

    override fun isLoginSuccessful(isLoginSuccessful: Boolean) {
        if (isLoginSuccessful) {
            startActivity(Intent(this@LoginActivity, HorizontalNtbActivity::class.java))
            finish()
        } else {
            println("loi")
        }
    }

    override fun isRegisterSuccessful(isRegisterSuccessful: Boolean) {
        if (isRegisterSuccessful) {
            startActivity(Intent(this@LoginActivity, HorizontalNtbActivity::class.java))
            finish()
        } else {
            println("loi")
        }

    }

    override fun setErrorMessage(errorMessage: String) {


        showSnackBarMessage(errorMessage)


    }

    override fun getUserDetail(user: User) {
        AppManager.saveAccountUser(this,user)
        this.user = user
    }


    private fun gotoregister() {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)

    }

//    private fun gotoforgot() {
//        startActivityForResult(Intent(this@LoginActivity, ForgotPassword::class.java), Constants.FOTGOTPASSWORD)
////        finish()
//        overridePendingTransition(0, 0)
//    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            handleSignInResult(result)
        } else if (requestCode == Constants.FOTGOTPASSWORD) {
            Log.e("requestCode: ", "OK ne")
            if (resultCode == Activity.RESULT_OK) {
                showSnackBarMessage("success")

            }
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d(TAG, "handleSignInResult:" + result.status)
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.

            val acct: GoogleSignInAccount? = result.signInAccount
            Log.e(TAG, "display name: " + acct!!.displayName)
            val tokenfirebase = FirebaseInstanceId.getInstance().token
            user.iduser = acct.id
            user.password = ""
            user.tokenfirebase = tokenfirebase
            mLoginPresenter!!.register(user)
            Log.e(TAG, "Name: " + user.iduser + ", email: ")
        } else {
            // Signed out, show unauthenticated UI.

        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed:" + p0)
    }

    private fun showSnackBarMessage(message: String?) {
        Snackbar.make(findViewById(R.id.root_login), message!!, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mLoginPresenter?.cancelRequest()
    }
}




