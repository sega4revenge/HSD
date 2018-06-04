package com.finger.hsd.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.finger.hsd.MyApplication;
import com.finger.hsd.R;
import com.finger.hsd.util.OkHttp3Downloader;
import com.squareup.picasso.Picasso;


public class show_PhotoProduct extends AppCompatActivity  {
    String ret="";
    String path;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show__photo_product);
        img = (ImageView) findViewById(R.id.img_product);
        ImageView img_back = (ImageView) findViewById(R.id.arrow_back);
        Intent i = getIntent();
        int type = i.getIntExtra("type",1);
        String path = i.getStringExtra("path");
        if(type!=0){
           new Picasso.Builder(show_PhotoProduct.this)
                    .downloader(new OkHttp3Downloader(MyApplication.Companion.okhttpclient()))
                    .build().load(path).into(img);
        }else{
            img.setImageBitmap(BitmapFactory.decodeFile(path));
            img_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


    }

}
