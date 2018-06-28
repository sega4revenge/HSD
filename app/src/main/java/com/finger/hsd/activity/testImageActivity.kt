package com.finger.hsd.activity

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.Toolbar
import android.text.Html.fromHtml
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.androidnetworking.error.ANError
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.finger.hsd.BaseActivity
import com.finger.hsd.R
import com.finger.hsd.R.string.note
import com.finger.hsd.R.string.product
import com.finger.hsd.common.GlideApp
import com.finger.hsd.library.CompressImage
import com.finger.hsd.library.image.TedBottomPicker
import com.finger.hsd.manager.RealmController
import com.finger.hsd.model.Notification
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.Response
import com.finger.hsd.presenter.DetailProductPresenter
import com.finger.hsd.util.AppIntent
import com.finger.hsd.util.ConnectivityChangeReceiver
import com.finger.hsd.util.Constants
import com.finger.hsd.util.Mylog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_detail_product.*
import kotlinx.android.synthetic.main.dialog_timepicker.view.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.*








class testImageActivity : BaseActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)




    }

}
