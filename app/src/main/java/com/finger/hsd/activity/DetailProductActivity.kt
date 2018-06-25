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
import android.graphics.drawable.Drawable
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
    private var mDialog: Dialog? = null

    var product: Product_v? = null
    var checkNotification: Boolean = false

    val options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_add_photo)
            .error(R.drawable.ic_back)
            .priority(Priority.LOW)

    // ? sau Kiểu biến, thể hiện cho có thể cho phép null
    // !! sau biến, thể hiện cho không cho phép null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)

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
        mImProduct = findViewById(R.id.im_product)
        mImStatus = findViewById(R.id.im_status)
        mImChange = findViewById(R.id.im_change)
        mLayoutDayBefore = findViewById(R.id.layout_choose_day_before)
        mToolbar = findViewById(R.id.toolbar)
        this.setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        mToolbar.setTitleTextColor(Color.WHITE)
        mToolbar.setTitle(resources.getString(R.string.detail_product))
        mToolbar.setNavigationIcon(R.drawable.ic_back_arrow)
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
        checkNotification = intent.getBooleanExtra("checkNotification", false)

        if (!strDataIntent.isEmpty())
            idProduct = strDataIntent

        realm = RealmController.with(this)
        presenter = DetailProductPresenter(this)

        product = realm!!.getProduct(idProduct)
//        if(isInternetAvailable()) {
        //  presenter.processDetailProduct(idProduct)
//        }else{
        getDataFromRealm()
//        }


        mBtSave.setOnClickListener {

            val noteChange: String? = mTvNote.text.toString()
            val nameChange: String? = mTvName.text.toString()

            if (ConnectivityChangeReceiver.isConnected()) {
                if ((!noteChange.equals(note))
                        || (!name.equals(nameChange))
                        || (expiredTimeChange!=null && !expiredTime.equals(expiredTimeChange))) {

                    Mylog.d("ttttttttt chay chua: "+expiredTimeChange)
                    showProgress()
                    presenter.processInfomationUpdate(idProduct, nameChange, expiredTimeChange, noteChange)

                } else if (selectedUri != null) {
                    Mylog.d("ttttttttt updateImage sau if(name!,name!..so on): "+selectedUri)
                    var file = File(getRealFilePath(this, selectedUri!!))
//                    realm!!.realm.executeTransaction(Realm.Transaction {
//                        product!!.imagechanged = selectedUri.toString()
//                    })

                    showProgress()
//                    val imageConvertToUri = Uri.parse(product!!.imagechanged)
//                    val fdelete = File(imageConvertToUri.path)
//                    if (fdelete.exists()) {
//                        if (fdelete.delete()) {
//                           Mylog.d("aaaaaaa file Deleted :")
//                        } else {
//                           Mylog.d("aaaaaaa file not Deleted :")
//                        }
//                    }else{
//                        Mylog.d("aaaaaaaa Don't Find File!! "+fdelete.absoluteFile+"//"+fdelete.exists())
//                    }

                    GlideApp.with(this)
                            .asBitmap()
                            .load(file)
                            .apply(options)
                            .into(object : SimpleTarget<Bitmap>() {

                                override fun onLoadFailed(errorDrawable: Drawable?) {
                                    super.onLoadFailed(errorDrawable)

                                    Mylog.d("aaaaaaaaaa failed: " + errorDrawable)
                                }

                                override fun onResourceReady(resource: Bitmap,
                                                             transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {

                                    try {

                                        val namePassive = Uri.parse(product!!.imagechanged)
//                                        val namePassive = product!!.imagechanged

                                        var myDir = File(namePassive.path)

                                        if (myDir.exists()) {
                                            Mylog.d("aaaaaaaaaa deleted")
                                            myDir.delete()
                                        }else{
                                            Mylog.d("aaaaaaaaaa deleted"+namePassive)
                                        }

                                        val namePass = product!!._id + "passive"+System.currentTimeMillis() + ".jpg"

                                        var myD = File(rootFolder, namePass)
                                        val out3 = FileOutputStream(myD)

                                        resource.compress(Bitmap.CompressFormat.JPEG, 90, out3)



                                        //changeProduct!!.imagechanged = Uri.fromFile(myDir).toString()
                                        realm!!.realm.executeTransaction(Realm.Transaction {
                                            product!!.imagechanged = Uri.fromFile(myD).toString()
                                            Mylog.d("aaaaaaaaaa image after: " + Uri.fromFile(myD).toString())

                                        })

                                        out3.flush()
                                        out3.close()

                                        Mylog.d("ttttttt image : " + Uri.fromFile(myD).toString())
                                        UploadImage(idProduct, file)

                                    } catch (e: Exception) {

                                        println(e)

                                    }

                                }
                            })
                }
            } else {
                showProgress()
                updateToRealm(noteChange!!, nameChange!!)
            }

        }

        mImChange.setOnClickListener {

            showImage()

        }
        mLayoutDayBefore.setOnClickListener {
            var intent = Intent(this, ChooseDayNotification::class.java)
            intent.putExtra("id_product", idProduct)
            intent.putExtra("day_before", realm!!.getProduct(idProduct)!!.daybefore)
            startActivityForResult(intent, Constants.REQUEST_DAY_BEFORE)
        }
        ln_expiredtime.setOnClickListener {
            showDialogCustomDay()
        }
    }

    fun updateToRealm(noteChange: String, nameChange: String) {
        /*
          * update to realm*/
        // val changeProduct = product
        if (( !note.equals(noteChange)) || (!name.equals(nameChange))
                || (expiredTimeChange != null && !expiredTime.equals(expiredTimeChange))) {

            if (expiredTimeChange != null && !expiredTime.equals(expiredTimeChange)) {
                //  changeProduct!!.expiretime = expiredTimeChange!!.toLong()
                realm!!.realm.executeTransaction(Realm.Transaction {
                    product!!.expiretime = expiredTimeChange!!.toLong()
                })
                var days = 0

                var longExpiredTime = expiredTimeChange!!.toLong()
                calendar.timeInMillis = longExpiredTime
                expiredTime = expiredTimeChange

                days = daysBetween(System.currentTimeMillis(), longExpiredTime)

                getWarningStatus(days)

            }
            if ( !name.equals(nameChange)) {
                //changeProduct!!.namechanged = nameChange
                realm!!.realm.executeTransaction(Realm.Transaction {
                    product!!.namechanged = nameChange
                })


            }
            if (!noteChange.equals(note)) {
                //changeProduct!!.description = noteChange
                realm!!.realm.executeTransaction(Realm.Transaction {
                    product!!.description = noteChange
                })

            }

            if (expiredTimeChange != null && !expiredTime.equals(expiredTimeChange)) {
                realm!!.realm.executeTransaction(Realm.Transaction {
                    product!!.expiretime = expiredTimeChange!!.toLong()
                })
            }
//fasdf


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
                                    val namePassive = Uri.parse(product!!.imagechanged)
//                                        val namePassive = product!!.imagechanged

                                    var myDir = File(namePassive.path)

                                    if (myDir.exists()) {
                                        Mylog.d("aaaaaaaaaa deleted")
                                        myDir.delete()
                                    }else{
                                        Mylog.d("aaaaaaaaaa deleted"+namePassive)
                                    }

                                    val namePass = product!!._id + "passive"+System.currentTimeMillis() + ".jpg"

                                    var myD = File(rootFolder, namePass)
                                    val out3 = FileOutputStream(myD)

                                    resource.compress(Bitmap.CompressFormat.JPEG, 90, out3)



                                    //changeProduct!!.imagechanged = Uri.fromFile(myDir).toString()
                                    realm!!.realm.executeTransaction(Realm.Transaction {
                                        product!!.imagechanged = Uri.fromFile(myD).toString()
                                        Mylog.d("aaaaaaaaaa image after: " + Uri.fromFile(myD).toString())

                                    })
                                    out3.flush()
                                    out3.close()

                                    putIntenDataBack(false, null, null, null)


                                } catch (e: Exception) {

                                    println(e)

                                }

                            }
                        })
            } else {

                putIntenDataBack(false, null, null, null)
            }

        } else if (selectedUri != null) {

            var file = File(getRealFilePath(this, selectedUri!!))

            GlideApp.with(this)
                    .asBitmap()
                    .load(file)
                    .apply(options)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {

                            try {
                                val namePassive = Uri.parse(product!!.imagechanged)
//                                        val namePassive = product!!.imagechanged

                                var myDir = File(namePassive.path)

                                if (myDir.exists()) {
                                    Mylog.d("aaaaaaaaaa deleted")
                                    myDir.delete()
                                }else{
                                    Mylog.d("aaaaaaaaaa deleted"+namePassive)
                                }

                                val namePass = product!!._id + "passive"+System.currentTimeMillis() + ".jpg"

                                var myD = File(rootFolder, namePass)
                                val out3 = FileOutputStream(myD)

                                resource.compress(Bitmap.CompressFormat.JPEG, 90, out3)



                                //changeProduct!!.imagechanged = Uri.fromFile(myDir).toString()
                                realm!!.realm.executeTransaction(Realm.Transaction {
                                    product!!.imagechanged = Uri.fromFile(myD).toString()
                                    Mylog.d("aaaaaaaaaa image after: " + Uri.fromFile(myD).toString())

                                })

                                out3.flush()
                                out3.close()

                                putIntenDataBack(false, null, null, null)


                            } catch (e: Exception) {

                                println(e)

                            }

                        }
                    })
        }

        hideProgress()
    }

    fun putIntenDataBack(isSync: Boolean, nameChanged: String?, description: String?, dateChange : Long?) {
//        val intent = Intent()
//        intent.putExtra("position", position!!)
//        intent.putExtra("id_product", idProduct)
//        setResult(AppIntent.RESULT_UPDATE_ITEM, intent)
//        finish()
//        changeProduct.isSyn = false
//        realm!!.updateProduct(changeProduct)

        if(isSync){
            realm!!.realm.executeTransaction(Realm.Transaction {
                product!!.namechanged = nameChanged
                product!!.description = description
                product!!.expiretime = dateChange!!
                product!!.isSyn = isSync
            })

        }else{
            realm!!.realm.executeTransaction(Realm.Transaction {
                product!!.isSyn = isSync
            })
        }
        hideProgress()

        val a = Intent()
        a.putExtra("updateItem", true)

        a.action = AppIntent.ACTION_UPDATE_ITEM
        if (!checkNotification) {
            a.putExtra("reloaditem", true)
            Mylog.d("aaaaaaaaa home " + AppIntent.ACTION_UPDATE_ITEM)
        } else {
            a.putExtra("reloaditem", false)
            Mylog.d("aaaaaaaaa notification " + AppIntent.ACTION_UPDATE_ITEM)
        }
        a.putExtra("position", position!!)
        a.putExtra("id_product", idProduct)
        sendBroadcast(a)
        finish()


    }

    fun showImage() {

        val permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {

                val bottomSheetDialogFragment = TedBottomPicker.Builder(this@DetailProductActivity)
                        .setOnImageSelectedListener(object : TedBottomPicker.OnImageSelectedListener {
                            override fun onImageSelected(uri: Uri) {
                                Log.d("selected ted: ", "uri: $uri")
                                Log.d("selected path: ", "uri.getPath(): " + uri.path)
                                selectedUri = uri

                                GlideApp
                                .with(this@DetailProductActivity)
                                        .load(uri)
                                        .thumbnail(0.1f)
                                        .apply(options)
                                        .into(findViewById(R.id.im_product))

                            }
                        })
                        .setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                        .setSelectedUri(selectedUri)
//                        .setPeekHeight(800)
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
                .setPermissions(Manifest.permission.CAMERA)
                .check()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_DAY_BEFORE) {
            if (resultCode == Constants.RESULT_DAY_BEFORE) {
                daysbefor = data!!.getIntExtra(Constants.DATA_DAY_BEFORE, 0)
                val strText =
                        resources.getString(R.string.recieve_one_notification) + " (" + daysbefor + " " +
                                resources.getString(R.string.days) + ")"
                mTvNotification.text = strText

            }
        }
    }

    override fun onSucess(response: Product_v, type: Int) {

        if (type == 333) {

            var days = 0


            expiredTime = response.expiretime.toString()
            Mylog.d("tttttttttt update success:  "+expiredTime)
            var longExpiredTime = expiredTime!!.toLong()
            calendar.timeInMillis = longExpiredTime
            val date = Date(expiredTime!!.toLong())
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            mTvExpiredtime.text = dateFormat.format(date)
            days = daysBetween(System.currentTimeMillis(), longExpiredTime)

            getWarningStatus(days)

            if (selectedUri != null) {
//********* UPDATE image NEEUS NHU co thay đổi  *************
                Mylog.d("ttttttttt updateImage onSucess: "+selectedUri)
                var file = File(getRealFilePath(this, selectedUri!!))
                GlideApp.with(this)
                        .asBitmap()
                        .load(selectedUri)
                        .apply(options)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap,
                                                         transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {

                                try {
                                    val namePassive = product!!._id + "passive" + ".jpg"

                                    var myDir = File(rootFolder, namePassive)

                                    Mylog.d("aaaaaaaaaa my dir: " + myDir)

                                    if (myDir.exists())
                                        myDir.delete()

                                    val out3 = FileOutputStream(myDir)

                                    resource?.compress(Bitmap.CompressFormat.JPEG, 90, out3)
                                    //changeProduct!!.imagechanged = Uri.fromFile(myDir).toString()
                                    realm!!.realm.executeTransaction(Realm.Transaction {
                                        product!!.imagechanged = Uri.fromFile(myDir).toString()
                                            product!!.namechanged = response.namechanged
                                            product!!.description = response.description

                                    })

                                    out3.flush()
                                    out3.close()

//                                    putIntenDataBack(false)

                                    UploadImage(response._id!!, file)

                                } catch (e: Exception) {

                                    println(e)

                                }

                            }
                        })

            } else {

                putIntenDataBack(true, response.namechanged, response.description, response.expiretime)

            }

        }
    }

    override fun onSucess(response: JSONObject, type: Int) {
        //*********thong tin của product!!*************

        if (type == 111) {
            // realm!!.deleteProduct(idProduct)
//
//            val jsonInfoProduct = JSONObject(response.getString("info_product!!"))
//            val jsonInfoProductType = JSONObject(jsonInfoProduct.getString("product!!type_id"))
//            if (jsonInfoProduct.isNull("daybefor")) daysbefor = 2
//            else {
//                daysbefor = jsonInfoProduct.getInt("daybefor")
//            }
//
//            if (jsonInfoProduct.isNull("description")) mTvNote.setHint(resources.getString(R.string.note))
//            else {
//                note = jsonInfoProduct.getString("description").toString()
//                mTvNote.setText(note)
//            }
//
//            val strText =
//                    resources.getString(R.string.recieve_one_notification) + " (" + daysbefor + " " + resources.getString(R.string.days) + ")"
//
//            mTvNotification.text = strText
//
//
//            idProduct = jsonInfoProduct.getString("_id")
//            if (jsonInfoProductType.isNull("name")) mTvName.setHint(resources.getString(R.string.no_name)) else mTvName.setText(jsonInfoProductType.getString("name"))
//            if (jsonInfoProductType.isNull("image")) strImProduct = ""
//            else {
//                strImProduct = jsonInfoProductType.getString("image")
//                val options = RequestOptions()
//                        .centerCrop()
//                        .dontAnimate()
//                        .placeholder(R.mipmap.ic_launcher)
//                        .priority(Priority.HIGH)
//                Glide.with(this@DetailProductActivity)
//                        .load( strImProduct)
//                        .thumbnail(0.1f)
//                        .apply(options)
//                        .into(mImProduct)
//            }
//            if (jsonInfoProduct.isNull("namechanged")) null else {
//                name = jsonInfoProduct.getString("namechanged")
//                mTvName.setText(name)
//            }
//            if (jsonInfoProduct.isNull("imagechanged")) null else {
//                strImProduct = jsonInfoProduct.getString("imagechanged")
//                val options = RequestOptions()
//                        .centerCrop()
//                        .dontAnimate()
//                        .placeholder(R.mipmap.ic_launcher_round)
//                        .priority(Priority.HIGH)
//                Glide.with(this@DetailProductActivity)
//                        .load( strImProduct)
//                        .thumbnail(0.1f)
//                        .apply(options)
//                        .into(mImProduct)
//            }
//
//            //  mTvBarcode.text = response.getString("barcode")
//
//            var days = 0
//            if (jsonInfoProduct.isNull("expiretime")) days = 0
//            else {
//
//                expiredTime = jsonInfoProduct.getString("expiretime")
//                var longExpiredTime = expiredTime!!.toLong()
//
//                calendar.timeInMillis = longExpiredTime
//                val date = Date(longExpiredTime)
//                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//                tv_expiredtime.text = dateFormat.format(date)
//                days = daysBetween(System.currentTimeMillis(), expiredTime!!.toLong())
//            }
//            var delete = false
//            if (jsonInfoProduct.isNull("delete")) delete = false
//            else {
//
//                delete = jsonInfoProduct.getBoolean("delete")
//            }
//            getWarningStatus(days)
//

            //   realm!!.updateProduct(product!!)
            //********* UPDATE THÀNH CÔNG *************
        }

//********* XÓA SẢN PHẨM THÀNH CÔNG *************
        else if (type == 222) {
            var item = realm!!.getProduct(idProduct)
            if (item!!.imagechanged !=null) {
                val fdelete = File(item!!.imagechanged)
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        System.out.println("file Deleted :")
                    } else {
                        System.out.println("file not Deleted :")
                    }
                }
            }
            realm!!.deleteProduct(idProduct)
           deleteProduct(true)
        }

    }

    override fun onError(typeError: Int) {
        // thông tin sản phẩm
        if (typeError == 111) {

            //getDataFromRealm()


        } else
        // xóa sản phẩm khong thanh cong tu server
            if (typeError == 222) {
                realm!!.realm.executeTransaction(Realm.Transaction {
                    product!!.delete = true
                    product!!._id = idProduct
                })

                realm!!.updateProduct(product!!)

            } else
            // update thông tin: image, name, note, expiredtime
                if (typeError == 333) {
                    note = mTvNote.text.toString()
                    name = mTvName.text.toString()
                    var days = 0
                    if (expiredTimeChange != null) {
                        expiredTime = expiredTimeChange
                        var longExpiredTime = expiredTime!!.toLong()
                        calendar.timeInMillis = longExpiredTime
                        val date = Date(expiredTime!!.toLong())
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        mTvExpiredtime.text = dateFormat.format(date)
                        days = daysBetween(System.currentTimeMillis(), longExpiredTime)
                        getWarningStatus(days)
                    }
                    updateToRealm(note!!, name!!)
                }
    }

    //=========== LẤY DỮ LIỆU OFFLINE ----------------
    fun getDataFromRealm() {

        //  var product!!Type = product!!.product!!TypeId
        if (product!!.daybefore == 0) daysbefor = 2
        else {
            daysbefor = product!!.daybefore
        }
        if (TextUtils.isEmpty(product!!.description)) mTvNote.setHint(resources.getString(R.string.note))
        else {
            mTvNote.setText(product!!.description.toString())
        }

        val strText =
                resources.getString(R.string.recieve_one_notification) + " (" + daysbefor + " " + resources.getString(R.string.days) + ")"

        mTvNotification.text = strText
        note = product!!.description
        name = product!!.namechanged
        expiredTime = product!!.expiretime.toString()

        idProduct = product!!._id!!
        if (TextUtils.isEmpty(product!!.namechanged)) mTvName.setHint(resources.getString(R.string.no_name))
        else mTvName.setText(product!!.namechanged)
        if (TextUtils.isEmpty(product!!.imagechanged))     Mylog.d("tttttttt image before khong có image")
        else {
            strImProduct = product!!.imagechanged.toString()
            Mylog.d("tttttttt image before: " + strImProduct)
            val options = RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.drawable.photo_unvailable)
                    .priority(Priority.HIGH)
            Glide.with(this@DetailProductActivity)
                    .load(strImProduct)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(mImProduct)


        }

        mTvBarcode.text = product!!.producttype_id!!.barcode

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
            mTvStatus.text = fromHtml(resources.getString(R.string.detail_product_text_expiry_date) + " " + txt + " " +
                    resources.getString(R.string.detail_product_text_day)
            )
        } else if (days < 0) {
            // danger
            txt = "<font color ='#FF4081'> " + Math.abs(days )+ "</font>"
            setColorForLeverWarning(R.drawable.roundedtext_grey, R.drawable.roundedtext_grey, R.drawable.roundedtext_red)
            mImStatus.setBackgroundColor(resources.getColor(R.color.red))
            val strDayCountDown = resources.getString(R.string.shelf_life) + " " + days + " " + resources.getString(R.string.detail_product_text_day)
            mTvDayCountDown.text = strDayCountDown
            mTvDayCountDown.setTextColor(resources.getColor(R.color.red))
            mTvStatus.text = fromHtml(resources.getString(R.string.detail_product_text_expiry_date) + " " + txt + " " +
                    resources.getString(R.string.detail_product_text_day)
            )
        } else if(days ==0){
            txt = "<font color ='#fc9a1b'> " + days + "</font>"
            setColorForLeverWarning(R.drawable.roundedtext_grey, R.drawable.roundedtext_orange, R.drawable.roundedtext_grey)
            mImStatus.setBackgroundColor(resources.getColor(R.color.orange))
            val strDayCountDown = resources.getString(R.string.shelf_life) + " " + resources.getString(R.string.today) + " " + resources.getString(R.string.detail_product_text_day)
            mTvDayCountDown.text = strDayCountDown
            mTvDayCountDown.setTextColor(resources.getColor(R.color.orange))
            mTvStatus.text = fromHtml(resources.getString(R.string.detail_product_text_expiry_date) + " " + resources.getString(R.string.today) + " " +
                    resources.getString(R.string.detail_product_text_day)
            )
        }
        else if (days >= 10) {
            // safe: an toan
            txt = "<font color ='#19a5f5' size = '50'> " + days + "</font>"
            setColorForLeverWarning(R.drawable.roundedtext_blue, R.drawable.roundedtext_grey, R.drawable.roundedtext_grey)
            mImStatus.setBackgroundColor(resources.getColor(R.color.blue))
            val strDayCountDown = resources.getString(R.string.shelf_life) + " " + days + " " + resources.getString(R.string.detail_product_text_day)
            mTvDayCountDown.text = strDayCountDown
            mTvDayCountDown.setTextColor(resources.getColor(R.color.blue))

        }


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
            val cursor = context.getContentResolver().query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA),
                    null, null, null)
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


    fun showDialogCustomDay() {


        var mDate = ""
        var datedialog = AlertDialog.Builder(this)
        var mView: View = View.inflate(this, R.layout.dialog_timepicker, null)
        datedialog.setView(mView)
        var datepicker = mView.datepicker
        var txtout = mView.txt_out
        var txtok = mView.txt_ok
        val calendarChange = calendar

        datepicker.init(calendarChange.get(Calendar.YEAR), calendarChange.get(Calendar.MONTH), calendarChange.get(Calendar.DAY_OF_MONTH),
                DatePicker.OnDateChangedListener { datePicker, year, month, day ->
                    mDate = day.toString() + "/" + (month + 1) + "/" + year
                    calendarChange.set(year, month, day)
                })
        txtout.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mDate = ""
                mDialog?.dismiss()
            }

        })
        txtok.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (mDate != "") {

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

        })
        dialog_ok.setOnClickListener({
            if (ConnectivityChangeReceiver.isConnected()) {
                var user = realm!!.getUser()!!

                presenter.processDeleteProduct(idProduct, user._id!!)

            } else {

                var product_v = realm!!.getProduct(idProduct)

                realm!!.realm.executeTransaction(Realm.Transaction {
                    product_v!!.delete = true
                    product_v.isSyn = false
                })
                realm!!.updateProduct(product_v!!)
                deleteProduct(true)
            }

//            val intent = Intent()
//            intent.putExtra(AppIntent.DATA_UPDATE_ITEM, position)
//            intent.putExtra("product_v", product_v)
//            intent.putExtra("position", position)
//            setResult(AppIntent.RESULT_DELETE_ITEM, intent)

            dialog.dismiss()
            finish()

        })
        dialog.show()
    }

    fun deleteProduct(isSync: Boolean) {

        if (!isSync){
            var notification: Notification = realm!!.getOneNotification(idProduct)!!
            realm!!.realm.executeTransaction(Realm.Transaction {
                notification.delete = true
                notification.isSync = isSync
            })
        }else{
            realm!!.deleteNotification(idProduct)
        }

        val a = Intent()
        a.putExtra("deleteItem", true)
        a.action = AppIntent.ACTION_UPDATE_ITEM

        if (!checkNotification) {

            a.putExtra("reloadItem", true)
            Mylog.d("aaaaaaaaa home ")
        } else {

            a.putExtra("reloadItem", false)
            Mylog.d("aaaaaaaaa notification ")
        }

        a.putExtra("position", position!!)
        a.putExtra("id_product", idProduct)
        sendBroadcast(a)
        finish()

    }

    // không cho phép con trỏ xuất hiện
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
                        var productImage = response.product


                        putIntenDataBack(true, productImage!!.namechanged, productImage.description, productImage.expiretime)


                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}