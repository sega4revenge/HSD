package com.finger.hsd


import android.app.ProgressDialog
import android.support.v4.app.Fragment
import android.widget.Toast


open class BaseFragment : Fragment(){

    private var mLoadingProgressDialog: ProgressDialog? = null

    fun createProgressDialog(): ProgressDialog {
        return LoadingProgressDialog(activity!!, R.style.no_bg_progress_dialog)
    }

    fun showProgress() {
        if (!isRemoving) {
            if (mLoadingProgressDialog == null) {
                mLoadingProgressDialog = createProgressDialog()
            }

            mLoadingProgressDialog!!.setMessage("")

            if (!mLoadingProgressDialog!!.isShowing) {
                mLoadingProgressDialog!!.dismiss()
                mLoadingProgressDialog!!.show()
            }
        }
    }

    fun showProgress(message: String) {
        if (!isRemoving) {
            if (mLoadingProgressDialog == null) {
                mLoadingProgressDialog = LoadingProgressDialog(activity!!, R.style.no_bg_progress_dialog)
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
        if (!isRemoving) {
            if (mLoadingProgressDialog != null && mLoadingProgressDialog!!.isShowing) {
                mLoadingProgressDialog!!.dismiss()
            }
        }
    }

    fun showToast(strResId: Int) {
        this.showToast(resources.getString(strResId))
    }

    fun showToast(str: String) {
        if (activity is BaseActivity) {
            (activity as BaseActivity).showToast(str)
        } else {
            Toast.makeText(activity, str, Toast.LENGTH_LONG).show()
        }
    }


    fun onFinishApi() {
        hideProgress()
    }
}
