package com.finger.hsd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.finger.hsd.BaseActivity;
import com.finger.hsd.R;
import com.finger.hsd.common.MyApplication;
import com.finger.hsd.fragment.FragmentProfile;
import com.finger.hsd.fragment.Home_Fragment;
import com.finger.hsd.fragment.NotificationFragment;
import com.finger.hsd.library.NavigationTabBar;
import com.finger.hsd.manager.RealmController;
import com.finger.hsd.util.ConnectivityChangeReceiver;

import java.util.ArrayList;

public class HorizontalNtbActivity extends BaseActivity implements NotificationFragment.NotificationBadgeListener,
        ConnectivityChangeReceiver.ConnectivityReceiverListener{

    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;
    RealmController realm;
     NavigationTabBar navigationTabBar;
    // SessionManager session;
    EditText edit_search;
    CountDownTimer mcoutdowntime;
    String searchkey = "";
     ViewPager viewPager;
     ImageView mImageview,scan_barcode_img;
    FragmentPagerAdapter fragmentPagerAdapter;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        realm = new RealmController(this);
       // session = new SessionManager(this);
        initUI();
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(searchkey!="" && mImageview.getDrawable() != getResources().getDrawable(R.drawable.ic_clear_black_24dp)){
                    mImageview.setImageResource(R.drawable.ic_clear_black_24dp);
                }
                searchkey =s.toString();
                if(mcoutdowntime!=null){
                    mcoutdowntime.cancel();
                }
                mcoutdowntime = new CountDownTimer(2000,2000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        if(viewPager.getCurrentItem()!=0){
                            viewPager.setCurrentItem(0);
                        }
                        fragmentPagerAdapter.notifyDataSetChanged();
                    }
                }.start();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initUI() {
        FloatingActionButton fb = findViewById(R.id.fab);
        mImageview = findViewById(R.id.img_selete);
        scan_barcode_img = findViewById(R.id.scan_barcode_img);
        scan_barcode_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HorizontalNtbActivity.this,ContinuousCaptureActivity.class);
                startActivity(i);
//                IntentIntegrator intentIntegrator =    new IntentIntegrator(HorizontalNtbActivity.this);
//                intentIntegrator.setOrientationLocked(false);
//                intentIntegrator.setCaptureActivity(Scanner_with_google_vision.class);
//                intentIntegrator.setCameraId(0);
//                intentIntegrator.initiateScan();
            }
        });
        mImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // if(mImageview.getDrawable() != getResources().getDrawable(R.drawable.search_icon_black_14dp)){
               //     mImageview.setImageResource(R.drawable.search_icon_black_14dp);
               // }
                edit_search.setText("");
                searchkey="";
            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectivityChangeReceiver.isConnected()){
                    Intent i = new Intent(HorizontalNtbActivity.this,ContinuousCaptureActivity.class);
                  //  startActivityForResult(i,999);
                    startActivity(i);
                }else{
                   Toast.makeText(HorizontalNtbActivity.this,"Không thể kết nối mạng",Toast.LENGTH_SHORT).show();
                }
            }
        });
        edit_search = (EditText) findViewById(R.id.edit_search);
        viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);

        viewPager.setOffscreenPageLimit(3);
        fragmentPagerAdapter =new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getItemPosition(Object object) {
                if(object instanceof Home_Fragment){
                    ((Home_Fragment) object).searchKey(searchkey);
                }
                return POSITION_UNCHANGED;
            }

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
        };
        viewPager.setAdapter(fragmentPagerAdapter);


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
            //    session.setCountNotification(0);
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Toast.makeText(this,requestCode+"///"+resultCode,Toast.LENGTH_LONG).show();
//        if(requestCode==969 && resultCode == 696){
//            int pos = data.getIntExtra("position",1);
//            Toast.makeText(this,pos+"///123",Toast.LENGTH_LONG).show();
//
//        }
////        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
////        if(result.getContents() == null) {
////            if(resultCode==999 && resultCode == 999){
////                Toast.makeText(this, "Scanned TYPE123: " + data.getStringExtra("result"), Toast.LENGTH_LONG).show();
////            }
////        } else {
////                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
////        }
//
//    }
}
