package com.finger.hsd.activity

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.TextView

import com.finger.hsd.R
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

import java.io.IOException
import android.widget.EditText
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.DialogAction




class Scanner_Barcode_Activity : AppCompatActivity() ,View.OnClickListener {
    private var cameraView: SurfaceView? = null
    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null
    internal val perID = 1001
    private var ProductID: Long = 0L
    private var txManual : TextView? = null
    private var txAddProduct : TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scanner_barcode_layout)

        cameraView = findViewById<SurfaceView>(R.id.camera_view)
        txManual = findViewById<TextView>(R.id.addmanual)
        txAddProduct = findViewById<TextView>(R.id.addproduct)


        txManual?.setOnClickListener(this)
        txAddProduct?.setOnClickListener(this)

        barcodeDetector = BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.EAN_13)
                .build()

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        cameraSource = CameraSource.Builder(this, barcodeDetector!!)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(width, height)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(2.0f)
                .build()

        cameraView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this@Scanner_Barcode_Activity, arrayOf(Manifest.permission.CAMERA), perID)
                        return
                    }
                    cameraSource!!.start(cameraView!!.holder)
                } catch (ie: IOException) {
                    Log.e("CAMERA SOURCE", ie.message)
                }

            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource!!.stop()
            }
        })
        barcodeDetector!!.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    for (i in 0 until barcodes.size()) {
                        if (ProductID == 0L) {
                            ProductID = java.lang.Long.parseLong(barcodes.valueAt(i).displayValue)
                        }
                    }
                }
            }
        })
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.addproduct ->
            {
                val i = Intent(this, Scanner_HSD_Activity::class.java)
                startActivity(i)
            }
            R.id.addmanual -> showDialog()
            else -> {
                print("x is neither 1 nor 2")
            }
        }
    }
    fun showDialog(){
        var mDialog: Dialog? = null
        val dialog = MaterialDialog.Builder(this)
                .title("Add Number Barcode")
                .customView(R.layout.dialog_put_barcode_layout, true)
                .cancelable(false)
        mDialog = dialog.build()
        mDialog.show()

        val v = mDialog?.getCustomView()
        val txadd = v?.findViewById(R.id.txadd) as TextView
        val txcancel = v?.findViewById(R.id.txcancel) as TextView
        val edit_putnumber = v?.findViewById(R.id.edit_putnumber) as EditText
        val text_input_layout  = v?.findViewById(R.id.text_input_layout) as TextInputLayout

        txadd.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                if(edit_putnumber.text.toString() == "") {
                    text_input_layout.error = "Không để trống"
                }else{
                    Toast.makeText(applicationContext,edit_putnumber.text.toString(),Toast.LENGTH_LONG).show()
                }
            }
        })

        txcancel.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                mDialog.dismiss()
            }
        })





    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            perID -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return
                    }
                    try {
                        cameraSource!!.start(cameraView!!.holder)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }
}
