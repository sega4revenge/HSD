package com.finger.hsd.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.finger.hsd.BaseActivity;
import com.finger.hsd.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Custom_Camera_Activity extends BaseActivity {
    private Camera mCamera;
    private Camera_Preview mCameraPreview;
    private ImageView img,arrow_back;
    private int perID = 1001;
    private int type = 0;
    private String barcode  = "";

    private String name="",detail="",ex="";
    private long exTime = 0;
    FrameLayout preview;
    private String TAG = "Custom_Camera_Activity";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_custom_scan_layout);

        getData();

        mCamera = getCameraInstance();
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
            case Surface.ROTATION_90: degrees = 90; break; //Landscape left
            case Surface.ROTATION_180: degrees = 180; break;//Upside down
            case Surface.ROTATION_270: degrees = 270; break;//Landscape right
        }
        int rotate = (info.orientation - degrees + 360) % 360;
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size size = sizes.get(0);

        params.setPictureSize(size.width,size.height);
        params.setRotation(rotate);
        mCamera.setParameters(params);


        mCameraPreview = new Camera_Preview(this, mCamera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        arrow_back = (ImageView) findViewById(R.id.arrow_back);
        preview.addView(mCameraPreview);


        LinearLayout captureButton = (LinearLayout) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (ActivityCompat.checkSelfPermission(Custom_Camera_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Custom_Camera_Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, perID);
                        return ;
                    }
                  showProgress();
                  mCamera.takePicture(null, null, mPicture);
                }catch (Exception e){
                    Log.d("error",e.getMessage());
                }

            }
        });
        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private int findFrontFacingCameraID() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
    private void getData() {
        Intent i = getIntent();
        type = i.getIntExtra("type",1);
        if(type==1){
            barcode = i.getStringExtra("barcode");
        }else if(type==1001){
            barcode = i.getStringExtra("barcode");
            name = i.getStringExtra("name");
            ex = i.getStringExtra("ex");
            exTime = i.getLongExtra("exTime",20);
            detail = i.getStringExtra("detail");
        }
    }


    /** A safe way to get an instance of the Camera object. */
    public  Camera getCameraInstance(){
        Camera c = null;
//        boolean cameraAccessible = false;
//        while (!cameraAccessible) {
//            try {
//                c = Camera.open(); // attempt to get a Camera instance
//                cameraAccessible = true;
//            }
//            catch (Exception e){
//                Log.v(TAG, "Camera not yet accessible");
//            }
//        }
//        c.release();
//        c = Camera.open();
//        return c;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            Log.d("IMAGEEEEEEEEEEEEEEE",e.getMessage()+"////messss");
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            boolean check = false;
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                check = true;
            } catch (FileNotFoundException e) {
                Log.d("aaaaaaaaaaaaaaa",e.getMessage());
             hideProgress();

            } catch (IOException e) {
                Log.d("aaaaaaaaaaaaaaa222222",e.getMessage());
             hideProgress();
            }
            if(check){
//                mRetrofitService = ApiUtils.getAPI();
//                try {
//                    RequestBody requestFile =
//                            RequestBody.create(MediaType.parse("multipart/form-data"), CompressImage.INSTANCE.compressImage(pictureFile,getApplicationContext()));
//
//                    MultipartBody.Part body =
//                            MultipartBody.Part.createFormData("image", pictureFile.getName(), requestFile);
//
//                    RequestBody id =
//                            RequestBody.create(MediaType.parse("multipart/form-data"), "5a1243b2cc58275299fda373");
//                    RequestBody oldavata =
//                            RequestBody.create(MediaType.parse("multipart/form-data"), "upload_c14e03646c50fd5e01f0d9ef4ccd7b94.jpg");
//
//                    mRetrofitService.updateImage(id,oldavata, body).enqueue(new Callback<sendAddProduct>() {
//                        @Override
//                        public void onResponse(Call<sendAddProduct> call, Response<sendAddProduct> response) {
//                            if (response.isSuccessful()) {
//                                Log.d(TAG, response.body() +"//"+ response.code());
//                            } else {
//                                Log.d(TAG, "////////" + response.message() + "//" + response.code());
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<sendAddProduct> call, Throwable t) {
//
//                        }
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                Intent i = new Intent(Custom_Camera_Activity.this,Add_Product.class);
                i.putExtra("path",pictureFile.getAbsolutePath());
                if(type==1){
                    i.putExtra("type",1);
                    i.putExtra("barcode",barcode);
                }else if(type==1001){
                    i.putExtra("type",3);
                    i.putExtra("name",name);
                    i.putExtra("ex",ex);
                    i.putExtra("exTime",exTime);
                    i.putExtra("detail",detail);
                    i.putExtra("barcode",barcode);
                    i.putExtra("path",pictureFile.getAbsolutePath());
                }else{
                    i.putExtra("type",0);
                }
             hideProgress();
                startActivity(i);
            }

        }
    };
//    private void showDialog(){
//       MaterialDialog.Builder m = new MaterialDialog.Builder(this)
//                .content("Please wait...")
//                .cancelable(false)
//                .progress(true, 0);
//        mDialogProgress = m.show();
//    }
    @Override
    protected void onResume() {
        super.onResume();
        hideProgress();
//        if(mDialogProgress != null) {
//            if (mDialogProgress.isShowing()) {
//                mDialogProgress.dismiss();
//            }
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private  File getOutputMediaFile() {
        File mediaStorageDir = this.getExternalFilesDir(null);
        //   if (!mediaStorageDir.exists()) {
        //    if (!mediaStorageDir.mkdirs()) {
        //        Log.d("Custom_Camera_Activity", "failed to create directory");
        //        return null;
        //     }
        //  }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

}