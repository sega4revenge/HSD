package com.finger.hsd

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.androidnetworking.error.ANError
import com.finger.hsd.activity.ContinuousCaptureActivity
import com.finger.hsd.common.MyApplication
import com.finger.hsd.fragment.FragmentProfile
import com.finger.hsd.fragment.Home_Fragment
import com.finger.hsd.fragment.NotificationFragment
import com.finger.hsd.fragment.NotificationFragment.NotificationBadgeListener
import com.finger.hsd.library.CompressImage
import com.finger.hsd.manager.RealmController
import com.finger.hsd.manager.SessionManager
import com.finger.hsd.model.Notification
import com.finger.hsd.model.Product_v
import com.finger.hsd.model.Response
import com.finger.hsd.model.User
import com.finger.hsd.presenter.SyncPresenter
import com.finger.hsd.util.ConnectivityChangeReceiver
import com.finger.hsd.util.Constants
import com.finger.hsd.util.Mylog
import com.finger.hsd.view.BadgeView
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_all_in_one.*
import me.leolin.shortcutbadger.ShortcutBadger
import org.json.JSONObject
import java.io.File
import java.io.IOException

class AllInOneActivity : BaseActivity(), NotificationBadgeListener, ConnectivityChangeReceiver.ConnectivityReceiverListener, SyncPresenter.ISyncPresenter{

    override fun onResume() {
        super.onResume()
        MyApplication.getConnectivityListener(this)
    }

    internal var bottomNavigationView: BottomNavigationView? = null
    internal var viewPager: ViewPager? = null
     var   mBadgeView: BadgeView? = null
    internal var prevMenuItem: MenuItem? = null;
    internal var menuItem:MenuItem? = null
    internal var edit_search: EditText? = null
    internal var mcoutdowntime: CountDownTimer? = null
    internal var searchkey = ""
    internal var mImageview: ImageView? = null
    internal var scan_barcode_img:ImageView? = null

    private val perID = 1001
    internal var fabCreate: FloatingActionButton? = null
    private val tabLayout: TabLayout? = null

    var adapter:FragmentPagerAdapter? = null
    var session : SessionManager? = null
    var presenter: SyncPresenter? = null
    var realm: RealmController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_in_one)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }
        presenter = SyncPresenter(this)
        realm = RealmController(this)
        session = SessionManager(this)
        viewPager = findViewById(R.id.viewpager)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        mImageview = findViewById<ImageView>(R.id.img_selete)
        edit_search = findViewById<View>(R.id.edit_search) as EditText
        scan_barcode_img = findViewById<ImageView>(R.id.scan_barcode_img)
        scan_barcode_img?.setOnClickListener(View.OnClickListener {
            try {
                if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), perID)
                    //return@OnClickListener
                }else{
                    if (ConnectivityChangeReceiver.isConnected()) {
                        val i = Intent(this@AllInOneActivity, ContinuousCaptureActivity::class.java)
                        startActivity(i)
                    } else {
                        Toast.makeText(this@AllInOneActivity, "Không thể kết nối mạng", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (ie: IOException) {
                Log.e("CAMERA SOURCE", ie.message)
            }

        })
//        showProgress()
        edit_search?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (searchkey !== "" && mImageview?.getDrawable() !== resources.getDrawable(R.drawable.ic_clear_black_24dp)) {
                    mImageview?.setImageResource(R.drawable.ic_clear_black_24dp)
                }
                searchkey = s.toString()
                if (mcoutdowntime != null) {
                    mcoutdowntime?.cancel()
                }
                mcoutdowntime = object : CountDownTimer(2000, 2000) {
                    override fun onTick(millisUntilFinished: Long) {

                    }

                    override fun onFinish() {
                        if (viewPager?.getCurrentItem() != 0) {
                            viewPager?.setCurrentItem(0)
                        }
                        adapter?.notifyDataSetChanged()
                    }
                }.start()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        mImageview?.setOnClickListener(View.OnClickListener {
            edit_search?.setText("")
            searchkey = ""
        })

        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        fabCreate = findViewById(R.id.fab_fixer)

        fabCreate!!.setOnClickListener(View.OnClickListener {
            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), perID)
                //return@OnClickListener
            }else{
                if (ConnectivityChangeReceiver.isConnected()) {
                    val i = Intent(this@AllInOneActivity, ContinuousCaptureActivity::class.java)
                    startActivity(i)
                } else {
                    Toast.makeText(this@AllInOneActivity, "Không thể kết nối mạng", Toast.LENGTH_SHORT).show()
                }
            }

        })

        val bottomNavigationMenuView = bottomNavigationView!!.getChildAt(0) as BottomNavigationMenuView
        val v = bottomNavigationMenuView.getChildAt(1)
        val itemView = v as BottomNavigationItemView
        itemView.addView(getTabItemView())

        bottomNavigationView!!.setOnNavigationItemSelectedListener(
                BottomNavigationView.OnNavigationItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.item_home -> viewPager!!.setCurrentItem(0)
                        R.id.item_notification -> viewPager!!.setCurrentItem(1)
                        R.id.item_profile -> viewPager!!.setCurrentItem(2)



                    }
                    //                                    viewPager.setCurrentItem(3);
                    //                                    mBadgeView.setText(formatBadgeNumber(0));
                    //                                    ShortcutBadger.removeCount(MainActivity.this);

                    false
                })




        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null) {
                    prevMenuItem!!.setChecked(false)
                } else {
                    bottomNavigationView!!.getMenu().getItem(0).isChecked = false
                }
                bottomNavigationView!!.getMenu().getItem(position).isChecked = true
                prevMenuItem = bottomNavigationView!!.getMenu().getItem(position)
                menuVisible(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        setupViewPager(viewPager!!)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItemPosition(`object`: Any): Int {
                if (`object` is Home_Fragment) {
                    `object`.searchKey(searchkey)
                }
                return PagerAdapter.POSITION_UNCHANGED
            }

            override fun getItem(position: Int): Fragment? {
                when (position) {
                    0 // Fragment # 0 - This will show FirstFragment
                    -> return Home_Fragment()
                    1 // Fragment # 0 - This will show FirstFragment different title
                    -> return NotificationFragment()
                    2 // Fragment # 1 - This will show SecondFragment
                    -> return FragmentProfile()
                    else -> return null
                }
            }

            override fun getCount(): Int {
                return 3
            }
        };
//                ViewPagerAdapter(supportFragmentManager)
//        adapter?.addFragment(Home_Fragment().newInstance("trangchu"))
//        adapter?.addFragment(NotificationFragment().newInstance("notification"))
//        adapter?.addFragment(FragmentProfile().newInstance("profile"))

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
    }

    // get tabItemView
    fun getTabItemView(): View {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_badge_view, null)
        val target = view.findViewById<View>(R.id.badgeview_target)
        mBadgeView = BadgeView(this)
        mBadgeView!!.setTargetView(target)
        mBadgeView!!.badgeGravity = (Gravity.RIGHT)
        mBadgeView!!.setTextSize(8f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBadgeView!!.setBackground(getDrawable(R.drawable.button_radius_red))
            }
        }
        mBadgeView!!.setText(formatBadgeNumber(realm!!.countNotification()))
        return view
    }
    override fun onBadgeUpdate(value: Int) {

        mBadgeView!!.text = formatBadgeNumber(realm!!.countNotification())
    }
    fun formatBadgeNumber(value: Int): String? {
        if (value <= 0) {
            return null
        }

        return if (value < 100) {
            // equivalent to String#valueOf(int);
            Integer.toString(value)
        } else "99+"

        // my own policy
    }

    fun menuVisible(postion: Int) {
        //        check = true;
        when (postion) {

            1 ->{
                session!!.setCountNotification(0)
                badgeIconScreen()
            }


        }
  }
    fun badgeIconScreen() {
        var badgeCount = 0

        if (session!!.getCountNotification() > 0) {
            try {
                badgeCount = session!!.getCountNotification()
            } catch (e: NumberFormatException) {
                Mylog.d("badge Count screen: ", e.message!!)
            }
            ShortcutBadger.applyCount(applicationContext, badgeCount)
        } else {
            ShortcutBadger.removeCount(applicationContext)
        }

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
                        val i = Intent(this, ContinuousCaptureActivity::class.java)
                        startActivity(i)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    var listProduct : ArrayList<Product_v>? = null
    var listNotification : ArrayList<Notification>? = null
    var temp: Int = 0
    var indexNotification : Int = 0
    var user : User? = null

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        if (isConnected) {
            if(!session!!.isSync()) {
                Mylog.d("yyyyyyyy chạy đồng bộ chưa:?? rồi")
                refresh()
                session!!.setIsSync(true)
                listProduct = realm!!.getListProductNotSync()
                user = realm!!.getUser()
                temp = 0
                indexNotification = 0
                syncProduct()

            }

        } else {
            Mylog.d("yyyyyyyyyy chạy đồng bộ chưa:?? chưa")
        }
    }

    fun syncProduct(){
        if (listProduct != null && !listProduct!!.isEmpty() && temp < listProduct!!.size) {
            if(listProduct!![temp].delete) {
                presenter!!.processDeleteProduct(listProduct!![temp]._id!!, user!!._id!!)
            }else{
                presenter!!.updateProduct(listProduct!![temp])
            }

        } else {
            listNotification = realm!!.getListNotificationNotSync()
            syncNotification()
        }
    }

    fun syncNotification(){
        if (listNotification != null && !listNotification!!.isEmpty() && temp < listProduct!!.size) {
            if(listNotification!![temp].delete) {
                realm!!.deleteNotification(listNotification!![temp].id_product!!)
                indexNotification++
                syncNotification()
            }else{
                presenter!!.updateNotficationOnServer(listNotification!![indexNotification], user!!._id!!)
            }
        }else{
            completeRefresh()
            session!!.setIsSync(false)

        }
    }

    override fun onSucess(response: JSONObject, type: Int) {
        if(type == 111){
            // delete product success
            var idProduct = listProduct!![temp]._id!!
            var item = realm!!.getProduct(idProduct)
            if (item!!.imagechanged !=null) {
                val fdelete = File(item.imagechanged)
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        System.out.println("file Deleted :")
                    } else {
                        System.out.println("file not Deleted :")
                    }
                }
            }
            realm!!.deleteProduct(idProduct)
            realm!!.deleteNotification(idProduct)
            var product = realm!!.getProduct(idProduct)
            realm!!.realm.executeTransaction(Realm.Transaction {
                product!!.isSyn = true
            })

            temp++
            syncProduct()

        }else if(type == 222){
            // update product success
            var idProduct = listProduct!![temp]._id!!
            if (listProduct!![temp].isNewImage){

                var file = File( listProduct!![temp].imagechanged!!)
                UploadImage(idProduct, file)

            }else{
                var product = realm!!.getProduct(idProduct)
                realm!!.realm.executeTransaction(Realm.Transaction {
                    product!!.isSyn = true
                })
                temp++
                syncProduct()
            }
        }else if (type == 333){
            var  notification = listNotification!![indexNotification]
            realm!!.realm.executeTransaction(Realm.Transaction {
                notification.isSync = true
                notification.watched = false
            })
            indexNotification++
            syncNotification()

        }
    }

    override fun onError(typeError: Int) {
        completeRefresh()
        if(typeError == 111){
            // delete product fail
            session!!.setIsSync(false)
        }else if(typeError == 222){
            // update product fail
            session!!.setIsSync(false)
        }else if(typeError== 333){
            // update notification fail
           session!!.setIsSync(false)
        }
    }

    fun UploadImage(idProduct: String, file: File?) {

        Rx2AndroidNetworking.upload(Constants.URL_UPDATE_IMAGE)
                .addMultipartParameter("id_product", idProduct)
                .addMultipartFile("image", CompressImage.compressImage(this, file))
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
                        temp++
                        syncProduct()
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

    //sync animation
    fun completeRefresh() {
        Mylog.d("yyyyy load xong")
        sync.clearAnimation()
        sync.setImageDrawable(resources.getDrawable(R.drawable.ic_sync_complete))
       // refreshItem.setActionView(null)
    }
    fun refresh() {
        /* Attach a rotating ImageView to the refresh item as an ActionView */

        Mylog.d("yyyyyyyy load chưa")
        val rotation = AnimationUtils.loadAnimation(this, R.anim.sync_animation)
        rotation.repeatCount = Animation.INFINITE
        sync.startAnimation(rotation)


        //TODO trigger loading
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter!!.cancelRequest()
    }
}