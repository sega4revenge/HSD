package com.finger.hsd

import android.app.ProgressDialog
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import java.net.InetAddress


open class BaseActivity : AppCompatActivity() {

    private var mToast: Toast? = null
    private var mSnackbar: Snackbar? = null
    private var mLoadingProgressDialog: ProgressDialog? = null
    private val CALL_REQUEST_CODE = 232
    fun createProgressDialog(): ProgressDialog {
        return LoadingProgressDialog(this, R.style.no_bg_progress_dialog)
    }

    fun isInternetAvailable(): Boolean {
        try {
            val ipAddr = InetAddress.getByName("google.com")
            //You can replace it with your name
            return !ipAddr.equals("")

        } catch (e: Exception) {
            return false
        }

    }
    fun showProgress() {
        if (!isFinishing) {
            if (mLoadingProgressDialog == null) {
                mLoadingProgressDialog = createProgressDialog()
            }

            if (!isFinishing && !mLoadingProgressDialog!!.isShowing) {
                mLoadingProgressDialog!!.setMessage("")
                mLoadingProgressDialog!!.dismiss()
                mLoadingProgressDialog!!.show()
            }
        }
    }

    fun showProgress(message: String) {
        if (!isFinishing) {
            if (mLoadingProgressDialog == null) {
                mLoadingProgressDialog = createProgressDialog()
            }

            if (mLoadingProgressDialog!!.isShowing) {
                mLoadingProgressDialog!!.setMessage(message)
            } else {
                mLoadingProgressDialog!!.dismiss()
                mLoadingProgressDialog!!.setMessage(message)
                mLoadingProgressDialog!!.show()
            }
        }
    }

    fun hideProgress() {
        if (!isFinishing) {
            if (mLoadingProgressDialog != null && mLoadingProgressDialog!!.isShowing) {
                mLoadingProgressDialog!!.dismiss()
            }
        }
    }

     fun showSnackBarMessage(message: String?) {

    }
    fun showSnack(str: String, length: Int, idLayout: Int) {
        if (mSnackbar == null) {
            mSnackbar = Snackbar.make(findViewById(idLayout), str, length)
        } else {
            mSnackbar!!.setText(str)
        }
        mSnackbar!!.show()
    }

    fun showSnack(strResId: Int, idLayout: Int) {
        this.showSnack(resources.getString(strResId),  Snackbar.LENGTH_SHORT, idLayout)
    }

    fun showSnack(strResId: Int, length: Int, idLayout: Int) {
        this.showSnack( resources.getString(strResId),  Snackbar.LENGTH_SHORT, idLayout)
    }

    fun showSnack(str: String, idLayout : Int) {
        this.showSnack( str,  Snackbar.LENGTH_SHORT, idLayout)
    }


    fun showToast(strResId: Int) {
        this.showToast(resources.getString(strResId), Toast.LENGTH_SHORT)
    }

    fun showToast(strResId: Int, length: Int) {
        this.showToast(resources.getString(strResId), length)
    }

    fun showToast(str: String) {
        this.showToast(str, Toast.LENGTH_SHORT)
    }

    fun showToast(str: String, length: Int) {
        if (mToast == null) {
            mToast = Toast.makeText(this, str, length)
        } else {
            mToast!!.setText(str)
        }
        mToast!!.show()
    }

}