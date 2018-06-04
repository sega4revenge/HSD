package com.finger.hsd.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.graphics.drawable.ColorDrawable
import android.hardware.Camera
import android.media.MediaActionSound
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseArray
import android.view.*
import android.widget.DatePicker
import android.widget.TextView

import com.finger.hsd.R
import com.google.android.gms.vision.CameraSource

import com.google.android.gms.vision.barcode.BarcodeDetector

import java.io.IOException
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.finger.hsd.model.*
import com.finger.hsd.util.ApiUtils
import com.finger.hsd.util.Constants
import com.finger.hsd.util.RetrofitService
import com.google.zxing.Result
import kotlinx.android.synthetic.main.dialog_put_barcode.view.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class Scanner_Barcode_Activity : AppCompatActivity(), View.OnClickListener ,ZXingScannerView.ResultHandler {
    private var cameraView: SurfaceView? = null
    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null
    private val perID = 1001
    private var ProductID: Long = 0L
    private var txManual : TextView? = null
    private var txAddProduct : TextView? = null
    private var TAG = "Scanner_Barcode_Activity"
    private var mDialog: Dialog? = null
    var mRetrofitService:RetrofitService? = null
    var mScannerView:ZXingScannerView? =null
    var arrBarcode = ""
    var contentFrame:ViewGroup? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scanner_barcode_layout2)


        contentFrame =findViewById<ViewGroup>(R.id.content_frame)
        try {
            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@Scanner_Barcode_Activity, arrayOf(Manifest.permission.CAMERA), perID)
                return
            }
            mScannerView =  ZXingScannerView(this)
            contentFrame?.addView(mScannerView)
        } catch (ie: IOException) {
            Log.e("CAMERA SOURCE", ie.message)
        }



        txManual = findViewById<TextView>(R.id.addmanual)
        txAddProduct = findViewById<TextView>(R.id.addproduct)

        txManual?.setOnClickListener(this)
        txAddProduct?.setOnClickListener(this)

//        barcodeDetector = BarcodeDetector.Builder(this)
//                .setBarcodeFormats(Barcode.EAN_13)
//                .build()
//
//        val displayMetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(displayMetrics)
//        val width = displayMetrics.widthPixels
//        val height = displayMetrics.heightPixels
//
//        cameraSource = CameraSource.Builder(this, barcodeDetector!!)
//                .setAutoFocusEnabled(true)
//                .setRequestedPreviewSize(width, height)
//                .setFacing(CameraSource.CAMERA_FACING_BACK)
//                .setRequestedFps(2.0f)
//                .build()
//
//        cameraView!!.holder.addCallback(object : SurfaceHolder.Callback {
//
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                try {
//                    if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(this@Scanner_Barcode_Activity, arrayOf(Manifest.permission.CAMERA), perID)
//                        return
//                    }
//                    cameraSource!!.start(cameraView!!.holder)
//                } catch (ie: IOException) {
//                    Log.e("CAMERA SOURCE", ie.message)
//                }
//
//            }
//
//            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
//
//            override fun surfaceDestroyed(holder: SurfaceHolder) {
//                cameraSource!!.stop()
//            }
//        })
//        barcodeDetector!!.setProcessor(object : Detector.Processor<Barcode> {
//            override fun release() {
//            }
//
//            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
//                val barcodes = detections.detectedItems
//                Log.d("zzzzzzzzzzz",barcodes.valueAt(0).displayValue+"on12312")
//                if (barcodes.size() != 0) {
//                    for (i in 0 until barcodes.size()) {
//                        if (ProductID == 0L) {
//
//                            ProductID = java.lang.Long.parseLong(barcodes.valueAt(i).displayValue)
//                          //  Toast.makeText(this@Scanner_Barcode_Activity,ProductID.toString()+"//",Toast.LENGTH_LONG).show()
//                            Log.d("zzzzzzzzzzz",ProductID.toString()+"onResume//haha")
//                           // checkBarcode(ProductID.toString())
//                            break
//                        }
//                    }
//                }
//            }
//        })

    }
    override fun handleResult(p0: Result?) {
        Toast.makeText(this, "Contents = " + p0?.getText() +
                ", Format = " + p0?.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show()
        if(!p0?.text.isNullOrEmpty()&& arrBarcode.indexOf(p0?.getText()!!)<0){
            checkBarcode(p0?.getText()!!)
            arrBarcode = arrBarcode +" "+p0?.getText()!!;
        }
        val handler = Handler()
        handler.postDelayed(Runnable {
            mScannerView?.resumeCameraPreview(this@Scanner_Barcode_Activity)
        }, 5000)
    }
    public override fun onResume() {
        super.onResume()
        mScannerView?.setResultHandler(this)
        mScannerView?.startCamera()
    }

    public override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera()
    }
    fun checkBarcode(barcode:String){
        mRetrofitService = ApiUtils.getAPI()
        mRetrofitService?.checkBarcode(barcode)?.enqueue(object: Callback<Result_Product>{
            override fun onFailure(call: Call<Result_Product>?, t: Throwable?) {
                Log.d("zzzzzzzzzzz",t?.message+"//")
            }

            override fun onResponse(call: Call<Result_Product>?, response: Response<Result_Product>?) {
                if(response?.isSuccessful!!){
                    if(response?.code()==200){
                        val mProduct = response.body().product[0]
                        val i = Intent(this@Scanner_Barcode_Activity,Add_Product::class.java)
                        i.putExtra("type",2)
                        i.putExtra("barcode",mProduct.barcode.toString())
                        i.putExtra("name",mProduct.name.toString())
                        i.putExtra("image",Constants.IMAGE_URL+mProduct.image.toString())
                        startActivity(i)
                    }
                }else{
                    if(response?.code() == 405){
                        showDialogNotFound(barcode)
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.addproduct ->
            {
                //camera?.autoFocus { success, camera -> playFocusSound() }
              //  cameraSource?.stop()
                val i = Intent(this@Scanner_Barcode_Activity, Custom_Camera_Activity::class.java)
                i.putExtra("type",0)
                startActivity(i)
            }
            R.id.addmanual -> showDialogInputBarcoe()
            else -> {
                print("x is neither 1 nor 2")
            }
        }
    }
    fun showDialogNotFound(barcode:String){
        var mDialog2: Dialog? = null
        val dialog = MaterialDialog.Builder(this)
                .title("Not found product!")
                .content("Would you like to add this product information to our system?")
                .cancelable(false)
                .positiveText("OK")
                .negativeText("Cancel")
                .onPositive { dialog, which ->
                    val i = Intent(this@Scanner_Barcode_Activity,Custom_Camera_Activity::class.java)
                    i.putExtra("type",1)
                    i.putExtra("barcode",barcode)
                    startActivity(i)
                    mDialog2?.dismiss()
                }

        mDialog2 = dialog.build()
        mDialog2.show()
    }
    fun showDialogInputBarcoe(){
        var mBarcode = ""
        var datedialog = AlertDialog.Builder(this@Scanner_Barcode_Activity)
        val mView:View = View.inflate(this@Scanner_Barcode_Activity,R.layout.dialog_put_barcode,null)
        datedialog.setView(mView)
        val mEdit = mView.edit_barcode
        val txtout:TextView = mView.txt_out
        val txtok = mView.txt_ok

        txtout.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                mDialog?.dismiss()
            }

        })
        txtok.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
            //    mBarcode = mEdit.text.toString()
                if(!mEdit.text.toString().isNullOrEmpty()){
                    if(mEdit.text.toString().length<6){
                        Toast.makeText(applicationContext,"Barcode not true",Toast.LENGTH_LONG).show()
                    }else{
                        checkBarcode(mEdit.text.toString())
                    }
                }else{
                    Toast.makeText(applicationContext,"Barcode not true",Toast.LENGTH_LONG).show()
                }
                mDialog?.dismiss()
            }

        })
        mDialog = datedialog.create()
        mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog?.show()
//        var mDialog: Dialog? = null
//        val dialog = MaterialDialog.Builder(this)
//                .title("Add Number Barcode")
//                .customView(R.layout.dialog_put_barcode_layout, true)
//                .cancelable(false)
//        mDialog = dialog.build()
//        mDialog.show()
//
//        val v = mDialog?.getCustomView()
//        val txadd = v?.findViewById(R.id.txadd) as TextView
//        val txcancel = v?.findViewById(R.id.txcancel) as TextView
//        val edit_putnumber = v?.findViewById(R.id.edit_putnumber) as EditText
//        val text_input_layout  = v?.findViewById(R.id.text_input_layout) as TextInputLayout
//
//        txadd.setOnClickListener(object: View.OnClickListener{
//            override fun onClick(v: View?) {
//                if(edit_putnumber.text.toString() == "") {
//                    text_input_layout.error = "Không để trống"
//                }else{
//                    if(edit_putnumber.text.toString().length<6){
//                        Toast.makeText(applicationContext,"Barcode not true",Toast.LENGTH_LONG).show()
//                    }else{
//                        checkBarcode(edit_putnumber.text.toString())
//                    }
//                }
//            }
//        })
//
//        txcancel.setOnClickListener(object: View.OnClickListener{
//            override fun onClick(v: View?) {
//                mDialog.dismiss()
//            }
//        })
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
                        mScannerView =  ZXingScannerView(this)
                        contentFrame?.addView(mScannerView)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }
}
































