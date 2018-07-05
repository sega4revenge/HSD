package com.finger.hsd.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.finger.hsd.AllInOneActivity
import com.finger.hsd.BaseActivity
import com.finger.hsd.R
import com.finger.hsd.common.GlideApp
import com.finger.hsd.manager.RealmController
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.Result_Product
import com.finger.hsd.util.ApiUtils
import com.finger.hsd.util.CompressImage
import com.finger.hsd.util.ConnectivityChangeReceiver
import com.finger.hsd.util.RetrofitService
import kotlinx.android.synthetic.main.dialog_timepicker.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class Add_Product : BaseActivity() ,View.OnClickListener,RealmController.updateData,
        ConnectivityChangeReceiver.ConnectivityReceiverListener {
    private var img_product: ImageView? = null
    private var arrow_back: ImageView? = null
    private var select_img: LinearLayout? = null
    private var scanner_ex: ImageView? = null
    private var img_check_product: ImageView? = null
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
    private var myRealmNotNetwork:RealmController? = null
    private var myRealm:RealmController? = null
    private var mRetrofitService: RetrofitService? = null
    private var mDialog:Dialog? =null
    private final val CODE_RESULT_IMAGE_SELECT = 1001
    private  var idUser : String?= null
    var path:String? = null
    var pathUri:Uri? = null
    private var haveImage = false
    var barcodeIn:String? = null

    val options = RequestOptions()
            .centerCrop()
            .dontAnimate()
            .placeholder(R.mipmap.ic_launcher)
            .priority(Priority.HIGH)
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

    override fun onupdateProduct(type: Int, product: Product_v) {
        if(type!=0){
            Toast.makeText(this@Add_Product,"Create Success!",Toast.LENGTH_SHORT).show()
            var i = Intent(this@Add_Product,AllInOneActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(i)
        }else{
            Toast.makeText(this@Add_Product,"Update Faile With Photo Product not Syn!",Toast.LENGTH_SHORT).show()
            var i = Intent(this@Add_Product,AllInOneActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(i)
           // Toast.makeText(this@Add_Product,"Error Update Image",Toast.LENGTH_SHORT).show()
        }

    }
    override fun onupdateDelete() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onNetworkConnectionChanged(isConnected: Boolean) {

    }
    private fun getData() {
        val stringToday = sdf.format(Date())
        val exToday = sdf.parse(stringToday)
         miliexToday =  exToday.time

        val mData = intent
        type = mData.getIntExtra("type",1)

        if(type==1 || type ==0){
            path = mData.getStringExtra("path")
            pathUri = Uri.parse(path)
            Glide.with(this@Add_Product)
                    .load(pathUri?.path)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(img_product!!)
            if(type==1){
                barcodeIn = mData.getStringExtra("barcode")
            }
        }else if(type ==2 || type ==3){
            if(type==2)
            {
                path = mData.getStringExtra("path")

                Glide.with(this@Add_Product)
                        .load(path)
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(img_product!!)
            }
            barcodeIn = mData.getStringExtra("barcode")
            edit_nameproduct?.setText(mData.getStringExtra("name"))
            if(type==3)
            {
                if(mData.getBooleanExtra("checkProduct",false)){
                    edit_nameproduct?.isEnabled = false
                    select_img?.visibility = View.GONE
                    img_check_product?.visibility = View.VISIBLE
                }

                path = mData.getStringExtra("image")

                Glide.with(this@Add_Product)
                        .load(path)
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(img_product!!)
            }
//            if(type==3){
//                path = mData.getStringExtra("path")
//                Glide.with(this@Add_Product)
//                        .load(path)
//                        .thumbnail(0.1f)
//                        .apply(options)
//                        .into(img_product!!)
//                edit_chitiet?.setText(mData.getStringExtra("detail"))
//                edit_ex?.setText(mData.getStringExtra("ex"))
//                miliexDate = mData.getLongExtra("exTime",20)
//                if(!edit_ex?.text.isNullOrEmpty()){
//                    updateEX(miliexToday!!, miliexDate!!)
//                }
//
//            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            select_img?.id ->{
                dispatchTakePictureIntent(111)
//                val i = Intent(this@Add_Product,Custom_Camera_Activity::class.java)
//                    i.putExtra("type",CODE_RESULT_IMAGE_SELECT)
//                    i.putExtra("name",edit_nameproduct?.text.toString())
//                    i.putExtra("ex",edit_ex?.text.toString())
//                    i.putExtra("exTime",miliexDate)
//                    i.putExtra("detail",edit_chitiet?.text.toString())
//                    i.putExtra("barcode",barcode?.text.toString())
//                    startActivity(i)
//               //     startActivityForResult(i,CODE_RESULT_IMAGE_SELECT)
            }
            scanner_ex?.id ->{
                val i = Intent(this@Add_Product,Scanner_HSD_Activity::class.java)
                startActivityForResult(i,RESULT_SCANNER)
            }
            edit_ex?.id ->{
                showHourPicker()
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
//                showDialog()
                showProgress("Please wait...")
                if(path.isNullOrEmpty() || edit_nameproduct?.text.isNullOrEmpty() || edit_ex?.text.isNullOrEmpty() || txtEX?.text.isNullOrEmpty() || barcodeIn?.isNullOrEmpty()!! || miliexDate == 0L){
//                    Toast.makeText(this,"Nhập đầy đủ thông tin",Toast.LENGTH_LONG).show()
                    hideProgress()
                    bt_post?.visibility = View.VISIBLE
                }else{
                 if(ConnectivityChangeReceiver.isConnected()){
                    mRetrofitService = ApiUtils.getAPI()
                    if(type==2 || type==3){//==========Truong Hop k update image
                        mRetrofitService?.addProduct_nonImage(edit_nameproduct?.text.toString(),barcodeIn.toString(),miliexDate.toString(),idUser!!,edit_chitiet?.text.toString())?.enqueue(object: Callback<Result_Product>{
                            override fun onFailure(call: Call<Result_Product>?, t: Throwable?) {
                                Toast.makeText(this@Add_Product,"Error! \n message:"+t?.message,Toast.LENGTH_SHORT).show()
                              hideProgress()
                                bt_post?.visibility = View.VISIBLE
                            }

                            override fun onResponse(call: Call<Result_Product>?, response: Response<Result_Product>?) {
                                if(response?.isSuccessful!!){
                                    if(response?.code()==200){
                                        hideProgress()
                                        response?.body().product.imagechanged = path
                                        response?.body().product.barcode = barcodeIn
                                        response?.body().product.isSyn = true
                                        myRealm?.addProductWithNonImage(response?.body().product,this@Add_Product,this@Add_Product)
                                    }
                                }else{
                                    hideProgress()
                                    bt_post?.visibility = View.VISIBLE
                                    Toast.makeText(this@Add_Product,"Error! Code:"+response?.code()+"\n message:"+response?.message(),Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    }else{ //==========Truong Hop  update image
                            val mediaFile = File(pathUri?.path)

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
                                    RequestBody.create(MediaType.parse("multipart/form-data"),idUser)

                            mRetrofitService?.addProduct(nameproduct,barcodenum,hsd_ex,detail,iduser,photoproduct)?.enqueue(object: Callback<Result_Product>{
                                override fun onFailure(call: Call<Result_Product>?, t: Throwable?) {
                                    Toast.makeText(this@Add_Product,"Error! \n message:"+t?.message,Toast.LENGTH_SHORT).show()
                                 hideProgress()
                                    bt_post?.visibility = View.VISIBLE
                                }

                                override fun onResponse(call: Call<Result_Product>?, response: Response<Result_Product>?) {
                                    if(response?.isSuccessful!!){
                                        if(response.code()==200){
                                            saveimageinMyApp(response.body().product,pathUri!!)
//                                            Log.d("ErrorErrorError",response.body().product.toString()+"//"+response.body().product._id)
//                                            var mProduct = response.body().product
//                                            mProduct.imagechanged = path
//                                            mProduct.barcode = barcodeIn
//                                            mProduct.isSyn = true
//                                            myRealm?.addProduct(mProduct)
//                                            if(myRealm?.checkaddsuccess(mProduct._id!!)!!>0){
//                                                onupdateProduct(1,mProduct)
//                                            }
                                        }
                                        hideProgress()
                                    }else{
                                       hideProgress()
                                        bt_post?.visibility = View.VISIBLE
                                        Toast.makeText(this@Add_Product,"Error! Code:"+response?.code()+"\n message:"+response?.message(),Toast.LENGTH_SHORT).show()
                                    }
                                }

                            })
                        }

                    }else{
                     Toast.makeText(this@Add_Product,"Không thể kết nối máy chủ!",Toast.LENGTH_SHORT).show()

//                        val stringToday = sdf.format(Date())
//                        val exToday = sdf.parse(stringToday)
//                        var r =  Random()
//                        var ran = r.nextInt(2000)
//                        if(myRealm?.checkaddsuccess(exToday.time.toString())!!<=0){
//                            var mProduct = Product_v((exToday.time +ran).toString(),edit_nameproduct?.text.toString(),
//                                    barcodeIn!!,miliexDate!!,edit_chitiet?.text.toString(),path!!)
//                            mProduct.delete = false
//                            mProduct.isSyn = false
//                            myRealm?.addProduct(mProduct)
//                            if(myRealm?.checkaddsuccess(mProduct._id!!)!!>0){
//                                onupdateProduct(1,mProduct)
//                            }
            //            }
                     //   if(myRealm?.checkaddsuccess(mProduct._id)!!>0){
                     //        onupdateProduct(1)
                     //  }
                     hideProgress()
                    }


                }
                val view = this.currentFocus
                if (view != null) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
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


            // Save a file: path for use with ACTION_VIEW intents
            cameraImageUri = Uri.fromFile(imageFile)
        } catch (e: IOException) {
            e.printStackTrace()
            // errorMessage("Could not create imageFile for camera")
        }


        return imageFile
    }
    private fun dispatchTakePictureIntent(mType:Int) {
        var permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001)
        }else {
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
    override fun onupdate() {
    }
    fun saveimageinMyApp(product : Product_v,uri:Uri){
        var file = File(getRealFilePath(this, uri))
        GlideApp.with(this)
                .asBitmap()
                .load(file)
                .apply(options)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {

                        try {

                            var dataOffline = myRealm?.getProductWithBarcode(product?.producttype_id?.barcode!!)
                            if(dataOffline!= null)
                            {
                                val nameOldImage = Uri.parse(dataOffline.imagechanged)

                                var myDirOld = File(nameOldImage.path)

                                if (myDirOld.exists()) {
//                                    Mylog.d("aaaaaaaaaa deleted")
                                    myDirOld.delete()
                                }else{
//                                    Mylog.d("aaaaaaaaaa deleted"+nameOldImage)
                                }
                            }

                            val namePassive = product._id + "passive"+System.currentTimeMillis() + ".jpg"
                            val rootFolder = File(filesDir.toString() + "/files")
                            val myDir = File(rootFolder, namePassive)


                            if (myDir.exists())
                                myDir.delete()

                            val out3 = FileOutputStream(myDir)

                            resource.compress(Bitmap.CompressFormat.JPEG, 90, out3)

                            product.imagechanged = Uri.fromFile(myDir).toString()
                            product.barcode = barcodeIn
                            product.isSyn = true
                            myRealm?.addProduct(product)
                            if(myRealm?.checkaddsuccess(product._id!!)!!>0){
                                onupdateProduct(1,product)
                            }else{
                                // saveimageinMyApp(product,uri)
                                Toast.makeText(this@Add_Product,"Update Error",Toast.LENGTH_SHORT).show()
                            }

                            out3.flush()
                            out3.close()

                        } catch (e: Exception) {

                            println(e)

                        }

                    }
                })
    }
    fun getRealFilePath(context: Context, uri: Uri): String? {
        if (null == uri) return null
        val scheme: String = uri.scheme
        var data: String? = null
        if (scheme == null) {
            data = uri.path
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            val cursor = context.getContentResolver().query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
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
        val sdk = android.os.Build.VERSION.SDK_INT
//        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        } else {
            mDialog?.window?.setBackgroundDrawableResource(R.color.transparent_app);
//        }

        mDialog?.show()
    }
    private fun showDialog() {
        showProgress("Please wait...")
//        val m = MaterialDialog.Builder(this@Add_Product)
//                .content("Please wait...")
//                .cancelable(false)
//                .progress(true, 0)
//        mDialogProgress = m.show()
    }

    public override fun onDestroy() {
        super.onDestroy()

        hideProgress()
//        if (mDialogProgress != null) {
//            if (mDialogProgress?.isShowing()!!) {
//                mDialogProgress?.dismiss()
//                mDialogProgress = null
//            }
//        }
    }
    override fun onResume() {
        super.onResume()
        hideProgress()
//        if (mDialogProgress != null) {
//            if (mDialogProgress?.isShowing()!!) {
//                mDialogProgress?.dismiss()
//
//            }
//        }
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
        img_check_product =  findViewById<ImageView>(R.id.img_check_product)
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
        myRealm = RealmController(this@Add_Product)
        idUser= myRealm!!.getUser()!!._id!!
        currentDateandTime = sdf.format(Date())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode==111){
                if(!cameraImageUri.toString().isNullOrEmpty()){
                    type = 1
                    pathUri = cameraImageUri
                    Glide.with(this@Add_Product)
                            .load(pathUri?.path)
                            .thumbnail(0.1f)
                            .apply(options)
                            .into(img_product!!)
                }
            }
        }
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
