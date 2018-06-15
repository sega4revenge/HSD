package com.finger.hsd

import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.*
import com.finger.hsd.adapters.ViewPagerAdapter
import com.finger.hsd.fragment.FragmentProfile
import com.finger.hsd.fragment.Home_Fragment
import com.finger.hsd.fragment.NotificationFragment
import com.finger.hsd.fragment.NotificationFragment.NotificationBadgeListener
import com.finger.hsd.view.BadgeView
import java.net.Socket

class AllInOneActivity : BaseActivity(), NotificationBadgeListener{



    internal var bottomNavigationView: BottomNavigationView? = null
    internal var viewPager: ViewPager? = null
     var   mBadgeView: BadgeView? = null
    internal var prevMenuItem: MenuItem? = null;
    internal var menuItem:MenuItem? = null

    private var mSocket: Socket? = null
    private var menuItemNotification: MenuItem? = null
    val menusetting: MenuItem? = null

    internal var fabCreate: FloatingActionButton? = null
    private val tabLayout: TabLayout? = null
    private var mLocationManager: LocationManager? = null
    private var toolbar: Toolbar? = null
    private val check = false
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

        toolbar = findViewById(R.id.toolbar)
//        toolbar.setNavigationIcon(R.drawable.back_arrow);
        viewPager = findViewById(R.id.viewpager)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        setSupportActionBar(toolbar)

        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        fabCreate = findViewById(R.id.fab_fixer)

        fabCreate!!.setOnClickListener(View.OnClickListener {

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
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(Home_Fragment().newInstance("trangchu"))
        adapter.addFragment(NotificationFragment().newInstance("notification"))
        adapter.addFragment(FragmentProfile().newInstance("profile"))

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
        when (postion) {
            0 ->
                // Its visible
                fabCreate!!.setVisibility(View.VISIBLE)
                //                    menusetting.getActionView().setVisibility(View.GONE);

            1 -> if (toolbar!!.getVisibility() == View.VISIBLE) {
                // Its visible
                fabCreate!!.setVisibility(View.VISIBLE)

            } else {
                fabCreate!!.setVisibility(View.VISIBLE)
                toolbar!!.setVisibility(View.VISIBLE)

            }
            2 -> {
                toolbar!!.setVisibility(View.VISIBLE)

                fabCreate!!.setVisibility(View.GONE)

            }

        }
    }


}