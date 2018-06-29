package com.finger.hsd

import android.app.ProgressDialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView

class LoadingProgressDialog(context: Context, theme: Int) : ProgressDialog(context, theme) {

    private var mContext: Context? = null
    private var mContentView: View? = null
    private var mLoadingImage: ImageView? = null
    private var mTxtMessage: TextView? = null

    init {
        init(context)
    }

    fun init(context: Context){
        this.mContext = context
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setDimAmount(0.5f)

        isIndeterminate = true
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        mContentView = View.inflate(mContext, R.layout.progress_dialog, null)
        mLoadingImage = mContentView!!.findViewById<View>(R.id.progress_loading) as ImageView
        mTxtMessage = mContentView!!.findViewById<View>(R.id.message) as TextView

    }

    override fun show() {
        super.show()
        startFrameAnimation(mLoadingImage!!)
        setContentView(mContentView)
    }


    fun stop() {

    }

    private fun startFrameAnimation(loadingImageView: ImageView) {
      //  loadingImageView.setBackgroundResource(R.drawable.loading_animation)
        val anim = loadingImageView.background as AnimationDrawable
        anim.start()

    }

    private fun stoptFrameAnimation(loadingImageView: ImageView) {
      //  loadingImageView.setBackgroundResource(R.drawable.loading_animation)
        val anim = loadingImageView.background as AnimationDrawable
        anim.stop()

    }

    override fun setMessage(message: CharSequence) {
        mTxtMessage!!.text = message
    }
}