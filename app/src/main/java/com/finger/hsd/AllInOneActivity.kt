package com.finger.hsd

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
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
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.finger.hsd.activity.ContinuousCaptureActivity
import com.finger.hsd.adapters.ViewPagerAdapter
import com.finger.hsd.common.MyApplication
import com.finger.hsd.fragment.FragmentProfile
import com.finger.hsd.fragment.Home_Fragment
import com.finger.hsd.fragment.NotificationFragment
import com.finger.hsd.fragment.NotificationFragment.NotificationBadgeListener
import com.finger.hsd.util.ConnectivityChangeReceiver
import com.finger.hsd.view.BadgeView
import java.io.IOException
import java.net.Socket

class AllInOneActivity : BaseActivity(), NotificationBadgeListener, ConnectivityChangeReceiver.ConnectivityReceiverListener{
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            showToast("CO MANG")
        } else {
            showToast(" KO CO MANG")
        }
    }

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
    private var mSocket: Socket? = null
    private var menuItemNotification: MenuItem? = null
    val menusetting: MenuItem? = null
    private val perID = 1001
    internal var fabCreate: FloatingActionButton? = null
    private val tabLayout: TabLayout? = null
    private var mLocationManager: LocationManager? = null
    private var toolbar: Toolbar? = null
    private val check = false
    var adapter:FragmentPagerAdapter? = null
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
        mBadgeView!!.setTextSize(10f)
        mBadgeView!!.setText(formatBadgeNumber(10))
        return view
    }
    override fun onBadgeUpdate(value: Int) {
        mBadgeView!!.text = formatBadgeNumber(value)
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
//        when (postion) {
//            0 ->
//                // Its visible
//                fabCreate!!.setVisibility(View.VISIBLE)
//                //                    menusetting.getActionView().setVisibility(View.GONE);
//
//            1 ->
//                if (toolbar!!.getVisibility() == View.VISIBLE) {
//                // Its visible
//                fabCreate!!.setVisibility(View.VISIBLE)
//
//            } else {
//                fabCreate!!.setVisibility(View.VISIBLE)
//                toolbar!!.setVisibility(View.VISIBLE)
//
//            }
//            2 -> {
//                toolbar!!.setVisibility(View.VISIBLE)
//
//                fabCreate!!.setVisibility(View.GONE)
//
//            }
//
//        }
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

}