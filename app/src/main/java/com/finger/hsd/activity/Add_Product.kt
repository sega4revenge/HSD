package com.finger.hsd.activity

import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View

import com.finger.hsd.R
import android.widget.TimePicker
import java.io.File
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog

import com.finger.hsd.MyApplication
import com.finger.hsd.model.Product
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.Result_Product
import com.finger.hsd.util.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_timepicker.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.Inflater


class Add_Product : AppCompatActivity() ,View.OnClickListener {
    private var img_product: ImageView? = null
    private var arrow_back: ImageView? = null
    private var select_img: LinearLayout? = null
    private var scanner_ex: ImageView? = null
    private var edit_nameproduct: EditText? = null
    private var edit_numbarcode: EditText? = null
    private var edit_ex: EditText? = null
    private var edit_chitiet: EditText? = null
    private var barcode:TextView? = null
    private var currentDateandTime: String? = null
    private var sdf = SimpleDateFormat("dd/MM/yyyy")
    private var txtEX:TextView? = null
    private var bt_post: Button? = null
    private val RESULT_SCANNER = 1001
    private var miliexDate:Long? = 0L
    private var miliexToday:Long? = 0L
    private var mRetrofitService: RetrofitService? = null
    private var mDialog:Dialog? =null
    private final val CODE_RESULT_IMAGE_SELECT = 1001
    var path:String? = null
    private var haveImage = false
    var barcodeIn:String? = null
    private var mDialogProgress: MaterialDialog? = null
    var type:Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_product_layout)

        initView()
        getData()
        if(barcodeIn!=null){
            barcode?.setText(barcodeIn)
        }else{
            val stringToday = sdf.format(Date())
            val exToday = sdf.parse(stringToday)
            barcodeIn = exToday.time.toString()
            barcode?.setText(barcodeIn)
        }

    }

    private fun getData() {
        val stringToday = sdf.format(Date())
        val exToday = sdf.parse(stringToday)
         miliexToday =  exToday.time

        val mData = intent
        type = mData.getIntExtra("type",1)

        if(type==1 || type ==0){
            path = mData.getStringExtra("path")
            img_product?.setImageBitmap(BitmapFactory.decodeFile(path))
            if(type==1){
                barcodeIn = mData.getStringExtra("barcode")
            }
        }else if(type ==2 || type ==3){
            if(type==2)
            {
                path = mData.getStringExtra("image")

                Picasso.Builder(this)
                        .downloader(OkHttp3Downloader(MyApplication.okhttpclient()))
                        .build().load(path).resize(60,60).error(R.drawable.ic_add_photo).placeholder(R.drawable.ic_calendar).into(img_product!!)
            }
            barcodeIn = mData.getStringExtra("barcode")
            edit_nameproduct?.setText(mData.getStringExtra("name"))
            if(type==3){
                path = mData.getStringExtra("path")
                img_product?.setImageBitmap(BitmapFactory.decodeFile(path))
                edit_chitiet?.setText(mData.getStringExtra("detail"))
                edit_ex?.setText(mData.getStringExtra("ex"))
                miliexDate = mData.getLongExtra("exTime",20)
                if(!edit_ex?.text.isNullOrEmpty()){
                    updateEX(miliexToday!!, miliexDate!!)
                }

            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            select_img?.id ->{
                val i = Intent(this@Add_Product,Custom_Camera_Activity::class.java)
                    i.putExtra("type",CODE_RESULT_IMAGE_SELECT)
                    i.putExtra("name",edit_nameproduct?.text.toString())
                    i.putExtra("ex",edit_ex?.text.toString())
                    i.putExtra("exTime",miliexDate)
                    i.putExtra("detail",edit_chitiet?.text.toString())
                    i.putExtra("barcode",barcode?.text.toString())
                    startActivity(i)
               //     startActivityForResult(i,CODE_RESULT_IMAGE_SELECT)
            }
            scanner_ex?.id ->{
                val i = Intent(this@Add_Product,Scanner_HSD_Activity::class.java)
                startActivityForResult(i,RESULT_SCANNER)
            }
            edit_ex?.id ->{
                showHourPicker()
//                val callback = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
//                    val stringToday = sdf.format(Date())
//                    val exToday = sdf.parse(stringToday)
//                    val exDate = sdf.parse(day.toString() + "/" + (month + 1) + "/" + year)
//                    miliexDate =  exDate.time
//                    val miliexToday =  exToday.time
//                    edit_ex?.setText(day.toString() + "/" + (month + 1) + "/" + year)
//                    updateEX(miliexToday, miliexDate!!)
//                }
//                //Lấy ra chuỗi của textView Date
//                val strArrtmp = currentDateandTime?.split("/")
//                val ngay = Integer.parseInt(strArrtmp!![0])
//                val thang = Integer.parseInt(strArrtmp[1]) - 1
//                val nam = Integer.parseInt(strArrtmp[2])
//                //Hiển thị ra Dialog
//                val pic = DatePickerDialog(
//                        this@Add_Product,
//                        callback, nam, thang, ngay)
//                pic.setTitle("Chọn ngày hết hạn")
//                pic.show()
            }
            img_product?.id ->{

                val i = Intent(this@Add_Product,show_PhotoProduct::class.java)
                if(type!=2){
                    i.putExtra("type",0)
                }else{
                    i.putExtra("type",1)
                }
                i.putExtra("path",path)
                startActivity(i)

            }
            arrow_back?.id ->{
                finish()
            }
            bt_post?.id ->{
                bt_post?.visibility = View.GONE
                showDialog()
                if(path.isNullOrEmpty() || edit_nameproduct?.text.isNullOrEmpty() || edit_ex?.text.isNullOrEmpty() || txtEX?.text.isNullOrEmpty() || barcodeIn?.isNullOrEmpty()!! || miliexDate == 0L){
                    Toast.makeText(this,"Nhập đầy đủ thôngtin",Toast.LENGTH_LONG).show()
                    mDialogProgress?.dismiss()
                    bt_post?.visibility = View.VISIBLE
                }else{
                    mRetrofitService = ApiUtils.getAPI()
                    if(type==2){//==========Truong Hop k update image
                        mRetrofitService?.addProduct_nonImage(edit_nameproduct?.text.toString(),barcodeIn.toString(),miliexDate.toString(),"5b0bb59da2c5e873632215a2",edit_chitiet?.text.toString())?.enqueue(object: Callback<Result_Product>{
                            override fun onFailure(call: Call<Result_Product>?, t: Throwable?) {
                                Toast.makeText(this@Add_Product,"Error! \n message:"+t?.message,Toast.LENGTH_SHORT)
                                mDialogProgress?.dismiss()
                                bt_post?.visibility = View.VISIBLE
                            }

                            override fun onResponse(call: Call<Result_Product>?, response: Response<Result_Product>?) {
                                if(response?.isSuccessful!!){
                                    if(response?.code()==200){
                                        Toast.makeText(this@Add_Product,"Update Success!",Toast.LENGTH_SHORT)
                                        var i = Intent(this@Add_Product,HorizontalNtbActivity::class.java)
                                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        startActivity(i)
                                    }
                                }else{
                                    mDialogProgress?.dismiss()
                                    bt_post?.visibility = View.VISIBLE
                                    Toast.makeText(this@Add_Product,"Error! Code:"+response?.code()+"\n message:"+response?.message(),Toast.LENGTH_SHORT)
                                }
                            }

                        })
                    }else{ //==========Truong Hop  update image
                        val mediaFile = File(path)

                        val requestFile =
                                RequestBody.create(MediaType.parse("multipart/form-data"), CompressImage.compressImage(mediaFile,getApplicationContext()))
                        val photoproduct =
                                MultipartBody.Part.createFormData("image", mediaFile.getName(), requestFile)

                        val nameproduct =
                                RequestBody.create(MediaType.parse("multipart/form-data"), edit_nameproduct?.text.toString())
                        val hsd_ex =
                                RequestBody.create(MediaType.parse("multipart/form-data"),miliexDate.toString())
                        val detail =
                                RequestBody.create(MediaType.parse("multipart/form-data"),edit_chitiet?.text.toString())
                        val barcodenum =
                                RequestBody.create(MediaType.parse("multipart/form-data"),barcodeIn)

                        val iduser =
                                RequestBody.create(MediaType.parse("multipart/form-data"),"5b0bb59da2c5e873632215a2")

                        mRetrofitService?.addProduct(nameproduct,barcodenum,hsd_ex,detail,iduser,photoproduct)?.enqueue(object: Callback<Result_Product>{
                            override fun onFailure(call: Call<Result_Product>?, t: Throwable?) {
                                Toast.makeText(this@Add_Product,"Error! \n message:"+t?.message,Toast.LENGTH_SHORT).show()
                                mDialogProgress?.dismiss()
                                bt_post?.visibility = View.VISIBLE
                            }

                            override fun onResponse(call: Call<Result_Product>?, response: Response<Result_Product>?) {
                                if(response?.isSuccessful!!){
                                    if(response?.code()==200){
                                        Toast.makeText(this@Add_Product,"Update Success!",Toast.LENGTH_SHORT).show()
                                        var i = Intent(this@Add_Product,HorizontalNtbActivity::class.java)
                                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        startActivity(i)
                                    }
                                }else{
                                    mDialogProgress?.dismiss()
                                    bt_post?.visibility = View.VISIBLE
                                    Toast.makeText(this@Add_Product,"Error! Code:"+response?.code()+"\n message:"+response?.message(),Toast.LENGTH_SHORT).show()
                                }
                            }

                        })
                    }



                }
            }
        }
    }
    fun showHourPicker() {
        var mDate = ""
        var datedialog = AlertDialog.Builder(this@Add_Product)
        var mView:View = View.inflate(this@Add_Product,R.layout.dialog_timepicker,null)
        datedialog.setView(mView)
        var datepicker = mView.datepicker
        var txtout = mView.txt_out
        var txtok = mView.txt_ok
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        datepicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                DatePicker.OnDateChangedListener { datePicker, year, month, day->
                    mDate = day.toString()+"/"+(month+1)+"/"+year
                    //Toast.makeText(this@Add_Product,year.toString()+"//"+month+"//"+day,Toast.LENGTH_LONG).show()
                })
        txtout.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                mDate = ""
                mDialog?.dismiss()
            }

        })
        txtok.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                if(mDate != ""){
                    val stringToday = sdf.format(Date())
                    val exToday = sdf.parse(stringToday)
                    val exDate = sdf.parse(mDate)
                    miliexDate =  exDate.time
                    val miliexToday =  exToday.time
                    edit_ex?.setText(mDate)
                    updateEX(miliexToday, miliexDate!!)
                }
                mDialog?.dismiss()
            }

        })
        mDialog = datedialog.create()
        mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog?.show()
    }
    private fun showDialog() {
        val m = MaterialDialog.Builder(this@Add_Product)
                .content("Please wait...")
                .cancelable(false)
                .progress(true, 0)
        mDialogProgress = m.show()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mDialogProgress != null) {
            if (mDialogProgress?.isShowing()!!) {
                mDialogProgress?.dismiss()
                mDialogProgress = null
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (mDialogProgress != null) {
            if (mDialogProgress?.isShowing()!!) {
                mDialogProgress?.dismiss()

            }
        }
    }

    private fun updateEX(miliexToday:Long, miliexDate:Long) {
        if(miliexToday<miliexDate){
            val dis = (miliexDate/86400000 - miliexToday/86400000)
            txtEX?.setText(dis.toString() +" Ngày")
            txtEX?.setTextColor(resources.getColor(R.color.text_shadow))
            edit_ex?.setTextColor(resources.getColor(R.color.text_shadow))

        }else{
            edit_ex?.setTextColor(resources.getColor(R.color.abc_btn_colored_borderless_text_material))
            txtEX?.setText("Hết hạn")
            txtEX?.setTextColor(resources.getColor(R.color.abc_btn_colored_borderless_text_material))
        }
    }
    private fun initView() {
        img_product = findViewById<ImageView>(R.id.img_product)
        select_img = findViewById<LinearLayout>(R.id.select_img)
        scanner_ex = findViewById<ImageView>(R.id.scanner_ex)
        arrow_back= findViewById<ImageView>(R.id.arrow_back)
        edit_nameproduct = findViewById<EditText>(R.id.edit_nameproduct)
        barcode = findViewById<TextView>(R.id.txt_barcode)
        txtEX = findViewById<TextView>(R.id.txtEX)
        edit_ex = findViewById<EditText>(R.id.edit_ex)
        edit_chitiet = findViewById<EditText>(R.id.edit_chitiet)
        bt_post = findViewById<Button>(R.id.bt_post)

        select_img?.setOnClickListener(this)
        scanner_ex?.setOnClickListener(this)
        edit_ex?.setOnClickListener(this)
        img_product?.setOnClickListener(this)
        arrow_back?.setOnClickListener(this)
        bt_post?.setOnClickListener(this)

        currentDateandTime = sdf.format(Date())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RESULT_SCANNER){
            if(resultCode == 1000){
                var dateChoose = data?.getStringExtra("dateChoose")
                miliexDate = data?.getLongExtra("miliseconDate",20)
                var typeDate = data?.getIntExtra("getType",1)
                if(typeDate == 2)
                {
                    dateChoose = dateChoose?.substring(0,2)+"/"+dateChoose?.substring(2,4)+"/20"+dateChoose?.substring(4,6)
                }
                if(typeDate == 1)
                {
                    dateChoose = dateChoose?.substring(0,2)+"/"+dateChoose?.substring(2,4)+"/"+dateChoose?.substring(4,8)
                }

                edit_ex?.setText(dateChoose)
                updateEX(miliexToday!!,miliexDate!!)
            }
        }
    }
}