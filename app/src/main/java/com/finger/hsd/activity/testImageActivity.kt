package com.finger.hsd.activity

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.finger.hsd.R
import com.finger.hsd.library.image.TedBottomPicker
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.dialog_put_barcode_layout.*
import java.util.*








class testImageActivity : AppCompatActivity() {



    val calendar: Calendar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_put_barcode_layout)

        im_view.setOnClickListener {
//            showImage()
        }


    }



    var selectedUri: Uri? = null

    fun showImage() {

        val permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {

                val bottomSheetDialogFragment = TedBottomPicker.Builder(this@testImageActivity)
                        .setOnImageSelectedListener(object : TedBottomPicker.OnImageSelectedListener {
                            override fun onImageSelected(uri: Uri) {
                                Log.d("selected ted: ", "uri: $uri")
                                Log.d("selected path: ", "uri.getPath(): " + uri.path)
                                selectedUri = uri
                                val options = RequestOptions()
                                        .dontAnimate()
                                        .placeholder(R.mipmap.ic_launcher)
                                        .priority(Priority.HIGH)
                                Glide.with(this@testImageActivity)
                                        .load(uri)
                                        .thumbnail(0.1f)
                                        .apply(options)
                                        .into((findViewById(R.id.im_view)))

                            }
                        })
                        //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                        .setSelectedUri(selectedUri)
                        .setPeekHeight(1200)
                        .create()

                bottomSheetDialogFragment.show(supportFragmentManager)
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                Toast.makeText(this@testImageActivity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
            }

        }

        TedPermission(this@testImageActivity)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions " +
                        "at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setPermissions(Manifest.permission.CAMERA)
                .check()

    }
}
