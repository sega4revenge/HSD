package com.finger.hsd.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.finger.hsd.R
import com.finger.hsd.manager.AppManager
import com.finger.hsd.model.User
import com.finger.hsd.presenter.LoginPresenter
import com.finger.hsd.util.Validation.validatePassword
import com.finger.hsd.util.Validation.validatePhone
import com.finger.hsd.util.Validation.validatePhone2
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), LoginPresenter.LoginView {
    var user = User()

    var v: View? = null
    override fun isRegisterSuccessful(isRegisterSuccessful: Boolean) {
        if (isRegisterSuccessful) {   startActivity(Intent(this@RegisterActivity, HorizontalNtbActivity::class.java))
            finish()
        }

    }

    var mRegisterPresenter: LoginPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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
            phone_number!!.error = "loi"
        } else {
            if (!validatePhone2(phone_number.text.toString())!!) {
                err++
                phone_number.error = "loi"
            }
        }

        if (!validatePassword(password!!.text.toString())) {

            err++
            password!!.error = "loi"
        }

        if (password!!.text.toString() != repass!!.text.toString() || repass!!.text.toString() == "") {

            err++
            repass!!.error = "loi"
        }

//        if (!validatePassword(password!!.text.toString())) {
//
//            err++
//            password!!.error = getString(R.string.st_errpass2)
//        }
//        if (password!!.text.toString() != repassword!!.text.toString() || repassword!!.text.toString() == "") {
//
//            err++
//
//            repassword!!.error = getString(R.string.err_repass)
//
//        }


        if (err == 0) {
//            progressBar.visibility = View.GONE
//            CircularAnim.show(btn_join).go()

            user.iduser = phone_number.text.toString()

            user.password = ""
            user.tokenfirebase = FirebaseInstanceId.getInstance().token

            mRegisterPresenter!!.register(user)


        } else {

            showSnackBarMessage("loi")
        }
    }

    private fun gotologin() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)

        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }

    private fun setError() {

        name!!.error = null
        repass.error = null
        password.error = null
    }


    override fun isLoginSuccessful(isLoginSuccessful: Boolean) {

    }

    override fun setErrorMessage(errorMessage: String) {

        showSnackBarMessage(errorMessage)
    }

    override fun getUserDetail(user: User) {
        AppManager.saveAccountUser(this, user)
        this.user = user
    }



    private fun showSnackBarMessage(message: String?) {


        Snackbar.make(findViewById(R.id.root_register), message!!, Snackbar.LENGTH_SHORT).show()

    }


    public override fun onDestroy() {
        super.onDestroy()
        mRegisterPresenter?.cancelRequest()
    }
}