package com.finger.hsd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.finger.hsd.R;


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
      //  if(type!=0){
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.drawable.photo_unvailable)
                    .priority(Priority.HIGH);
            Glide.with(this)
                     .load(path)
                    .thumbnail(0.1f)
                    .apply(options)
                    .into(img);

//        }else{
//            img.setImageBitmap(BitmapFactory.decodeFile(path));
//
//        }
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

}
