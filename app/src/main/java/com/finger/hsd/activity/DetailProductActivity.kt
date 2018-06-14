package com.finger.hsd.activity

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.Toolbar
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.androidnetworking.error.ANError
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.finger.hsd.BaseActivity
import com.finger.hsd.R
import com.finger.hsd.R.string.days
import com.finger.hsd.common.GlideApp
import com.finger.hsd.library.CompressImage
import com.finger.hsd.library.image.TedBottomPicker
import com.finger.hsd.manager.RealmController
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.Response
import com.finger.hsd.presenter.DetailProductPresenter
import com.finger.hsd.util.AppIntent
import com.finger.hsd.util.Constants
import com.finger.hsd.util.Mylog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail_product.*

import kotlinx.android.synthetic.main.dialog_timepicker.view.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*




class DetailProductActivity : BaseActivity(), DetailProductPresenter.IDetailProductPresenterView {


    private lateinit var mTvName: EditText
    private lateinit var mTvBarcode: TextView
    private lateinit var mTvExpiredtime: TextView
    private lateinit var mTvNote: EditText
    private lateinit var mTvSafe: TextView
    private lateinit var mTvWarning: TextView
    private lateinit var mTvDanger: TextView
    private lateinit var mTvStatus: TextView
    private lateinit var mTvDayCountDown: TextView
    private lateinit var mTvNotification: TextView
    private lateinit var mBtSave: Button
    private lateinit var mImProduct: ImageView
    private lateinit var mToolbar: Toolbar
    private lateinit var mImStatus: ImageView
    private lateinit var mImChange: ImageView
    private lateinit var mLayoutDayBefore: RelativeLayout

    private lateinit var idProduct: String

    lateinit var presenter: DetailProductPresenter;
    private var strImProduct: String = ""

    private var name: String? = null
    private var expiredTime: String? = null
    private var note: String? = null
    private var position: Int? = 0
    private var expiredTimeChange: String? = null

    var selectedUri: Uri? = null
    var daysbefor: Int? = 0
    var rootFolder: File? = null
    val calendar = Calendar.getInstance()
    private var realm: RealmController? = null
    private var mDialog:Dialog? =null
    
    var product: Product_v? = null

    val options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_add_photo)
            .error(R.drawable.ic_back)
            .priority(Priority.LOW)

    // ? sau Kiểu biến, thể hiện cho có thể cho phép null
    // !! sau biến, thể hiện cho không cho phép null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product!!)

        mTvName = findViewById(R.id.tv_name)
        mTvBarcode = findViewById(R.id.tv_barcode)
        mTvExpiredtime = findViewById(R.id.tv_expiredtime)
        mTvNote = findViewById(R.id.tv_note)
        mTvSafe = findViewById(R.id.tv_safe)
        mTvWarning = findViewById(R.id.tv_warning)
        mTvDanger = findViewById(R.id.tv_danger)
        mTvStatus = findViewById(R.id.tv_status)
        mTvDayCountDown = findViewById(R.id.tv_day_coudown)
        mTvNotification = findViewById(R.id.tv_notification)
        mBtSave = findViewById(R.id.bt_save)
        mImProduct = findViewById(R.id.im_product!!)
        mImStatus = findViewById(R.id.im_status)
        mImChange = findViewById(R.id.im_change)
        mLayoutDayBefore = findViewById(R.id.layout_choose_day_before)
        mToolbar = findViewById(R.id.toolbar)
        this.setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        mToolbar.setTitleTextColor(Color.GRAY)
        mToolbar.setSubtitleTextColor(Color.GRAY)
        mToolbar.setNavigationIcon(R.drawable.ic_move_through)
        mToolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                onBackPressed()
            }
        })

        rootFolder = File(filesDir.toString() + "/files")
        if (!rootFolder!!.exists()) {
            rootFolder!!.mkdirs()
        }
        val strDataIntent: String = intent.getStringExtra("id_product")
        position = intent.getIntExtra("position", -1)

        if (!strDataIntent.isEmpty())
            idProduct = strDataIntent

        realm = RealmController.with(this)
        presenter = DetailProductPresenter(this)

         product= realm!!.getProduct(idProduct)
//        if(isInternetAvailable()) {
        //  presenter.processDetailProduct(idProduct)
//        }else{
        getDataFromRealm()
//        }


        mBtSave.setOnClickListener {
            /*
            * update to realm*/
            val noteChange = mTvNote.text.toString()
            val nameChange = mTvName.text.toString()
            if ((!TextUtils.isEmpty(noteChange) && !note.equals(noteChange))
                    || (!TextUtils.isEmpty(noteChange) && !name.equals(nameChange))
                    || (!TextUtils.isEmpty(expiredTimeChange) && !expiredTime.equals(expiredTimeChange))) {

          
                if (!TextUtils.isEmpty(expiredTimeChange) && !expiredTime.equals(expiredTimeChange)) {
                    product!!!!.expiretime = expiredTimeChange!!.toLong()

                    var days = 0


                    var longExpiredTime = expiredTimeChange!!.toLong()
                    calendar.timeInMillis = longExpiredTime
                    expiredTime = expiredTimeChange

                    days = daysBetween(System.currentTimeMillis(), longExpiredTime)

                    getWarningStatus(days)

                }
                if (!TextUtils.isEmpty(noteChange) && !name.equals(nameChange)) {
                    product!!!!.namechanged = nameChange


                }
                if (!TextUtils.isEmpty(note) && !noteChange.equals(note)) {
                    product!!!!.description = noteChange
                }


                if (selectedUri != null) {
//********* UPDATE image NEEUS NHU co thay đổi  *************
                    var file = File(getRealFilePath(this, selectedUri!!))

                    GlideApp.with(this)
                            .asBitmap()
                            .load(file)
                            .apply(options)
                            .into(object : SimpleTarget<Bitmap>() {
                                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {

                                    try {
                                        val namePassive = product!!!!._id + "passive" + ".jpg"

                                        var myDir = File(rootFolder, namePassive)

                                        Mylog.d("aaaaaaaaaa my dir: " + myDir)

                                        if (myDir.exists())
                                            myDir.delete()

                                        val out3 = FileOutputStream(myDir)

                                        resource?.compress(Bitmap.CompressFormat.JPEG, 90, out3)

                                        product!!.imagechanged = Uri.fromFile(myDir).toString()

                                        out3.flush()
                                        out3.close()


                                    } catch (e: Exception) {

                                        println(e)

                                    }


                                }
                            })


                    //  UploadImage(idProduct, file)
                }
                realm!!.updateProduct(product!!!!)

                val intent = Intent()
                intent.putExtra("position", position!!)
                intent.putExtra("product!!_v", product!!)
                setResult(AppIntent.RESULT_UPDATE_ITEM, intent)

                finish()


            } else if (selectedUri != null) {
                var file = File(getRealFilePath(this, selectedUri!!))
                UploadImage(idProduct, file)
            }


            /*
            * update server
            * */
//            val noteChange = mTvNote.text.toString()
//            val nameChange = mTvName.text.toString()
//            if((!TextUtils.isEmpty(note) && !noteChange.equals(note))
//                    || (!TextUtils.isEmpty(noteChange) && !name.equals(nameChange))
//                    ||(!TextUtils.isEmpty(expiredTimeChange) && !expiredTime.equals(expiredTimeChange)) ) {
//
//                presenter.processInfomationUpdate(idProduct, nameChange, expiredTimeChange, noteChange)
//            }else if (selectedUri != null) {
//                var file = File(getRealFilePath(this, selectedUri!!))
//                UploadImage(idProduct, file )
//            }
        }

        mImChange.setOnClickListener {

            showImage()

        }
        mLayoutDayBefore.setOnClickListener {
            var intent = Intent(this, ChooseDayNotification::class.java)
            intent.putExtra("id_product", idProduct)
            intent.putExtra("day_before", days)
            startActivityForResult(intent, Constants.REQUEST_DAY_BEFORE)
        }
        ln_expiredtime.setOnClickListener {
            showDialogCustomDay()
        }
    }


    fun showImage() {

        val permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {

                val bottomSheetDialogFragment = TedBottomPicker.Builder(this@DetailProductActivity)
                        .setOnImageSelectedListener(object : TedBottomPicker.OnImageSelectedListener {
                            override fun onImageSelected(uri: Uri) {
                                Log.d("ted", "uri: $uri")
                                Log.d("ted", "uri.getPath(): " + uri.path)
                                selectedUri = uri
                                val options = RequestOptions()
                                        .dontAnimate()
                                        .placeholder(R.mipmap.ic_launcher)
                                        .priority(Priority.HIGH)
                                Glide.with(this@DetailProductActivity)
                                        .load(uri)
                                        .thumbnail(0.1f)
                                        .apply(options)
                                        .into(mImProduct)

                            }
                        })
                        //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                        .setSelectedUri(selectedUri)
                        .setPeekHeight(1200)
                        .create()

                bottomSheetDialogFragment.show(supportFragmentManager)
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                Toast.makeText(this@DetailProductActivity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
            }


        }

        TedPermission(this@DetailProductActivity)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions " +
                        "at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_DAY_BEFORE) {
            if (resultCode == Constants.RESULT_DAY_BEFORE) {
                daysbefor = data!!.getIntExtra(Constants.DATA_DAY_BEFORE, 0)
                val strText =
                        resources.getString(R.string.recieve_one_notification) + " (" + daysbefor + " " + resources.getString(R.string.days) + ")"
                mTvNotification.text = strText

            }
        }
    }

    override fun onSucess(response: JSONObject, type: Int) {
        //*********thong tin của product!!*************

        if (type == 111) {
            // realm!!.deleteProduct(idProduct)

            val jsonInfoProduct = JSONObject(response.getString("info_product!!"))
            val jsonInfoProductType = JSONObject(jsonInfoProduct.getString("product!!type_id"))
            if (jsonInfoProduct.isNull("daybefor")) daysbefor = 2
            else {
                daysbefor = jsonInfoProduct.getInt("daybefor")
            }

            if (jsonInfoProduct.isNull("description")) mTvNote.setHint(resources.getString(R.string.note))
            else {
                note = jsonInfoProduct.getString("description").toString()
                mTvNote.setText(note)
            }

            val strText =
                    resources.getString(R.string.recieve_one_notification) + " (" + daysbefor + " " + resources.getString(R.string.days) + ")"

            mTvNotification.text = strText


            idProduct = jsonInfoProduct.getString("_id")
            if (jsonInfoProductType.isNull("name")) mTvName.setHint(resources.getString(R.string.no_name)) else mTvName.setText(jsonInfoProductType.getString("name"))
            if (jsonInfoProductType.isNull("image")) strImProduct = ""
            else {
                strImProduct = jsonInfoProductType.getString("image")
                val options = RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .placeholder(R.mipmap.ic_launcher)
                        .priority(Priority.HIGH)
                Glide.with(this@DetailProductActivity)
                        .load(Constants.IMAGE_URL + strImProduct)
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(mImProduct)
            }
            if (jsonInfoProduct.isNull("namechanged")) null else {
                name = jsonInfoProduct.getString("namechanged")
                mTvName.setText(name)
            }
            if (jsonInfoProduct.isNull("imagechanged")) null else {
                strImProduct = jsonInfoProduct.getString("imagechanged")
                val options = RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .placeholder(R.mipmap.ic_launcher_round)
                        .priority(Priority.HIGH)
                Glide.with(this@DetailProductActivity)
                        .load(Constants.IMAGE_URL + strImProduct)
                        .thumbnail(0.1f)
                        .apply(options)
                        .into(mImProduct)
            }

            //  mTvBarcode.text = response.getString("barcode")

            var days = 0
            if (jsonInfoProduct.isNull("expiretime")) days = 0
            else {

                expiredTime = jsonInfoProduct.getString("expiretime")
                var longExpiredTime = expiredTime!!.toLong()

                calendar.timeInMillis = longExpiredTime
                val date = Date(longExpiredTime)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                tv_expiredtime.text = dateFormat.format(date)
                days = daysBetween(System.currentTimeMillis(), expiredTime!!.toLong())
            }
            var delete = false
            if (jsonInfoProduct.isNull("delete")) delete = false
            else {

                delete = jsonInfoProduct.getBoolean("delete")
            }
            getWarningStatus(days)


            //   realm!!.updateProduct(product!!)
            //********* UPDATE THÀNH CÔNG *************
        } else if (type == 333) {
            val jsonInfoProduct = JSONObject(response.getString("info_product!!"))
            var days = 0
            if (jsonInfoProduct.isNull("expiretime")) days = 0
            else {

                expiredTime = jsonInfoProduct.getString("expiretime")
                var longExpiredTime = expiredTime!!.toLong()
                calendar.timeInMillis = longExpiredTime
                val date = Date(expiredTime!!.toLong())
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                mTvExpiredtime.text = dateFormat.format(date)
                days = daysBetween(System.currentTimeMillis(), longExpiredTime)
            }

            getWarningStatus(days)

            if (selectedUri != null) {
//********* UPDATE image NEEUS NHU co thay đổi  *************
                var file = File(getRealFilePath(this, selectedUri!!))
                UploadImage(idProduct, file)
            } else {
                val intent = Intent()
                intent.putExtra(AppIntent.DATA_UPDATE_ITEM, position)
                intent.putExtra("expredTime", expiredTime)
                intent.putExtra("name", jsonInfoProduct.getString("namechanged"))
                setResult(AppIntent.RESULT_UPDATE_ITEM, intent)

                finish()
            }

        }
//********* XÓA SẢN PHẨM THÀNH CÔNG*************
        else if (type == 222) {
            showToast(R.string.delete_sucess)
            finish()
        }

    }

    override fun onError(typeError: Int) {
        // thông tin sản phẩm
        if (typeError == 111) {

            //getDataFromRealm()


        } else
        // xóa sản phẩm
            if (typeError == 222) {

                product!!.isDelete = true
                product!!._id = idProduct
                realm!!.updateProduct(product!!)

            } else
            // update thông tin: image, name, note, expiredtime
                if (typeError == 333) {
                    note = mTvNote.text.toString()
                    name = mTvName.text.toString()
                    presenter.processInfomationUpdate(idProduct, name, expiredTime, note)

                    product!!.namechanged = name
                    product!!._id = idProduct
                    if (!TextUtils.isEmpty(note))
                        product!!.description = note
                    if (!TextUtils.isEmpty(expiredTime))
                        product!!.expiretime = expiredTime!!.toLong()

                    realm!!.updateProduct(product!!)
                }
    }

    //===========LẤY DỮ LIỆU OFFLINE----------------
    fun getDataFromRealm() {
     
        //  var product!!Type = product!!!!.product!!TypeId
        if (product!!!!.daybefore == 0) daysbefor = 2
        else {
            daysbefor = product!!.daybefore
        }
        if (TextUtils.isEmpty(product!!!!.description)) mTvNote.setHint(resources.getString(R.string.note))
        else {
            mTvNote.setText(product!!.description.toString())
        }

        val strText =
                resources.getString(R.string.recieve_one_notification) + " (" + daysbefor + " " + resources.getString(R.string.days) + ")"

        mTvNotification.text = strText

        idProduct = product!!._id!!
        if (TextUtils.isEmpty(product!!!!.namechanged)) mTvName.setHint(resources.getString(R.string.no_name))
        else mTvName.setText(product!!!!.namechanged)
        if (TextUtils.isEmpty(product!!!!.imagechanged)) strImProduct = ""
        else {
            strImProduct = product!!!!.imagechanged.toString()
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.mipmap.ic_launcher)
                    .priority(Priority.HIGH)
            Glide.with(this@DetailProductActivity)
                    .load(Constants.IMAGE_URL + strImProduct)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(mImProduct)
        }
        if (TextUtils.isEmpty(product!!.namechanged)) null else mTvName.setText(product!!.namechanged)
        if (TextUtils.isEmpty(product!!.imagechanged)) null else {
            strImProduct = product!!.imagechanged.toString()
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.mipmap.ic_launcher_round)
                    .priority(Priority.HIGH)
            Glide.with(this@DetailProductActivity)
                    .load(Constants.IMAGE_URL + strImProduct)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(mImProduct)
        }

        //  mTvBarcode.text = response.getString("barcode")

        var days = 0
        if (TextUtils.isEmpty(product!!.expiretime.toString())) days = 0
        else {

            expiredTime = product!!.expiretime.toString()
            var longExpiredTime = expiredTime!!.toLong()

            calendar.timeInMillis = longExpiredTime
            val date = Date(longExpiredTime)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            tv_expiredtime.text = dateFormat.format(date)
            days = daysBetween(System.currentTimeMillis(), expiredTime!!.toLong())
        }

        getWarningStatus(days)

    }

    fun getWarningStatus(days: Int) {
        var txt: String? = null
        if (days > 0 && days < 10) {
            // warning
            txt = "<font color ='#fc9a1b'> " + days + "</font>"
            setColorForLeverWarning(R.drawable.roundedtext_grey, R.drawable.roundedtext_orange, R.drawable.roundedtext_grey)
            mImStatus.setBackgroundColor(resources.getColor(R.color.orange))
            val strDayCountDown = resources.getString(R.string.shelf_life) + " " + days + " " + resources.getString(R.string.detail_product_text_day)
            mTvDayCountDown.text = strDayCountDown
            mTvDayCountDown.setTextColor(resources.getColor(R.color.orange))
        } else if (days <= 0) {
            // danger
            txt = "<font color ='#FF4081'> " + days + "</font>"
            setColorForLeverWarning(R.drawable.roundedtext_grey, R.drawable.roundedtext_grey, R.drawable.roundedtext_red)
            mImStatus.setBackgroundColor(resources.getColor(R.color.red))
            val strDayCountDown = resources.getString(R.string.shelf_life) + " " + days + " " + resources.getString(R.string.detail_product_text_day)
            mTvDayCountDown.text = strDayCountDown
            mTvDayCountDown.setTextColor(resources.getColor(R.color.red))
        } else if (days >= 10) {
            // safe: an toan
            txt = "<font color ='#19a5f5' size = '50'> " + days + "</font>"
            setColorForLeverWarning(R.drawable.roundedtext_blue, R.drawable.roundedtext_grey, R.drawable.roundedtext_grey)
            mImStatus.setBackgroundColor(resources.getColor(R.color.blue))
            val strDayCountDown = resources.getString(R.string.shelf_life) + " " + days + " " + resources.getString(R.string.detail_product_text_day)
            mTvDayCountDown.text = strDayCountDown
            mTvDayCountDown.setTextColor(resources.getColor(R.color.blue))

        }

        mTvStatus.text = fromHtml(resources.getString(R.string.detail_product_text_expiry_date) + " " + txt!! + " " +
                resources.getString(R.string.detail_product_text_day)
        )
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


    fun fromHtml(html: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }

    fun setColorForLeverWarning(idSafe: Int, idWarning: Int, idDanger: Int) {
        mTvSafe.setBackgroundResource(idSafe)
        mTvDanger.setBackgroundResource(idDanger)
        mTvWarning.setBackgroundResource(idWarning)
    }

    // xử lý ngày -> trả về số ngày
    fun daysBetween(startDate: Long, endDate: Long): Int {

        val calendarStart = Calendar.getInstance()
        calendarStart.timeInMillis = startDate
        calendarStart.set(Calendar.HOUR_OF_DAY, 0)
        calendarStart.set(Calendar.MINUTE, 0)
        calendarStart.set(Calendar.SECOND, 0)
        calendarStart.set(Calendar.MILLISECOND, 0)

        val calendarEnd = Calendar.getInstance()
        calendarEnd.timeInMillis = endDate
        calendarEnd.set(Calendar.HOUR_OF_DAY, 0)
        calendarEnd.set(Calendar.MINUTE, 0)
        calendarEnd.set(Calendar.SECOND, 0)
        calendarEnd.set(Calendar.MILLISECOND, 0)

        val start: Long = calendarStart.timeInMillis
        val end: Long = calendarEnd.timeInMillis

        return java.util.concurrent.TimeUnit.MILLISECONDS.toDays((end - start)).toInt()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail_product!!, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.item_delete) {

            showDialogDelete(idProduct)
            return true
        } else if (item.itemId == R.id.home) {
            finish()
            return true
        } else return super.onOptionsItemSelected(item)

    }


    fun onPositiveDelete() {
        Mylog.d("aaaaaaaaaa chay ngay di")
    }

    fun onNegativeDelete() {
        Mylog.d("aaaaaaaaaa chay ngay di")
    }

    //=========DIALOG SHOW CHOOSE DATE=================
//    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
//        var montht = month + 1
//        tv_expiredtime.text = day.toString() + "/" + montht.toString() + "/" + year.toString()
//
//        calendar.set(year, month, day)
//        expiredTimeChange = calendar.timeInMillis.toString()
//
//    }

    fun showDialogCustomDay() {


        var mDate = ""
        var datedialog = AlertDialog.Builder(this)
        var mView:View = View.inflate(this,R.layout.dialog_timepicker,null)
        datedialog.setView(mView)
        var datepicker = mView.datepicker
        var txtout = mView.txt_out
        var txtok = mView.txt_ok
        val calendarChange = calendar

        datepicker.init(calendarChange.get(Calendar.YEAR), calendarChange.get(Calendar.MONTH), calendarChange.get(Calendar.DAY_OF_MONTH),
                DatePicker.OnDateChangedListener { datePicker, year, month, day->
                    mDate = day.toString()+"/"+(month+1)+"/"+year
                    calendarChange.set(year, month, day)
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

                    expiredTimeChange = calendarChange.timeInMillis.toString()

                    val date = Date(calendarChange.timeInMillis)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    tv_expiredtime.text = dateFormat.format(date)
                }
                mDialog?.dismiss()
            }

        })
        mDialog = datedialog.create()
        mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog?.show()
    }

//    private fun showDialogCustomDay() {
////        // dialog declare
////        val dFragment = CustomeDatePickerFragment()
////        dFragment.show(fragmentManager, "Date Picker")
//        val dpd = DatePickerDialog(this,
//                AlertDialog.THEME_HOLO_LIGHT, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH))
//
//        dpd.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), object : DialogInterface.OnClickListener {
//            override fun onClick(p0: DialogInterface?, p1: Int) {
//                dpd.dismiss()
//            }
//        })
//
//        dpd.setCustomTitle(null)
//        dpd.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dpd.show()
//    }

    //============ dialog delete product!! =================
    private fun showDialogDelete(idProduct: String) {
        // dialog declare
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.fragment_dialog_all)

        val btnDialog_cancel = dialog.findViewById(R.id.bt_cancel) as Button
        val dialog_ok = dialog.findViewById(R.id.bt_ok) as Button
        // handle event click button
        btnDialog_cancel.setOnClickListener({

            dialog.dismiss()
            finish()

        })
        dialog_ok.setOnClickListener({

            //  presenter.processDeleteProduct(idProduct)
            var product_v = realm!!.getProduct(idProduct)
            product_v!!.isDelete = true
            product_v!!.isChecksync = false
            realm!!.updateProduct(product_v)
            val intent = Intent()
            intent.putExtra(AppIntent.DATA_UPDATE_ITEM, position)
            intent.putExtra("product!!_v", product_v)
            intent.putExtra("position", position)
            setResult(AppIntent.RESULT_DELETE_ITEM, intent)

            dialog.dismiss()
            finish()

        })
        dialog.show()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outR = Rect()
                v.getGlobalVisibleRect(outR)
                val isKeyboardOpen = !outR.contains(ev.rawX.toInt(), ev.rawY.toInt())
                print("Is Keyboard? $isKeyboardOpen")
                if (isKeyboardOpen) {
                    print("Entro al IF")
                    v.clearFocus()
                    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }

                v.isCursorVisible = !isKeyboardOpen

            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun UploadImage(idProduct: String, file: File?) {
        val disposable = CompositeDisposable()
        Rx2AndroidNetworking.upload(Constants.URL_UPDATE_IMAGE)
                .addMultipartParameter("id_product", idProduct)
                .addMultipartFile("image", CompressImage.compressImage(this@DetailProductActivity, file))
                .build()
                .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache -> }

                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            Mylog.d(e.message!!)
                        }
                    }

                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(response: Response) {
                        val intent = Intent()
                        intent.putExtra(AppIntent.DATA_UPDATE_ITEM, position)
                        intent.putExtra("expredTime", expiredTime)
                        intent.putExtra("name", response.nameProduct)
                        intent.putExtra("image", response.image)
                        setResult(AppIntent.RESULT_UPDATE_ITEM, intent)

                        finish()

                        finish()


                    }
                })

    }
}