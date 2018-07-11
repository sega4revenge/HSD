package com.finger.hsd.view;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.finger.hsd.R;
import com.finger.hsd.util.AppIntent;

import java.util.ArrayList;

/**
 * Created by Duong on 11/15/2017.
 */

public class ViewImageProduct extends AppCompatActivity implements View.OnTouchListener {
    // We can be in one of these 3 states
    public static final int NONE = 0;
    public static final int DRAG = 1;
    public static final int ZOOM = 2;
    private static final String TAG = "Touch";
    public static PointF mid = new PointF();
    public static int mode = NONE;
    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF start = new PointF();
    float oldDist;
    ArrayList<String> listimage = new ArrayList<>();
    int pos =0;
    private float[] matrixValues = new float[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpaper_image);
        init();
    }

    public void backscreen(View v){
        finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                //Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                //Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    //Log.d(TAG, "mode=ZOOM");
                }
                break;
            case MotionEvent.ACTION_MOVE:
                /*  *//*  if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                }*//*
                else*/
                if (mode == ZOOM) {
                    float newDist = spacing(event);
                    //Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                //Log.d(TAG, "mode=NONE");
                break;
        }
        // Perform the transformation
        view.setImageMatrix(matrix);
        return true; // indicate event was handled
    }

    private float spacing(MotionEvent event) {

        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {

        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    class TouchImageAdapter extends PagerAdapter {
        // int[] image={R.drawable.nature_1};
        @Override
        public int getCount() {

            return listimage.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {

            //Log.d("aaaa", "init: "+ listimage.get(position));
            TouchImageView img = new TouchImageView(container.getContext());
            RequestOptions options =new RequestOptions()
                    .fitCenter()
                    .dontAnimate()
                    .placeholder(R.drawable.photo_unvailable)
                    .error(R.drawable.photo_unvailable)
                    .priority(Priority.HIGH);
            Glide.with(ViewImageProduct.this)
                    .load(listimage.get(0).toString())
                    .thumbnail(0.4f)
                    .apply(options)
                    .into(img);


            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {

            return view == object;
        }
    }

    public void init(){

        ExtendedViewPager mViewPager =  findViewById(R.id.view_pager);
        TextView tvName = findViewById(R.id.tv_name);
//        Bundle extras = getIntent().getExtras();
//        pos = extras.getInt("pos");
//        System.out.println(pos + "ok");
//        listimage = extras.getStringArrayList("data");

        Intent i = getIntent();
        String a = i.getStringExtra(AppIntent.INTENT_IMAGE_VIEW);
        String name = i.getStringExtra(AppIntent.INTENT_NAME_PRODUCT);
        listimage.add(a);
        System.out.println(listimage.get(pos));
        if(name!=null){
            tvName.setText(name);
        }

        mViewPager.setAdapter(new TouchImageAdapter());
        mViewPager.setCurrentItem(pos);
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
    }
}