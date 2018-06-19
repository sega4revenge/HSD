package com.finger.hsd.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog

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
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import kotlinx.android.synthetic.main.dialog_put_barcode.view.*
import kotlinx.android.synthetic.main.dialog_scanner_barcode_.view.*
import kotlinx.android.synthetic.main.dialog_timepicker.view.*

import me.dm7.barcodescanner.zxing.ZXingScannerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    private val arrBarcode = ""
    private var mBitmap:Bitmap? = null
    private val mRealm: RealmController? = null
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
        ///    if (resultPoints.size > 0)
         //       Log.d("ContinuousCapt", resultPoints[0].toString() + "/123/")
            // Toast.makeText(ContinuousCaptureActivity.this, resultPoints.get(0).toString()+"//", Toast.LENGTH_SHORT).show();
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
            val i = Intent(this@ContinuousCaptureActivity, Custom_Camera_Activity::class.java)
            i.putExtra("type",0)
            startActivity(i)
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
                Log.d("ContinuousCapt", "FAILLLLLL//123/"+barcode+"//"+t?.message)
                var product =  mRealm?.getProductWithBarcode(barcode)
                if(product != null){
                    val i = Intent(this@ContinuousCaptureActivity,Add_Product::class.java)
                    i.putExtra("type",1)
                    i.putExtra("barcode",product.barcode.toString())
                    i.putExtra("name",product.namechanged.toString())
                    i.putExtra("path",product.imagechanged.toString())
                    startActivity(i)
                }else{
                    showDialogNotFound(barcode)
                }
            }

            override fun onResponse(call: Call<Result_Product>?, response: Response<Result_Product>?) {
                if(response?.isSuccessful!!){
                    Log.d("ContinuousCapt", "FAILLLLLL"+response?.code())
                    if(response?.code()==200){
                        val mProduct = response.body().productType
                        val i = Intent(this@ContinuousCaptureActivity,Add_Product::class.java)
                        i.putExtra("type",2)
                        i.putExtra("barcode",mProduct.barcode.toString())
                        i.putExtra("name",mProduct.name.toString())
                        i.putExtra("image", Constants.IMAGE_URL+mProduct.image.toString())
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
                mDialog_scan?.dismiss()
            }

        })
        txtok2.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val i = Intent(this@ContinuousCaptureActivity,Custom_Camera_Activity::class.java)
                    i.putExtra("type",1)
                    i.putExtra("barcode",barcode)
                    startActivity(i)
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
        barcodeView!!.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView!!.pause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return barcodeView!!.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    companion object {
        private val TAG = ContinuousCaptureActivity::class.java.simpleName
    }
}
