package com.finger.hsd

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
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
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
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
import me.leolin.shortcutbadger.ShortcutBadger
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class AllInOneActivity : BaseActivity(), NotificationBadgeListener, ConnectivityChangeReceiver.ConnectivityReceiverListener, SyncPresenter.ISyncPresenter{

    override fun onResume() {
        super.onResume()
        MyApplication.getConnectivityListener(this)
        onBadgeUpdate(session!!.getCountNotification())
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    internal var bottomNavigationView: BottomNavigationView? = null
    internal var viewPager: ViewPager? = null
     var   mBadgeView: BadgeView? = null
    internal var prevMenuItem: MenuItem? = null;

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
    var lin:RelativeLayout? = null
    var sync: ImageView? = null

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
        lin = findViewById(R.id.actionbar)
        lin?.requestFocus()
        viewPager = findViewById(R.id.viewpager)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        mImageview = findViewById(R.id.img_selete)
        edit_search = findViewById<View>(R.id.edit_search) as EditText
        scan_barcode_img = findViewById(R.id.scan_barcode_img)
        sync= findViewById(R.id.sync)
        scan_barcode_img?.setOnClickListener {
            try {
                if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), perID)
                    //return@OnClickListener
                }else{
                    if (ConnectivityChangeReceiver.isConnected()) {
                        val i = Intent(this@AllInOneActivity, ContinuousCaptureActivity::class.java)
                        startActivity(i)
                    } else {
                        showToast(R.string.not_connect_to_network)                    }
                }

            } catch (ie: IOException) {
                Log.e("CAMERA SOURCE", ie.message)
            }

        }
//        refresh()
//        showProgress()

        try {
            val info = packageManager.getPackageInfo(
                    "ycom.finger.hsd",
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }


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
            if(edit_search?.text.toString() == ""){
                mImageview?.setImageResource(R.drawable.search_icon_black_24dp)
                lin?.requestFocus()
                val view = this.currentFocus
                if (view != null) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }else{
                edit_search?.setText("")
                searchkey = ""
            }

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
                 showToast(R.string.not_connect_to_network)
                }
            }

        })

        val bottomNavigationMenuView = bottomNavigationView!!.getChildAt(0) as BottomNavigationMenuView
        val v = bottomNavigationMenuView.getChildAt(1)
        val itemView = v as BottomNavigationItemView
        itemView.addView(getTabItemView())

        bottomNavigationView!!.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_home -> viewPager!!.setCurrentItem(0)
                R.id.item_notification ->{
                    viewPager!!.setCurrentItem(1)
                }
                R.id.item_profile -> viewPager!!.setCurrentItem(2)
            }

            false
        }




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


        onNetworkConnectionChanged(ConnectivityChangeReceiver.isConnected())
    }


    private fun setupViewPager(viewPager: ViewPager) {
        adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItemPosition(item: Any): Int {
                if (item is Home_Fragment) {
                    item.searchKey(searchkey)
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
                mBadgeView!!.background = getDrawable(R.drawable.button_radius_red)
            }
        }
        mBadgeView!!.text = formatBadgeNumber(session!!.getCountNotification())
        return view
    }
    override fun onBadgeUpdate(value: Int) {

        mBadgeView!!.text = formatBadgeNumber(session!!.getCountNotification())

    }
    fun formatBadgeNumber(value: Int): String? {

        if (value <= 0) {
            return null
        }
        if (value < 100) {
            // equivalent to String#valueOf(int);
            return Integer.toString(value)
        } else return "99+"

        // my own policy
    }

    fun menuVisible(postion: Int) {
        //        check = true;
        when (postion) {

            1 ->{
                session!!.setCountNotification(0)
                badgeIconScreen()
              onBadgeUpdate(session!!.getCountNotification())
            }


        }
  }
    fun badgeIconScreen() {
        val badgeCount = session!!.getCountNotification()
//        ShortcutBadger.applyCount(applicationContext, badgeCount)
        if (session!!.getCountNotification() > 0) {
            try {
//                badgeCount = session!!.getCountNotification()
            } catch (e: NumberFormatException) {
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
                refresh()

                listProduct = realm!!.getListProductNotSync()
                user = realm!!.getUser()
                temp = 0
                indexNotification = 0
                syncProduct()


        } else {

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


        }
    }

    override fun onSucess(response: JSONObject, type: Int) {
        if(type == 111){
            // delete product success
            if(temp < listProduct!!.size) {
                val idProduct = listProduct!![temp]._id!!
                val item = realm!!.getProduct(idProduct)
                if (item!!.imagechanged != null) {
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

            }
            temp++
            syncProduct()

        }else if(type == 222){
            // update product success
            val idProduct = listProduct!![temp]._id!!
            if (listProduct!![temp].isNewImage){
                val uri = Uri.parse(listProduct!![temp].imagechanged!!)
                val file = File(uri.path)
                UploadImage(idProduct, file)

            }else{
                val product = realm!!.getProduct(idProduct)
                realm!!.realm.executeTransaction {
                    product!!.isSyn = true
                }
                temp++
                syncProduct()
            }
        }else if (type == 333){
            val  notification = listNotification!![indexNotification]
            realm!!.realm.executeTransaction {
                notification.isSync = true
                notification.watched = false
            }
            indexNotification++
            syncNotification()

        }
    }

    override fun onError(typeError: Int) {
        completeRefresh()
        if(typeError == 111){
            // delete product fail

        }else if(typeError == 222){
            // update product fail

        }else if(typeError== 333){
            // update notification fail

        }
    }

    fun UploadImage(idProduct: String, file: File?) {



        Rx2AndroidNetworking.upload(Constants.URL_UPDATE_IMAGE)
                .addMultipartParameter("id_product", idProduct)
                .addMultipartFile("image", CompressImage.compressImage(this, file))
                .build()
                .setAnalyticsListener { _, _, _, _ -> }
                .getObjectObservable(Response::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {

                    override fun onError(e: Throwable) {
                        if (e is ANError) {
                            Mylog.d("yyyyyyy uploadimageErr "+e.message!!)
                            val product = realm!!.getProduct(idProduct)
                            realm!!.realm.executeTransaction(Realm.Transaction {
                                product!!.isSyn = false
                                product.isNewImage = true
                            })
                            temp++
                            syncProduct()
                        }
                    }

                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(response: Response) {
                        Mylog.d("yyyyyyy uploadimageSuccess ")
                        val product = realm!!.getProduct(idProduct)
                        realm!!.realm.executeTransaction(Realm.Transaction {
                            product!!.isSyn = true
                            product.isNewImage = false
                        })
                        temp++
                        syncProduct()
                    }
                })
    }

    //sync animation
    fun completeRefresh() {

        sync?.clearAnimation()
        sync?.setImageDrawable(resources.getDrawable(R.drawable.ic_sync_complete))
       // refreshItem.setActionView(null)
    }
    fun refresh() {
        /* Attach a rotating ImageView to the refresh item as an ActionView */
        sync?.setImageDrawable(resources.getDrawable(R.drawable.ic_sync_ing))
        val rotation = AnimationUtils.loadAnimation(this, R.anim.sync_animation)
        rotation.repeatCount = Animation.INFINITE
        sync?.startAnimation(rotation)


        //TODO trigger loading
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter!!.cancelRequest()
    }
}