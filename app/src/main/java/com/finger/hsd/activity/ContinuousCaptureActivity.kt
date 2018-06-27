package com.finger.hsd.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Camera
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.finger.hsd.BuildConfig

import com.finger.hsd.R
import com.finger.hsd.manager.RealmController
import com.finger.hsd.model.Result_Product
import com.finger.hsd.util.ApiUtils
import com.finger.hsd.util.Constants
import com.finger.hsd.util.RetrofitService
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.finger.hsd.library.DecoratedBarcodeView
import com.finger.hsd.util.Mylog
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.journeyapps.barcodescanner.camera.CameraManager
import kotlinx.android.synthetic.main.dialog_put_barcode.view.*
import kotlinx.android.synthetic.main.dialog_scanner_barcode_.view.*
import kotlinx.android.synthetic.main.dialog_timepicker.view.*


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * This sample performs continuous scanning, displaying the barcode and source image whenever
 * a barcode is scanned.
 */
class ContinuousCaptureActivity : Activity() {
    private var barcodeView: DecoratedBarcodeView? = null
    private var beepManager: BeepManager? = null
    private var lastText: String? = null
    private var mDialog: Dialog? = null
    private var mDialog_scan: Dialog? = null
    private var mRetrofitService: RetrofitService? = null
    private var arrBarcode:String = ""
    private var mBitmap:Bitmap? = null
    private val mRealm: RealmController? = null
    private var mStatusChoose = 0
    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastText) {
                // Prevent duplicate scans
                return
            }
            Log.d("ContinuousCapt", result.result.toString() + "//" + result.text + "//" + result.toString())
            lastText = result.text
            //barcodeView!!.setStatusText(result.text)

            beepManager!!.playBeepSoundAndVibrate()
            checkBarcode(result.text)
            mBitmap = result.getBitmapWithResultPoints(Color.YELLOW)
            //Added preview of scanned barcode
            //  ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            // imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.continuous_scan)
        val backimg   = findViewById<ImageView>(R.id.arrow_back)
        val putbarcode = findViewById<TextView>(R.id.txt_putbarcode)
        val addproduct = findViewById<TextView>(R.id.txt_addproduct)
        putbarcode.setOnClickListener(View.OnClickListener {
            showDialogInputBarcoe()
        })
        addproduct.setOnClickListener(View.OnClickListener {
            dispatchTakePictureIntent(111)
        })
        backimg.setOnClickListener(View.OnClickListener {
            finish()
        })
        barcodeView = findViewById<View>(R.id.barcode_scanner) as DecoratedBarcodeView
        val formats = Arrays.asList(BarcodeFormat.EAN_13, BarcodeFormat.EAN_8)
        barcodeView!!.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView!!.decodeContinuous(callback)
        beepManager = BeepManager(this)
    }

    fun checkBarcode(barcode:String){
        mRetrofitService = ApiUtils.getAPI()
        mRetrofitService?.checkBarcode(barcode)?.enqueue(object: Callback<Result_Product> {
            override fun onFailure(call: Call<Result_Product>?, t: Throwable?) {
//                Log.d("ContinuousCapt", "FAILLLLLL//"+barcode+"//"+t?.message)
//               var product =  mRealm?.getProductWithBarcode(barcode)
//                if(product != null){
//                    val i = Intent(this@ContinuousCaptureActivity,Add_Product::class.java)
//                    i.putExtra("type",2)
//                    i.putExtra("barcode",product.barcode.toString())
//                    i.putExtra("name",product.namechanged.toString())
//                    i.putExtra("path",product.imagechanged.toString())
//                    i.putExtra("checkProduct",product.producttype_id?.check_product)
//                    startActivity(i)
//                }else{
//                   val mcoutdowntime = object : CountDownTimer(2000, 2000) {
//                        override fun onTick(millisUntilFinished: Long) {
//                        }
//                        override fun onFinish() {
//                            lastText = ""
//                        }
//                    }.start()
//
//                    Toast.makeText(this@ContinuousCaptureActivity, "Không thể kết nối mạng", Toast.LENGTH_SHORT).show()
//                  //  showDialogNotFound(barcode)
//                }
                lastText = ""
                Toast.makeText(this@ContinuousCaptureActivity, "Không thể kết nối mạng", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Result_Product>?, response: Response<Result_Product>?) {
                if(response?.isSuccessful!!){
                    if(response?.code()==200){
                        var dataOffline = mRealm?.getProductWithBarcode(barcode)
                        val mProduct = response.body().productType
                        val i = Intent(this@ContinuousCaptureActivity,Add_Product::class.java)
                        i.putExtra("type",3)
                        i.putExtra("barcode",mProduct.barcode.toString())
                        i.putExtra("name",mProduct.name.toString())
                        if(dataOffline!=null){
                            i.putExtra("image",dataOffline.imagechanged.toString())
                        }else{
                            i.putExtra("image", Constants.IMAGE_URL+mProduct.image.toString())
                        }
                        i.putExtra("checkProduct",mProduct.check_product)
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
    fun showDialogNotFound(barcode:String){
        var datedialog = AlertDialog.Builder(this@ContinuousCaptureActivity)
        var mView2:View = View.inflate(this@ContinuousCaptureActivity,R.layout.dialog_scanner_barcode_,null)
        datedialog.setView(mView2)
        datedialog.setCancelable(false)
        var img_barcode = mView2.img_barcode
        if(mBitmap!=null){
            img_barcode.setImageBitmap(mBitmap)
        }else{
            img_barcode.visibility = View.GONE
        }
        var txtout2 = mView2.txt_out_scanbarcode
        var txtok2 = mView2.txt_ok_scanbarcode

        txtout2.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                lastText = ""
             //   barcodeView!!.decodeSingle(callback)
                mDialog_scan?.dismiss()
            }

        })
        txtok2.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                arrBarcode = barcode
                dispatchTakePictureIntent(222)
                mDialog_scan?.dismiss()
            }

        })
//        var mDialog2: Dialog? = null
//        val dialog = MaterialDialog.Builder(this)
//                .title("Not found product!")
//                .content("Would you like to add this product information to our system?")
//                .cancelable(false)
//                .positiveText("OK")
//                .negativeText("Cancel")
//                .onPositive { dialog, which ->
//                    val i = Intent(this@ContinuousCaptureActivity,Custom_Camera_Activity::class.java)
//                    i.putExtra("type",1)
//                    i.putExtra("barcode",barcode)
//                    startActivity(i)
//                    mDialog2?.dismiss()
//                }

        mDialog_scan = datedialog.create()
        mDialog_scan?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog_scan?.show()
    }

    fun showDialogInputBarcoe(){
        var mBarcode = ""
        var datedialog = AlertDialog.Builder(this@ContinuousCaptureActivity)
        val mView:View = View.inflate(this@ContinuousCaptureActivity,R.layout.dialog_put_barcode,null)
        datedialog.setView(mView)
        val mEdit = mView.edit_barcode
        val txtout: TextView = mView.txt_out_put_barcode
        val txtok = mView.txt_ok_put_barcode

        txtout.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                mDialog?.dismiss()
            }

        })
        txtok.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                //    mBarcode = mEdit.text.toString()
                mBitmap = null
                if(!mEdit.text.toString().isNullOrEmpty()){
                    if(mEdit.text.toString().length<6){
                        Toast.makeText(applicationContext,"Barcode not true", Toast.LENGTH_LONG).show()
                    }else{
                        arrBarcode = mEdit.text.toString()
                        checkBarcode(mEdit.text.toString())
                    }
                }else{
                    Toast.makeText(applicationContext,"Barcode not true", Toast.LENGTH_LONG).show()
                }
                mDialog?.dismiss()
            }

        })
        mDialog = datedialog.create()
        mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog?.show()

    }
    override fun onResume() {
        super.onResume()
        lastText = ""
        barcodeView!!.resume()
        if(mDialog_scan!= null &&mDialog_scan!!.isShowing){
            mDialog_scan?.dismiss()
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeView!!.pauseAndWait()

    }

    override fun onStop() {
        super.onStop()

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return barcodeView!!.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Log.d("aaaaaaaaa",cameraImageUri.toString())
            when(requestCode){
                111 ->{
                    if(cameraImageUri != null&&!cameraImageUri.toString().isNullOrEmpty()){
                        val i = Intent(this@ContinuousCaptureActivity,Add_Product::class.java)
                        i.putExtra("type",0)
                        i.putExtra("path",cameraImageUri.toString())
                        startActivity(i)
                    }
                }
                222->{
                    if(cameraImageUri != null && !cameraImageUri.toString().isNullOrEmpty()){
                        val i = Intent(this@ContinuousCaptureActivity,Add_Product::class.java)
                        i.putExtra("type",1)
                        i.putExtra("path",cameraImageUri.toString())
                        i.putExtra("barcode",arrBarcode)
                        startActivity(i)
                    }
                }
                1001->{

                }
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            111 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return
                    }
                    try {
                        dispatchTakePictureIntent(111)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
            222 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return
                    }
                    try {
                        dispatchTakePictureIntent(222)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    val REQUEST_TAKE_PHOTO = 1
    var mCurrentPhotoPath: String? = null

    private fun dispatchTakePictureIntent(mType:Int) {
        var permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), mType)
        }else{
            val cameraInent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (cameraInent.resolveActivity(this.getPackageManager()) == null) {
                //      errorMessage("This Application do not have Camera Application")
                return
            }

            val imageFile = getImageFile()
            val photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", imageFile!!)

            val resolvedIntentActivities = this.getPackageManager().queryIntentActivities(cameraInent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolvedIntentInfo in resolvedIntentActivities) {

                val packageName = resolvedIntentInfo.activityInfo.packageName
                this.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            cameraInent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

            startActivityForResult(cameraInent, mType)
        }


    }


    private var cameraImageUri: Uri? = null
    private fun getImageFile(): File? {
        // Create an image file name
        var imageFile: File? = null
        try {
            val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

            if (!storageDir.exists())
                storageDir.mkdirs()

            imageFile = File.createTempFile(
                    imageFileName, /* prefix */
                    ".jpg", /* suffix */
                    storageDir      /* directory */
            )
            cameraImageUri = Uri.fromFile(imageFile)
        } catch (e: IOException) {
            e.printStackTrace()
           // errorMessage("Could not create imageFile for camera")
        }


        return imageFile
    }


    private fun getOutputMediaFile(): File {
        val mediaStorageDir = this.getExternalFilesDir(null)
        //   if (!mediaStorageDir.exists()) {
        //    if (!mediaStorageDir.mkdirs()) {
        //        Log.d("Custom_Camera_Activity", "failed to create directory");
        //        return null;
        //     }
        //  }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(Date())
        val mediaFile: File
        mediaFile = File(mediaStorageDir!!.path + File.separator
                + "IMG_" + timeStamp + ".jpg")

        mCurrentPhotoPath = mediaFile.absolutePath
        return mediaFile
    }

    companion object {
        private val TAG = ContinuousCaptureActivity::class.java.simpleName
    }
}
