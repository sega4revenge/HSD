package com.finger.hsd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.facebook.FacebookSdk;
import com.finger.hsd.BaseActivity;
import com.finger.hsd.R;

import com.finger.hsd.common.MyApplication;
import com.finger.hsd.fragment.FragmentProfile;
import com.finger.hsd.fragment.Home_Fragment;
import com.finger.hsd.fragment.NotificationFragment;
import com.finger.hsd.library.NavigationTabBar;
import com.finger.hsd.util.ConnectivityChangeReceiver;
import com.finger.hsd.manager.RealmController;
import com.finger.hsd.util.SessionManager;

import java.util.ArrayList;

public class HorizontalNtbActivity extends BaseActivity implements NotificationFragment.NotificationBadgeListener,ConnectivityChangeReceiver.ConnectivityReceiverListener{


    RealmController realm;
     NavigationTabBar navigationTabBar;
     SessionManager session;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        realm = new RealmController(this);
        session = new SessionManager(this);
        initUI();
    }

    private void initUI() {
        FloatingActionButton fb = findViewById(R.id.fab);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HorizontalNtbActivity.this,Scanner_Barcode_Activity.class);
                startActivity(i);
            }
        });
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0: // Fragment # 0 - This will show FirstFragment
                        return new Home_Fragment();
                    case 1: // Fragment # 0 - This will show FirstFragment different title
                        return new NotificationFragment();
                    case 2: // Fragment # 1 - This will show SecondFragment
                        return new FragmentProfile();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        });


        final String[] colors = getResources().getStringArray(R.array.default_preview);

      navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_home_black_24dp),
                        Color.parseColor(colors[0]))
                      //  .selectedIcon(getResources().getDrawable(R.drawable.ic_sixth))
                        .title("Heart")
                       // .badgeTitle("NTB")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_notifications_active_black_24dp),
                        Color.parseColor(colors[0]))
//                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .title("Cup")
                     //   .badgeTitle("with")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_person_black_24dp),
                        Color.parseColor(colors[2]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_seventh))
                        .title("Profile")
                        .badgeTitle("Profile")
                        .build()
        );
        navigationTabBar.setBgColor(getResources().getColor(R.color.white));
        navigationTabBar.setActiveColor(getResources().getColor(R.color.viewfinder_border));
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
//                navigationTabBar.getModels().get(position).hideBadge();
                session.setCountNotification(0);
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                final NavigationTabBar.Model model = navigationTabBar.getModels().get(1);
                if(realm.countNotification()!=0) {

                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.setBadgeTitle(realm.countNotification()+"");
                            model.showBadge();
                        }
                    }, 1 * 100);
                }else {
                    model.setBadgeTitle(0+"");
                    model.hideBadge();

                }
            }
        }, 500);
    }


    @Override
    public void onBadgeUpdate(final int value) {
        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                final NavigationTabBar.Model model = navigationTabBar.getModels().get(1);
                if(realm.countNotification()!=0) {

                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.setBadgeTitle(value+"");
                            model.showBadge();
                        }
                    }, 1 * 100);
                }else {
                    model.setBadgeTitle(0+"");
                    model.hideBadge();

                }
            }
        }, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.Companion.getConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
    private void showSnack(boolean isConnected) {
        if (isConnected) {

        } else {

        }
        //            Log.d("NetworkConnection","Sorry! Not connected to internet");
//            message = "Sorry! Not connected to internet";
//            color = Color.RED;
//
//        Snackbar snackbar = Snackbar
//                .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);
//
//        View sbView = snackbar.getView();
//        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//        textView.setTextColor(color);
//        snackbar.show();
    }
}
